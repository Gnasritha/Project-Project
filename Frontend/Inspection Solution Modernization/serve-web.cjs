// Static file server for the Flutter web release bundle, with a reverse
// proxy for /api/** so the browser only ever talks to this one origin
// (127.0.0.1:5000). The proxy hop to the backend happens server-side, which
// sidesteps the browser cross-origin/host quirks that hung direct :8980 calls.
const http = require('http');
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, 'build', 'web');
const PORT = 5000;
const HOST = '127.0.0.1'; // IPv4 so http://localhost:5000 / 127.0.0.1:5000 resolve

const BACKEND_HOST = '127.0.0.1';
const BACKEND_PORT = 8980;

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.mjs': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.wasm': 'application/wasm',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.ttf': 'font/ttf',
  '.otf': 'font/otf',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.bin': 'application/octet-stream',
  '.map': 'application/json',
};

// ── Reverse proxy: /api/** -> backend ───────────────────────────────────
function proxyToBackend(req, res) {
  const options = {
    host: BACKEND_HOST,
    port: BACKEND_PORT,
    method: req.method,
    path: req.url, // path + query, forwarded verbatim
    headers: { ...req.headers, host: `${BACKEND_HOST}:${BACKEND_PORT}` },
  };
  const upstream = http.request(options, (upRes) => {
    res.writeHead(upRes.statusCode || 502, upRes.headers);
    upRes.pipe(res);
  });
  upstream.on('error', (err) => {
    console.error(`proxy ${req.method} ${req.url} -> ${err.message}`);
    res.writeHead(502, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ message: 'Backend unreachable: ' + err.message }));
  });
  req.pipe(upstream); // forwards the request body for POST/PUT/PATCH
}

// ── Static file serving ─────────────────────────────────────────────────
function serveStatic(req, res) {
  let urlPath = decodeURIComponent(req.url.split('?')[0]);
  if (urlPath === '/') urlPath = '/index.html';
  let filePath = path.join(ROOT, urlPath);
  if (!filePath.startsWith(ROOT)) {
    res.writeHead(403); res.end('Forbidden'); return;
  }
  fs.stat(filePath, (err, stat) => {
    if (err || !stat.isFile()) {
      filePath = path.join(ROOT, 'index.html'); // SPA fallback
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream' });
    fs.createReadStream(filePath).pipe(res);
  });
}

const server = http.createServer((req, res) => {
  if (req.url.startsWith('/api/')) {
    proxyToBackend(req, res);
  } else {
    serveStatic(req, res);
  }
});

server.listen(PORT, HOST, () => {
  console.log(`Serving ${ROOT}`);
  console.log(`Proxying /api/** -> http://${BACKEND_HOST}:${BACKEND_PORT}`);
  console.log(`Momthathel app ready: http://localhost:${PORT}/`);
});
