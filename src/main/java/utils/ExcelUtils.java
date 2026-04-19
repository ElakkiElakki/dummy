package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ExcelUtils {

    private ExcelUtils() {
    }

    public static Map<String, String> getTestCaseData(String workbookPath, String sheetName, String testCaseId) {
        try (InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(workbookPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Excel file not found in classpath: " + workbookPath);
            }
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new RuntimeException("Sheet not found: " + sheetName);
                }
                DataFormatter formatter = new DataFormatter();
                Row headerRow = sheet.getRow(0);
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    String currentTestCaseId = formatter.formatCellValue(row.getCell(2)).trim();
                    if (!testCaseId.equalsIgnoreCase(currentTestCaseId)) {
                        continue;
                    }
                    Map<String, String> data = new LinkedHashMap<String, String>();
                    for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                        String header = formatter.formatCellValue(headerRow.getCell(cellIndex)).trim();
                        if (header.isEmpty()) {
                            continue;
                        }
                        Cell cell = row.getCell(cellIndex);
                        String value = cell == null ? "" : formatter.formatCellValue(cell).trim();
                        data.put(header, value);
                        data.put(normalizeKey(header), value);
                    }
                    data.put("scenario_id", data.get("scenario_id"));
                    data.put("test_case_id", data.get("test_case_id"));
                    return data;
                }
                throw new RuntimeException("Test Case ID not found in Excel: " + testCaseId);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel test data", e);
        }
    }

    private static String normalizeKey(String key) {
        return key.trim().toLowerCase().replace("/", " ").replace("-", " ").replace(".", "").replace("(", "").replace(")", "").replaceAll("\\s+", "_");
    }
}
