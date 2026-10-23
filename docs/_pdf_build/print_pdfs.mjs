import http from "node:http";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { createRequire } from "node:module";

const require = createRequire(import.meta.url);
const puppeteer = require("puppeteer-core");

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const docsDir = path.resolve(__dirname, "..");
const htmlDir = path.join(__dirname, "html");
const chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

const files = [
  ["README.html", "README.pdf"],
  ["architecture.html", "architecture.pdf"],
  ["database.html", "database.pdf"],
  ["controllers.html", "controllers.pdf"],
];

const mime = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript",
  ".css": "text/css",
};

const server = http.createServer((req, res) => {
  const urlPath = decodeURIComponent(req.url.split("?")[0]);
  const filePath = path.normalize(path.join(htmlDir, urlPath));
  if (!filePath.startsWith(htmlDir)) {
    res.writeHead(403);
    res.end();
    return;
  }
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404);
      res.end("not found");
      return;
    }
    res.writeHead(200, { "Content-Type": mime[path.extname(filePath)] || "text/plain" });
    res.end(data);
  });
});

await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
const { port } = server.address();

const browser = await puppeteer.launch({
  executablePath: chromePath,
  headless: true,
  args: ["--disable-gpu", "--no-sandbox"],
});

try {
  for (const [htmlName, pdfName] of files) {
    const page = await browser.newPage();
    await page.setViewport({ width: 1280, height: 1600, deviceScaleFactor: 2 });
    await page.goto(`http://127.0.0.1:${port}/${htmlName}`, {
      waitUntil: "networkidle2",
      timeout: 120000,
    });
    await page.waitForFunction(
      () => document.body.dataset.ready === "1",
      { timeout: 120000 }
    );
    await new Promise((r) => setTimeout(r, 800));
    const outPath = path.join(docsDir, pdfName);
    await page.pdf({
      path: outPath,
      format: "A4",
      printBackground: true,
      margin: { top: "14mm", bottom: "16mm", left: "12mm", right: "12mm" },
    });
    await page.close();
    console.log("wrote", outPath);
  }
} finally {
  await browser.close();
  server.close();
}
