package sample;

import sample.Model.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;


public class CustomerController implements Initializable {

    @FXML
    private TextField clientNameID, pickupID, clientPhoneID, contactPersonID, deliveryID, contactPhoneID,
            staffID, chargeID;
    @FXML
    private RadioButton paidRB;

    @FXML
    private DatePicker dateID;

    private Set<String> possibleWordSet = DataSource.getInstance().WordSet;


    private AutoCompletionBinding<String> autoCompletionBinding;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Collections.addAll(possibleWordSet, DataSource.getInstance().readToSet().toArray(new String[0]));

        textFieldHandler(clientNameID);
        textFieldHandler(pickupID);
        textFieldHandler(clientPhoneID);
        textFieldHandler(contactPersonID);
        textFieldHandler(deliveryID);
        textFieldHandler(contactPhoneID);
        textFieldHandler(staffID);
        textFieldHandler(chargeID);


    }

    private void textFieldHandler(TextField field){
        autoCompletionBinding = TextFields.bindAutoCompletion(field, possibleWordSet);

        field.setOnKeyPressed((KeyEvent e)->{
            switch(e.getCode()){
                case ENTER: case TAB: case SPACE:
                    learnWord(field);
                    break;
                default:
                    break;
            }
        });
    }

    private void learnWord(TextField newTextField){
        possibleWordSet.add(newTextField.getText());

        if(autoCompletionBinding != null){
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(newTextField, possibleWordSet);
    }

    public Customer processAddCandidate(){
        if(fieldValidation(clientNameID, pickupID, clientPhoneID, dateID, contactPersonID, deliveryID,
                contactPhoneID, staffID, chargeID)){
            return null;
        }
        Customer customer = new Customer();
        customer.setClient_name(clientNameID.getText().trim().toUpperCase());
        customer.setPickup_address(pickupID.getText().trim().toUpperCase());
        customer.setClient_phone(clientPhoneID.getText().trim().toUpperCase());
        customer.setDate(Date.valueOf(dateID.getValue()));
        customer.setContact_person(contactPersonID.getText().trim().toUpperCase());
        customer.setDelivery_address(deliveryID.getText().trim().toUpperCase());
        customer.setContact_phone(contactPhoneID.getText().trim().toUpperCase());
        customer.setStaff(staffID.getText().trim().toUpperCase());
        customer.setCharge(Integer.parseInt(chargeID.getText()));
        if(paidRB.isSelected()){
            customer.setCash_Status("PAID");
        }else{
            customer.setCash_Status("UNPAID");
        }

        return customer;
    }

    public void editCustomer(Customer customer){
        clientNameID.setText(customer.getClient_name());
        pickupID.setText(customer.getPickup_address());
        clientPhoneID.setText(customer.getClient_phone());
        dateID.setValue(customer.getDate().toLocalDate());
        contactPersonID.setText(customer.getContact_person());
        deliveryID.setText(customer.getDelivery_address());
        contactPhoneID.setText(customer.getContact_phone());
        staffID.setText(customer.getStaff());
        chargeID.setText(String.valueOf(customer.getCharge()));
    }

    public Customer updateCustomerController(Customer customer) {
        if(fieldValidation(clientNameID, pickupID, clientPhoneID, dateID, contactPersonID, deliveryID,
                contactPhoneID, staffID, chargeID)){
            return null;
        }
        customer.setClient_name(clientNameID.getText().trim().toUpperCase());
        customer.setPickup_address(pickupID.getText().trim().toUpperCase());
        customer.setClient_phone(clientPhoneID.getText().trim().toUpperCase());
        customer.setDate(Date.valueOf(dateID.getValue()));
        customer.setContact_person(contactPersonID.getText().trim().toUpperCase());
        customer.setDelivery_address(deliveryID.getText().trim().toUpperCase());
        customer.setContact_phone(contactPhoneID.getText().trim().toUpperCase());
        customer.setStaff(staffID.getText().trim().toUpperCase());
        customer.setCharge(Integer.parseInt(chargeID.getText().trim().toUpperCase()));
        if(paidRB.isSelected()){
            customer.setCash_Status("PAID");
        }else{
            customer.setCash_Status("UNPAID");
        }
        return customer;
    }


    public boolean fieldValidation(TextField clientNameID, TextField pickupID, TextField clientPhoneID, DatePicker dateID,
                                   TextField contactPersonID, TextField deliveryID, TextField contactPhoneID,
                                   TextField staffID, TextField chargeID){

        String clientName = clientNameID.getText().trim().toUpperCase();
        if(clientName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Client Name Validation");
            alert.setHeaderText(null);
            alert.setContentText("Client Name box cannot be empty");
            alert.showAndWait();
            return true;
        }

        String pickup = pickupID.getText().trim().toUpperCase();
        if(pickup.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("PickUp Address Validation");
            alert.setHeaderText(null);
            alert.setContentText("PickUp Address box cannot be empty");
            alert.showAndWait();
            return true;
        }

        String clientPhone = clientPhoneID.getText().trim().toUpperCase();
        if(clientPhone.isEmpty() || clientPhone.length() < 11){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Client Phone Number Validation");
            alert.setHeaderText(null);
            alert.setContentText("Client Phone box can neither be empty nor less than 11 digits");
            alert.showAndWait();
            return true;
        }

        if(dateID == null || !isValidDate(dateID)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Date Validation");
            alert.setHeaderText(null);
            alert.setContentText("date box cannot be empty and must be of format MM/dd/YYYY");
            alert.showAndWait();
            return true;
        }

        String contactPerson = contactPersonID.getText().trim().toUpperCase();
        if(contactPerson.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Contact Person Validation");
            alert.setHeaderText(null);
            alert.setContentText("Contact Person box cannot be empty");
            alert.showAndWait();
            return true;
        }

        String delivery = deliveryID.getText().trim().toUpperCase();
        if(delivery.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delivery Address Validation");
            alert.setHeaderText(null);
            alert.setContentText("Delivery Address box Cannot be empty");
            alert.showAndWait();
            return true;
        }

        String contactPhone = contactPhoneID.getText().trim().toUpperCase();
        if(contactPhone.isEmpty() || contactPhone.length() < 11){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Contact Phone Validation");
            alert.setHeaderText(null);
            alert.setContentText("Contact Phone box can neither be empty nor less than 11 digits");
            alert.showAndWait();
            return true;
        }

        String staff = staffID.getText().trim().toUpperCase();
        if(staff.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Staff Validation");
            alert.setHeaderText(null);
            alert.setContentText("staff box cannot be empty");
            alert.showAndWait();
            return true;
        }

        String charge = chargeID.getText().trim().toUpperCase();
        if(charge.isEmpty() || !isInteger(charge)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Amount Charged Validation");
            alert.setHeaderText(null);
            alert.setContentText("charge box cannot be empty and must be numerals");
            alert.showAndWait();
            return true;
        }
        return false;
    }


    private boolean isInteger(String input){
        try{
            Integer.parseInt(input);
            return true;
        }catch (NumberFormatException | NullPointerException e){
            return false;
        }
    }


    private boolean isValidDate(DatePicker datePicker){
        String formattedString = datePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/YYYY"));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
        sdf.setLenient(false);
        try{
            java.util.Date date = sdf.parse(formattedString);
            return true;
        }catch (ParseException e){
            return false;
        }
    }
}
