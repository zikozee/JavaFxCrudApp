package sample.Model;

import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class WriteEXCEL {
    private LinkedList<String> EXCEL_HEADER= new LinkedList<>(Arrays.asList("S/N", "CLIENT NAME", "PICKUP ADDRESS",
            "CLIENT PHONE", "DATE", "CONTACT PERSON", "DELIVERY ADDRESS", "CONTACT PHONE", "STAFF", "CHARGE", "PAYMENT STATUS"));

    public LinkedList<String> getEXCEL_HEADER() {
        return EXCEL_HEADER;
    }

    private void excelWritten(ArrayList<Customer> customersList){
        try{
            String sys;
            //File file = new File(System.getProperty("user.home") +"/Desktop");
            Path path = Paths.get(System.getProperty("user.home") + "\\Desktop");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd _ HH-mm-ss");
            String formatDateTime = now.format(formatter);

            if (Files.exists(path)) {
                Files.createDirectories(Paths.get(path.toString() +"\\Customer_Entry_List"));
                sys = path.toString() +"\\Customer_Entry_List\\CustomerList=(" + formatDateTime+ ")" +".xlsx";
                System.out.println(sys);
            }else{
                Files.createDirectories(Paths.get(System.getProperty("user.home") +"\\Customer_Entry_List"));
                sys = System.getProperty("user.home") + "\\Customer_Entry_List\\CustomerList=(" + formatDateTime + ")" + ".xlsx";
            }


            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Customer Entry");

            //Header font styling
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontName("Arial Narrow");
            headerFont.setFontHeightInPoints((short) 14);

            int rowIdx = 0;
            short cellIdx = 0;
            //Header
            Row headerRow = sheet.createRow(rowIdx);
            headerRow.setHeightInPoints(26);
            XSSFCellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //Border
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);



            for (String aEXCEL_HEADER : EXCEL_HEADER) {
                Cell cell = headerRow.createCell(cellIdx++);
                cell.setCellStyle(headerCellStyle);
                cell.setCellValue(aEXCEL_HEADER);

            }


            final XSSFFont font = sheet.getWorkbook().createFont();
            font.setFontName("Arial Narrow");
            font.setBold(true);
            font.setFontHeight(14.0);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(false);// for AutoColumn fit
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            for(int sheetRow = 0; sheetRow< customersList.size(); sheetRow++){
                Row row = sheet.createRow(sheetRow+1);

                ArrayList rowArray = ObjectToArrayList((customersList.get(sheetRow)) );

                cellIdx = 0;
                for (Object aRowArray : rowArray) {
                    Cell cell = row.createCell(cellIdx++);
                    cell.setCellValue((String) aRowArray);
                    cell.setCellStyle(cellStyle);
                    //To Adjust Column
                    sheet.autoSizeColumn(cellIdx);
                    /**
                     * TO DO AUTOSIZE RECEIVER'S ADDRESS
                     */
                }
            }
            FileOutputStream fileOutput = new FileOutputStream(sys);


            workbook.write(fileOutput);
            fileOutput.flush();
            fileOutput.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("WORKBOOK STATUS");
            alert.setHeaderText(null);
            alert.setContentText("Excel Output File written successfully!\n" + "File written to: " + sys );
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("slide creation failed successfully.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getWrittenEXCEL(ArrayList<Customer> neededList) {
        excelWritten(neededList);
    }

    private ArrayList ObjectToArrayList(Customer customer){
        ArrayList<String> list = new ArrayList<>();
        int id = customer.getId();
        String client_name = customer.getClient_name();
        String pickup_address = customer.getPickup_address();
        String client_phone = customer.getClient_phone();
        String date = customer.getDate().toString();
        String contact_person = customer.getContact_person();
        String delivery_address = customer.getDelivery_address();
        String contact_phone  = customer.getContact_phone();
        String staff = customer.getStaff();
        int charge = customer.getCharge();
        String cash_status = customer.getCash_Status();

        Collections.addAll(list, String.valueOf(id), client_name, pickup_address, client_phone, date, contact_person,
                delivery_address, contact_phone, staff,  String.valueOf(charge), cash_status);
        return list;
    }

}
