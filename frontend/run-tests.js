const http = require('http');
const fs = require('fs');
const path = require('path');
const Mocha = require('mocha');
const { generateExcelReport } = require('./test/excel-reporter');

const PORT = 8080;
const PUBLIC_DIR = __dirname;

// MIME types helper for static server
const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'text/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml'
};

function startServer() {
    return new Promise((resolve) => {
        const server = http.createServer((req, res) => {
            let filePath = path.join(PUBLIC_DIR, req.url === '/' ? 'index.html' : req.url.split('?')[0]);
            const ext = path.extname(filePath).toLowerCase();
            const contentType = MIME_TYPES[ext] || 'application/octet-stream';

            fs.readFile(filePath, (err, content) => {
                if (err) {
                    res.writeHead(404, { 'Content-Type': 'text/plain' });
                    res.end('404 Not Found');
                } else {
                    res.writeHead(200, { 'Content-Type': contentType });
                    res.end(content, 'utf-8');
                }
            });
        });

        server.listen(PORT, () => {
            console.log(`🌐 Local test web server running at http://localhost:${PORT}`);
            resolve(server);
        });
    });
}

async function runE2ETests() {
    const server = await startServer();
    const testResults = [];

    const mocha = new Mocha({
        timeout: 40000,
        reporter: 'spec'
    });

    const testFile = path.join(__dirname, 'test', 'e2e.test.js');
    mocha.addFile(testFile);

    return new Promise((resolve, reject) => {
        const runner = mocha.run(async (failures) => {
            try {
                await generateExcelReport(testResults);
            } catch (err) {
                console.error('Failed to generate Excel report:', err);
            } finally {
                server.close(() => {
                    console.log('🛑 Local test web server stopped.');
                    if (failures > 0) {
                        console.error(`❌ E2E Test Suite finished with ${failures} failure(s).`);
                        process.exit(1);
                    } else {
                        console.log('✅ All E2E Test Cases passed successfully!');
                        process.exit(0);
                    }
                });
            }
        });

        runner.on('pass', (test) => {
            testResults.push({
                suite: test.parent ? test.parent.title : 'E2E Test Suite',
                title: test.title,
                status: 'PASS',
                duration: test.duration,
                timestamp: new Date().toISOString(),
                error: null
            });
        });

        runner.on('fail', (test, err) => {
            testResults.push({
                suite: test.parent ? test.parent.title : 'E2E Test Suite',
                title: test.title,
                status: 'FAIL',
                duration: test.duration,
                timestamp: new Date().toISOString(),
                error: err ? err.stack || err.message : 'Unknown test failure'
            });
        });
    });
}

runE2ETests().catch((err) => {
    console.error('Error executing test runner:', err);
    process.exit(1);
});
