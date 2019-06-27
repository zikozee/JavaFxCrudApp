package sample.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class Customer {
    private SimpleIntegerProperty id, charge;
    private SimpleObjectProperty<Date> date;
    private SimpleStringProperty client_name, pickup_address,client_phone, contact_person, contact_phone, staff,
            delivery_address, cash_Status;


    public Customer(){
        this.id = new SimpleIntegerProperty();
        this.client_name = new SimpleStringProperty();
        this.pickup_address =  new SimpleStringProperty();
        this.client_phone = new SimpleStringProperty();
        this.date  = new SimpleObjectProperty<>();
        this.contact_person =  new SimpleStringProperty();
        this.delivery_address =  new SimpleStringProperty();
        this.contact_phone =  new SimpleStringProperty();
        this.staff =  new SimpleStringProperty();
        this.charge = new SimpleIntegerProperty();
        this.cash_Status = new SimpleStringProperty();

    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getCharge() {
        return charge.get();
    }

    public SimpleIntegerProperty chargeProperty() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge.set(charge);
    }

    public Date getDate() {
        return date.get();
    }

    public SimpleObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public String getClient_name() {
        return client_name.get();
    }

    public SimpleStringProperty client_nameProperty() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name.set(client_name);
    }

    public String getPickup_address() {
        return pickup_address.get();
    }

    public SimpleStringProperty pickup_addressProperty() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address.set(pickup_address);
    }

    public String getClient_phone() {
        return client_phone.get();
    }

    public SimpleStringProperty client_phoneProperty() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone.set(client_phone);
    }

    public String getContact_person() {
        return contact_person.get();
    }

    public SimpleStringProperty contact_personProperty() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person.set(contact_person);
    }

    public String getContact_phone() {
        return contact_phone.get();
    }

    public SimpleStringProperty contact_phoneProperty() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone.set(contact_phone);
    }

    public String getStaff() {
        return staff.get();
    }

    public SimpleStringProperty staffProperty() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff.set(staff);
    }

    public String getDelivery_address() {
        return delivery_address.get();
    }

    public SimpleStringProperty delivery_addressProperty() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address.set(delivery_address);
    }

    public String getCash_Status() {
        return cash_Status.get();
    }

    public SimpleStringProperty cash_StatusProperty() {
        return cash_Status;
    }

    public void setCash_Status(String cash_Status) {
        this.cash_Status.set(cash_Status);
    }

    /**
     * @param "null"
     * @return String : This returns Overrides toString method to keep track
     * */

    @Override
    public String toString() {
        return "ID: " + this.id + ", Client_Name: " + this.client_name + ", Pickup_Address: " +
                this.pickup_address + ", Client_Phone: " + this.getClient_phone() + ", Contact_Phone: " + this.contact_person +
                ", Contact_Phone: " + this.contact_phone + ", Delivery_Address: " + this.delivery_address;
    }
}
