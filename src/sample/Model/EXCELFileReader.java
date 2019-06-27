package sample.Model;

import com.aspose.cells.LoadFormat;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.SaveFormat;

import com.aspose.cells.Workbook;
import edu.duke.DirectoryResource;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class EXCELFileReader {

    /***
     * Private Field to help store data
     */
    private ArrayList<Customer> candidatesData;

    /**
     *constructor that help with initialization
     */
    public  void initialize(){
        if (this.candidatesData == null) {
            candidatesData = new ArrayList<>();
            LoadCandidates();
            System.out.println("here nko:::::  "+candidatesData.size());
        }
    }


    /**
     * This method Validates if excel file contains the needed Data header
     * @param "XSSFSheet"
     * @return LinkedList<String>
     */
    private LinkedList<String> headerValidation( XSSFSheet sheet){
        LinkedList<String> linkedList = new LinkedList<>();
        Row row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();

        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            linkedList.add(cellAsString(cell));
        }
        return linkedList;
    }

    /**
     * This method opens directory to select multiple files(i.e .xlsx),
     * confirms if each excel file has a predefined header with the help of headerValidation() above
     * if so then copy data of each flle(first sheet) and stores in global variable candidateData
     *
     * @param "null"
     *
     * @return "void"
     */
    private void LoadCandidates(){
        try{
            DirectoryResource dr = new DirectoryResource();
            xlsToXlsx(dr);
            for(File f: dr.selectedFiles()){
                //Validation for excel file only

                if (f.getName().endsWith(".xlsx")) {

                    FileInputStream fileInputStream = new FileInputStream(f);
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

                    XSSFSheet sheet = workbook.getSheetAt(0);
                    //Iterate through each row from first sheet
                    Iterator<Row> rows = sheet.rowIterator();
                    if(!headerValidation(sheet).equals(new WriteEXCEL().getEXCEL_HEADER())){
                        continue;
                    }

                    while(rows.hasNext()){
                        Row row = rows.next();
                        //skip HEADER row

                        if(row.getRowNum() == 0) continue;

                        Iterator<Cell> cellIterator = row.cellIterator();

                        ArrayList<String> dataColumn = new ArrayList<>();
                        //Iterate through each cell in row
                        while(cellIterator.hasNext()){
                            Cell cell = cellIterator.next();
                            dataColumn.add(cellAsString(cell));
                        }

                        Customer customer = new Customer();
                        customer.setId(Integer.parseInt(dataColumn.get(0)));
                        customer.setClient_name(dataColumn.get(1).toUpperCase());
                        customer.setPickup_address(dataColumn.get(2).toUpperCase());
                        customer.setClient_phone(dataColumn.get(3).toUpperCase());
                        customer.setDate(Date.valueOf(dataColumn.get(4)));
                        customer.setContact_person(dataColumn.get(5).toUpperCase());
                        customer.setDelivery_address(dataColumn.get(6).toUpperCase());
                        customer.setContact_phone(dataColumn.get(7).toUpperCase());
                        customer.setStaff(dataColumn.get(8).toUpperCase());
                        customer.setCharge(Integer.parseInt(dataColumn.get(9))); // validate if integer
                        customer.setCash_Status(dataColumn.get(10).toUpperCase());

                        System.out.println(customer.toString());

                        candidatesData.add(customer);
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check data type of value of cell and convert to string
     * @param "Cell"
     * @return String value of Cell as String
     */
    private static String cellAsString(Cell cell){
        String strCellValue = null;
        if(cell != null){
            switch (cell.getCellTypeEnum()){
                case STRING:
                    strCellValue = cell.toString();
                    break;
                case NUMERIC:
                    if(DateUtil.isCellDateFormatted(cell)){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                        strCellValue = dateFormat.format(cell.getDateCellValue());
                    }else{
                        Double value = cell.getNumericCellValue();
                        long longValue   = value.longValue();
                        strCellValue = Long.toString(longValue);
                    }
                    break;
                case BOOLEAN:
                    strCellValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK: case ERROR: case _NONE: case FORMULA:
                    strCellValue = "EMPTY";
                    break;
                default:
                    break;
            }
        }
        return strCellValue;
    }

    /**
     *
     * This method returns converts all xls excel to xlsx
     * @param "directoryResource"
     *  @return void
     */

    public void xlsToXlsx(DirectoryResource directoryResource){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About to convert all EXCEL 97-2003");
        alert.setHeaderText("CONVERTING ALL  CHOSEN EXCEL 97-2003 TO RECENT VERSIONS");
        alert.setContentText("Press Ok to Proceed");
        alert.showAndWait();

        int serialNumber = 1;
        for(File f: directoryResource.selectedFiles()) {
            if (f.getName().endsWith(".xls")) {
                LoadOptions  opts= new LoadOptions(LoadFormat.EXCEL_97_TO_2003);

                Workbook wb;
                try {
                    wb = new Workbook(f.getParent() +'\\'+ f.getName(), opts);
                    wb.save(f.getParent() + "\\Output" + serialNumber + ".xlsx", SaveFormat.XLSX);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                serialNumber++;
            }
        }
    }

    public ArrayList<Customer> getCandidatesData() {
        return candidatesData;
    }

}
