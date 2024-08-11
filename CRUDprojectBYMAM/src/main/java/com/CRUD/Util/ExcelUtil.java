package com.CRUD.Util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.CRUD.Entity.UserEntity;

public class ExcelUtil {

    public static ByteArrayInputStream generateExcel(List<UserEntity> users) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            String[] columns = {"ID", "Name", "Phone", "Password", "Registration Date"};

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Create data rows
            int rowIndex = 1;
            for (UserEntity user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getName());
                row.createCell(2).setCellValue(user.getPhone());
                row.createCell(3).setCellValue(user.getPassword());
                row.createCell(4).setCellValue(user.getRegistrationDate().toString());
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        }
    }
}