const ExcelJS = require('exceljs');
const path = require('path');
const fs = require('fs');

async function generateExcelReport(results, outputDir = path.join(__dirname, '../test-reports')) {
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
    }

    const workbook = new ExcelJS.Workbook();
    workbook.creator = 'FlowSense E2E Automation';
    workbook.created = new Date();

    const totalTests = results.length;
    const passedTests = results.filter(r => r.status === 'PASS').length;
    const failedTests = results.filter(r => r.status === 'FAIL').length;
    const passRate = totalTests > 0 ? ((passedTests / totalTests) * 100).toFixed(1) + '%' : '0%';
    const totalDuration = results.reduce((sum, r) => sum + (r.duration || 0), 0);

    // ==========================================
    // 1. SUMMARY SHEET
    // ==========================================
    const summarySheet = workbook.addWorksheet('Summary Metrics', { views: [{ showGridLines: true }] });
    
    // Header Title
    summarySheet.mergeCells('B2:E2');
    const titleCell = summarySheet.getCell('B2');
    titleCell.value = 'FlowSense E2E Test Execution Summary';
    titleCell.font = { name: 'Arial', size: 16, bold: true, color: { argb: 'FFFFFF' } };
    titleCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '1E293B' } };
    titleCell.alignment = { horizontal: 'center', vertical: 'middle' };
    summarySheet.getRow(2).height = 35;

    // Metrics Table Header
    summarySheet.getCell('B4').value = 'Metric';
    summarySheet.getCell('C4').value = 'Value';
    ['B4', 'C4'].forEach(cellRef => {
        const cell = summarySheet.getCell(cellRef);
        cell.font = { name: 'Arial', size: 11, bold: true, color: { argb: 'FFFFFF' } };
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '334155' } };
        cell.alignment = { horizontal: 'left', vertical: 'middle' };
    });

    const metricsData = [
        ['Execution Date', new Date().toLocaleString()],
        ['Total Test Cases', totalTests],
        ['Passed Test Cases', passedTests],
        ['Failed Test Cases', failedTests],
        ['Pass Rate', passRate],
        ['Total Execution Duration', `${(totalDuration / 1000).toFixed(2)} seconds`]
    ];

    metricsData.forEach((row, idx) => {
        const rowNum = 5 + idx;
        const labelCell = summarySheet.getCell(`B${rowNum}`);
        const valCell = summarySheet.getCell(`C${rowNum}`);

        labelCell.value = row[0];
        valCell.value = row[1];

        labelCell.font = { name: 'Arial', size: 11, bold: true };
        valCell.font = { name: 'Arial', size: 11 };

        if (row[0] === 'Passed Test Cases') {
            valCell.font = { name: 'Arial', size: 11, bold: true, color: { argb: '166534' } };
        } else if (row[0] === 'Failed Test Cases' && failedTests > 0) {
            valCell.font = { name: 'Arial', size: 11, bold: true, color: { argb: '991B1B' } };
        } else if (row[0] === 'Pass Rate') {
            valCell.font = { name: 'Arial', size: 12, bold: true, color: { argb: '0284C7' } };
        }

        summarySheet.getRow(rowNum).height = 22;
    });

    summarySheet.getColumn('B').width = 28;
    summarySheet.getColumn('C').width = 30;

    // ==========================================
    // 2. DETAILED TEST RESULTS SHEET
    // ==========================================
    const detailSheet = workbook.addWorksheet('Detailed Results', { views: [{ showGridLines: true }] });

    detailSheet.columns = [
        { header: '#', key: 'id', width: 6 },
        { header: 'Test Suite', key: 'suite', width: 25 },
        { header: 'Test Case Name', key: 'title', width: 45 },
        { header: 'Status', key: 'status', width: 12 },
        { header: 'Duration (ms)', key: 'duration', width: 15 },
        { header: 'Timestamp', key: 'timestamp', width: 22 },
        { header: 'Error Details / Stack Trace', key: 'error', width: 55 }
    ];

    // Header styling
    const headerRow = detailSheet.getRow(1);
    headerRow.height = 28;
    headerRow.eachCell((cell) => {
        cell.font = { name: 'Arial', size: 11, bold: true, color: { argb: 'FFFFFF' } };
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '0F172A' } };
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
    });

    // Populate test rows
    results.forEach((item, index) => {
        const row = detailSheet.addRow({
            id: index + 1,
            suite: item.suite || 'E2E Suite',
            title: item.title,
            status: item.status,
            duration: item.duration || 0,
            timestamp: item.timestamp || new Date().toISOString(),
            error: item.error || ''
        });

        row.height = 22;
        row.getCell('id').alignment = { horizontal: 'center', vertical: 'middle' };
        row.getCell('suite').alignment = { horizontal: 'left', vertical: 'middle' };
        row.getCell('title').alignment = { horizontal: 'left', vertical: 'middle' };
        row.getCell('status').alignment = { horizontal: 'center', vertical: 'middle' };
        row.getCell('duration').alignment = { horizontal: 'right', vertical: 'middle' };
        row.getCell('timestamp').alignment = { horizontal: 'center', vertical: 'middle' };
        row.getCell('error').alignment = { horizontal: 'left', vertical: 'middle' };

        const statusCell = row.getCell('status');
        if (item.status === 'PASS') {
            statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'DCFCE7' } };
            statusCell.font = { name: 'Arial', size: 10, bold: true, color: { argb: '15803D' } };
        } else {
            statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FEE2E2' } };
            statusCell.font = { name: 'Arial', size: 10, bold: true, color: { argb: 'B91C1C' } };
        }
    });

    const reportFilePath = path.join(outputDir, 'e2e-test-report.xlsx');
    await workbook.xlsx.writeFile(reportFilePath);
    console.log(`\n📊 Excel report successfully generated at: ${reportFilePath}\n`);
    return reportFilePath;
}

module.exports = { generateExcelReport };
