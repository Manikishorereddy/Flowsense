const path = require('path');
const Mocha = require('mocha');
const { generateAndroidExcelReport } = require('./test/excel-reporter');

async function runAndroidAppiumTests() {
    console.log('📱 Starting FlowSense Android Appium E2E Automation Suite (300 Test Cases)...');
    
    const mocha = new Mocha({
        timeout: 120000,
        reporter: 'spec'
    });

    const testFile = path.join(__dirname, 'test', 'appium-e2e.test.js');
    mocha.addFile(testFile);

    return new Promise((resolve) => {
        const runner = mocha.run(async (failures) => {
            const results = global.__ANDROID_E2E_RESULTS__ || [];
            
            try {
                await generateAndroidExcelReport(results);
            } catch (err) {
                console.error('Failed to generate Android Excel report:', err);
            }

            console.log(`\n==================================================`);
            console.log(`📱 Android Appium E2E Execution Complete`);
            console.log(`✅ Total Test Cases Processed: ${results.length}`);
            console.log(`✔ Passed: ${results.filter(r => r.status === 'PASS').length}`);
            console.log(`✖ Failed: ${failures}`);
            console.log(`==================================================\n`);

            if (failures > 0) {
                process.exit(1);
            } else {
                process.exit(0);
            }
        });
    });
}

runAndroidAppiumTests().catch((err) => {
    console.error('Error executing Android test runner:', err);
    process.exit(1);
});
