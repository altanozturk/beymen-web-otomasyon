package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;

public class Excel {
    private static XSSFWorkbook workbook;

    public static String getSearchTerm(String filePath, int rowNum, int colNum) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(rowNum);
        String data = row.getCell(colNum).getStringCellValue();
        workbook.close();
        fis.close();
        return data;
    }
}