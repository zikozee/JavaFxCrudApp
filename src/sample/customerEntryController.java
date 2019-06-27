package sample;

import sample.Model.Customer;
import sample.Model.EXCELFileReader;
import sample.Model.WriteEXCEL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class customerEntryController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, String> tableClientName, tablePickup, tableContactPerson,
            tableDelivery, tableContactPhone, tableStaff, tableDate, tableClientPhone, tablePaymentStatus;

    @FXML
    private TableColumn<Customer, Integer> tableSerialNo, tableCharge;

    @FXML
    private TextArea resultArea;

    @FXML
    private TextField searchField;

    @FXML
    private RadioButton mainRBPaid, mainRBUnpaid, mainRBAll;

    private List<Customer> candidatesSHOW;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableSerialNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableClientName.setCellValueFactory(new PropertyValueFactory<>("client_name"));
        tablePickup.setCellValueFactory(new PropertyValueFactory<>("pickup_address"));
        tableDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableClientPhone.setCellValueFactory(new PropertyValueFactory<>("client_phone"));
        tableContactPerson.setCellValueFactory(new PropertyValueFactory<>("contact_person"));
        tableDelivery.setCellValueFactory(new PropertyValueFactory<>("delivery_address"));
        tableContactPhone.setCellValueFactory(new PropertyValueFactory<>("contact_phone"));
        tableStaff.setCellValueFactory(new PropertyValueFactory<>("staff"));
        tableCharge.setCellValueFactory(new PropertyValueFactory<>("charge"));
        tablePaymentStatus.setCellValueFactory(new PropertyValueFactory<>("cash_Status"));

        //for totalAmount fxml
        customersTable.getColumns().clear();
        customersTable.getColumns().addAll( tableSerialNo, tableClientName, tablePickup, tableDate,
                tableClientPhone, tableContactPerson, tableDelivery, tableContactPhone, tableStaff, tableCharge,
                tablePaymentStatus);
    //in order to use columnResizePolicy and still resize column, is to column.setMinWidth and column.setMaxWidth
        // to same value then it will auto align
    }

    @FXML
    private void existingData(){
        customersTable.getItems().clear();
        DataSource.getInstance().existingDatabaseName(mainBorderPane);
        listCandidates();
        resultArea.setText("Existing data Loaded Successfully!\n");
    }

    @FXML
    private void newDatabase(){
        DataSource.getInstance().result();
        if(DataSource.getInstance().getDB_NAME() == null || DataSource.getInstance().getDB_NAME().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OOPS!!!");
            alert.setHeaderText(null);
            alert.setContentText("Database Requires a Name");
            alert.showAndWait();
            return;
        }else {
            if(customersTable.getItems() != null){
                customersTable.getItems().clear();
            }
            DataSource.getInstance().createDatabaseAndItsTable();
            listCandidates();
            resultArea.setText("Database Created Successfully!\n");
        }
    }

    @FXML
    private void compiledDatabase() {
        DataSource.getInstance().result();
        if(DataSource.getInstance().getDB_NAME() == null || DataSource.getInstance().getDB_NAME().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OOPS!!!");
            alert.setHeaderText(null);
            alert.setContentText("Database Requires a Name");
            alert.showAndWait();
            return;
        }else {
            customersTable.getItems().clear();
            DataSource.getInstance().createDatabaseAndItsTable();
            compiledInsertion();
            listCandidates();
            listToExcel();
            resultArea.setText("Database Created Successfully!\n");
        }
    }

    private void compiledInsertion(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SELECTION PANE PREFIX");
        alert.setHeaderText(null);
        alert.setContentText("SELECT EXCEL FOLDER FILES!!!");
        alert.showAndWait();
        EXCELFileReader fileReader =  new EXCELFileReader();
        fileReader.initialize();
        System.out.println("does it work 1111");
        ArrayList<Customer> customerList = fileReader.getCandidatesData();
        System.out.println("does it work:   " + customerList.size());

        for(Customer customer: customerList){
            DataSource.getInstance().insertIntoTable(customer);
        }
        resultArea.setText("Database Compilation Successful!!!\n");
    }

    private void listCandidates() {
        GetAllCandidatesTask task = new GetAllCandidatesTask();
        // remember the class created below i.e GetAllArtistTask() returns a list of customer( i.e observableList)
        customersTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    public ObservableList<Customer> getTaskList() throws ParseException {
        GetAllCandidatesTask task = new GetAllCandidatesTask();
        return task.call();
    }


    @FXML
    public void AddCustomer(){
        Stage dialog = new Stage();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add Customer");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AddCustomer.fxml"));
        GridPane editor = null;
        try {
            editor = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.initModality(Modality.WINDOW_MODAL);
        CustomerController controller = fxmlLoader.getController();

        Button Save = new Button("save");
        Save.setFont(Font.font("Tahoma", 15));
        Save.setOnAction( e-> {
            Customer newCandidate = controller.processAddCandidate();
            if(newCandidate == null){
                return;
            }
            DataSource.getInstance().addCustomer(newCandidate);
            resultArea.setText("Customer Added Successfully!!!\n");
            listCandidates();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Insertion Status");
            alert.setContentText("Customer Added Successfully");
            alert.showAndWait();

            dialog.close();
        });

        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font("Tahoma", 15));
        cancel.setOnAction(event -> dialog.close());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2,2,8,2));
        hBox.setSpacing(35);
        hBox.getChildren().addAll(Save, cancel);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        editor.add(hBox, 0, 10, 2, 1);
        editor.setHgap(10);
        editor.setVgap(10);
        Scene scene = new Scene(editor);
        dialog.setScene(scene);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    @FXML
    public void updateCustomer(){
        //SELECT ANY AND UPDATE
        final Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the customer Entry you want to edit.");
            alert.showAndWait();
            return;
        }

        Stage dialog = new Stage();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Customer");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AddCustomer.fxml"));
        GridPane editor = null;
        try {
            editor = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.initModality(Modality.WINDOW_MODAL);

        CustomerController controller = fxmlLoader.getController();
        controller.editCustomer(selectedCustomer);

        Button Save = new Button("save");
        Save.setFont(Font.font("Tahoma", 15));
        Save.setOnAction( e-> {
            Customer modifiedCustomer = controller.updateCustomerController(selectedCustomer);
            if(modifiedCustomer == null){
                return;
            }
            DataSource.getInstance().updateCustomer(modifiedCustomer);
            resultArea.setText("Customer UPDATED Successfully!!!\n");
            listCandidates();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Insertion Status");
            alert.setContentText("Customer Edited Successfully");
            alert.showAndWait();

            dialog.close();
        });

        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font("Tahoma", 15));
        cancel.setOnAction(event -> dialog.close());
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2,2,8,2));
        hBox.setSpacing(35);
        hBox.getChildren().addAll(Save, cancel);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        editor.add(hBox, 0, 10, 2, 1);
        editor.setHgap(10);
        editor.setVgap(10);
        Scene scene = new Scene(editor);
        dialog.setScene(scene);
        dialog.setResizable(true);
        dialog.showAndWait();

    }

    public void deleteCustomer(){
        //SELECT ANY AND UPDATE
        final Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the customer Entry you want to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete Candidate");
        alert.setContentText("Are you sure you want to delete this Customer: " +
                selectedCustomer.getClient_name() + ", who transacted =N=" + selectedCustomer.getCharge());

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DataSource.getInstance().deleteCustomer(selectedCustomer);
            resultArea.setText("Customer DELETED Successfully!!!\n");
            listCandidates();
        }
    }

    @FXML
    public void listToExcel()  {
        try {
            new WriteEXCEL().getWrittenEXCEL(new ArrayList<>(getTaskList()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        resultArea.setText("Data Written to Excel Successfully!!!\n");
    }



    @FXML
    protected void searchCustomer()  {
        List<Customer> candidates = null;
        String answer = null;

        if(mainRBPaid.isSelected()){
            answer = "P";
        }else if(mainRBUnpaid.isSelected()){
            answer = "U";
        }else if(mainRBAll.isSelected()){
            answer = "UP";
        }
        try {
            candidates = DataSource.getInstance().searchCustomers(searchField.getText().toUpperCase(), answer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(candidates != null){
            candidatesSHOW = candidates;
            final String finalAnswer = answer;
            Task<ObservableList<Customer>> task = new Task<ObservableList<Customer>>() {
                @Override
                protected ObservableList<Customer> call() throws ParseException {
                    return FXCollections.observableArrayList(
                            DataSource.getInstance().searchCustomers(searchField.getText().toUpperCase(), finalAnswer));
                }
            };
            customersTable.itemsProperty().bind(task.valueProperty());
            new Thread(task).start();
            resultArea.setText("Customer Search Successful!!!");

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entry Status");
            alert.setHeaderText("Please Note");
            alert.setContentText("No Customer matches this Criteria");
            alert.showAndWait();
            resultArea.setText("No Customer matches this Criteria");
        }
    }
    @FXML
    private void ShowDetails(){
        new dynamicReports(candidatesSHOW);
    }

    @FXML
    public void DateRangeListShow(){
        Stage dialog = new Stage();

        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Select Date Range");

        GridPane editor = new GridPane();

        editor.setStyle("-fx-background-color:#D1E57D");
        editor.prefWidth(460);
        editor.prefHeight(134);
        editor.setHgap(10);
        editor.setVgap(10);

        Label labelFrom = new Label("Date Range From:");
        labelFrom.setFont(Font.font("Monotype Corsiva", 18));

        Label labelTo = new Label("To:");
        labelTo.setFont(Font.font("Monotype Corsiva", 18));

        Label labelPayment = new Label("Payment Status:");
        labelPayment.setFont(Font.font("Monotype Corsiva", 18));

        DatePicker dateFrom = new DatePicker();

        DatePicker dateTo = new DatePicker();

        ToggleGroup tg = new ToggleGroup();

        RadioButton paid = new RadioButton("Paid");
        paid.setFont(Font.font("Monotype Corsiva", 18));
        paid.setToggleGroup(tg);
        paid.setPadding(new Insets(10,0,0,0));

        RadioButton unPaid = new RadioButton("Unpaid");
        unPaid.setFont(Font.font("Monotype Corsiva", 18));
        unPaid.setToggleGroup(tg );
        unPaid.setPadding(new Insets(10,0,0,20));

        RadioButton All = new RadioButton("All");
        All.setFont(Font.font("Monotype Corsiva", 18));
        All.setToggleGroup(tg );
        All.setSelected(true);
        All.setPadding(new Insets(10,0,0,20));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(paid, unPaid, All);


        GridPane.setMargin(labelFrom, new Insets(20,0,0,20));
        GridPane.setMargin(labelTo, new Insets(10,0,0,115));
        GridPane.setMargin(dateFrom, new Insets(20,0,0,0));
        GridPane.setMargin(dateTo, new Insets(10,0,0,0));
        GridPane.setMargin(labelPayment, new Insets(0,0,0,20));
        GridPane.setMargin(hbox, new Insets(0,20,0,0));

        editor.add(labelFrom, 0,0,1,1);
        editor.add(dateFrom, 1,0,1,1);
        editor.add(labelTo,0,1,1,1);
        editor.add(dateTo,1,1,1,1);
        editor.add(labelPayment, 0,2,1,1);
        editor.add(hbox,1,2,1,1);


        Button Show = new Button("Show List");
        Show.setFont(Font.font("Tahoma", 15));
        Show.setOnAction(e-> {
            String answer = null;

            if(paid.isSelected()){
                answer = "P";
            }else if(unPaid.isSelected()){
                answer = "U";
            }else if(All.isSelected()){
                answer = "UP";
            }
            String finalAnswer = answer;

            try {
                final List<Customer> searchedCandidates = DataSource.getInstance().dateRangeSearch(dateFrom.getValue().toString(),
                        dateTo.getValue().toString(), finalAnswer);

                if(!searchedCandidates.isEmpty()){
                    new dynamicReports(searchedCandidates);
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Entry Status");
                    alert.setContentText("DATE OR PAYMENT STATUS CHOSEN MATCHES NO CRITERIA");
                    alert.showAndWait();
                    return;
                }

            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            dialog.close();
        });

        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font("Tahoma", 15));
        cancel.setOnAction(event -> dialog.close());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2,2,8,2));
        hBox.setSpacing(35);
        hBox.getChildren().addAll(Show, cancel);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        editor.add(hBox, 0, 4, 2, 1);
        editor.setHgap(10);
        editor.setVgap(10);
        Scene scene = new Scene(editor);
        dialog.setScene(scene);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}

class GetAllCandidatesTask extends Task {
    @Override
    public ObservableList<Customer> call() throws ParseException {
        return FXCollections.observableArrayList
                (DataSource.getInstance().queryCustomers());
    }
    //we can't place any order code in here because if we do, it will run on the UI THREAD hence ****above in listArtist
}