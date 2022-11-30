package com.mystic.arczen.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author javacodepoint.com
 */
public class JsonToExcelConverter {

    private final ObjectMapper mapper = new ObjectMapper();

    public File jsonFileToExcelFile(URL url, String xlsxExtension) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Reading the json file
            ObjectNode jsonData = mapper.readTree(url).deepCopy();

            //Iterating over the each sheets
            Iterator<String> sheetItr = jsonData.fieldNames();
            while (sheetItr.hasNext()) {

                // create the workbook sheet
                String sheetName = sheetItr.next();
                Sheet sheet = workbook.createSheet(sheetName);

                ArrayNode sheetData = new ArrayNode(JsonNodeFactory.instance).add(jsonData.get(sheetName));
                ArrayList<String> headers = new ArrayList<>();

                //Creating cell style for header to make it bold
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                //creating the header into the sheet
                Row header = sheet.createRow(0);
                Iterator<Map.Entry<String, JsonNode>> it = sheetData.get(0).fields();
                int headerIdx = 0;
                while (it.hasNext()) {
                    String headerName = it.next().getKey();
                    headers.add(headerName);
                    Cell cell = header.createCell(headerIdx++);
                    cell.setCellValue(headerName);
                    //apply the bold style to headers
                    cell.setCellStyle(headerStyle);
                }

                //Iterating over the each row data and writing into the sheet
                for (int i = 0; i < sheetData.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < headers.size(); j++) {
                        if(sheetData.get(i).get(headers.get(j)) != null) {
                            row.createCell(j).setCellValue(sheetData.get(i).get(headers.get(j)).asText());
                        } else {
                            row.createCell(j).setCellValue("");
                        }
                    }
                }

                /*
                 * automatic adjust data in column using autoSizeColumn, autoSizeColumn should
                 * be made after populating the data into the excel. Calling before populating
                 * data will not have any effect.
                 */
                for (int i = 0; i < headers.size(); i++) {
                    sheet.autoSizeColumn(i);
                }

            }

            File file = File.createTempFile("Weather XL", xlsxExtension);
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);

            //close the workbook and fos
            workbook.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteTempWorkbook(File file) {
       if(file.delete()) {
           System.out.println("Temp Workbook file deleted");
         } else {
           System.out.println("Error: Temp Workbook file not deleted");
       }
    }
}