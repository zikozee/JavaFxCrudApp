package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sample.Model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataSource {
    public static final String sqlCREATION = "CREATE TABLE IF NOT EXISTS clients ("
            + " ID INTEGER PRIMARY KEY NOT NULL," + " CLIENT_NAME TEXT NOT NULL,"
            + " PICKUP_ADDRESS TEXT NOT NULL," + " CLIENT_PHONE TEXT NOT NULL," + " DATE TEXT NOT NULL,"
            + " CONTACT_PERSON TEXT NOT NULL," + " DELIVERY_ADDRESS TEXT NOT NULL," + " CONTACT_PHONE TEXT NOT NULL,"
            + " STAFF TEXT NOT NULL," + " CHARGES INTEGER NOT NULL," + " CASH_STATUS TEXT NOT NULL" + ");";
    public static final String TABLE_INSERTION = "INSERT INTO " +
            "clients(CLIENT_NAME, PICKUP_ADDRESS, CLIENT_PHONE, DATE, CONTACT_PERSON, DELIVERY_ADDRESS, " +
            "CONTACT_PHONE, STAFF, CHARGES, CASH_STATUS) VALUES(?,?,?,?,?,?,?,?,?,?)";
    public static final String QUERY_CUSTOMER = "SELECT * FROM clients WHERE CLIENT_NAME = ? " +
            "AND CONTACT_PERSON = ? AND CONTACT_PHONE = ?; ";
    public static final String TABLE_QUERY_BY_ID = "SELECT * FROM clients ORDER BY ID";
    public static final String UPDATE_CUSTOMER_ENTRY = "UPDATE clients SET CLIENT_NAME = ?, PICKUP_ADDRESS = ?, " +
            "CLIENT_PHONE = ?, DATE = ?, CONTACT_PERSON = ?, DELIVERY_ADDRESS = ?, CONTACT_PHONE = ?," +
            "STAFF = ?, CHARGES = ?, CASH_STATUS= ?  WHERE ID = ?;";
    public static final String DELETE_CANDIDATE = "DELETE FROM clients WHERE ID = ?;";
    public static final String SEARCH_CANDIDATES = "select * FROM clients where CASH_STATUS GLOB ? AND " +
            "(CLIENT_NAME LIKE ? OR CONTACT_PERSON LIKE ? OR CONTACT_PHONE LIKE ?);  ";//remove Order by ID
    public static final String DATE_RANGE_SEARCH = "select * FROM clients where CASH_STATUS GLOB ? AND " +
            "(DATE >= ? AND DATE <= ?);";
    private static DataSource instance = new DataSource();
    public Set<String> WordSet = new HashSet<>();
    // to be used by adding
    private String existingDB_NAME;
    private String DB_NAME;

    private DataSource() {
    }

    public static DataSource getInstance() {
        return instance;
        //To call we den use DataSource.getInstance().METHODNAME();
    }

    /**
     * Purpose: Text Input used to prefix databaseName  + DateTime
     *
     * @param "null"
     * @return "null"
     */
    public void result() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Give your database a name");
        dialog.setHeaderText("Database name Prefix");
        dialog.setContentText("Enter Database Title");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd _ HH-mm-ss");
            String formatDateTime = now.format(formatter);
            DB_NAME = result.get() + "=(" + formatDateTime + ")";
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Cancel Button was Pressed");
            alert.showAndWait();
            return;
        }
    }

    /**
     * Purpose: to choose file existing database name to
     * and pass its value to existingDB_NAME field above
     *
     * @param "BorderPane"
     * @return "null"
     */

    public void existingDatabaseName(BorderPane borderPane) {
        FileChooser chooser = new FileChooser();
        //      chooser.setTitle("Save Application File");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Database File", "*.db"));

        File file = chooser.showOpenDialog(borderPane.getScene().getWindow()); // single file chosen
        if (file != null) {
            existingDB_NAME = file.getAbsolutePath();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFO: ");
            alert.setHeaderText(null);
            alert.setContentText("File Chooser was Cancelled!!!");
            alert.showAndWait();
        }
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    private Connection connect() {
        String CONNECTION_NAME = null;
        if ((existingDB_NAME == null)) {
            File fileCheck = new File("C:/sqlite/databases/" + DB_NAME + ".db");
            if (!fileCheck.isDirectory()) {
                Path path = Paths.get("C:/sqlite/databases");
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CONNECTION_NAME = "jdbc:sqlite:C:/sqlite/databases/" + DB_NAME + ".db";
            }

        } else {
            File file = new File(existingDB_NAME);
            if (file.exists()) {
                CONNECTION_NAME = "jdbc:sqlite:" + existingDB_NAME;
            }
        }

        return SqliteConnection.ConnectToDatabase(CONNECTION_NAME);
    }

    public void createDatabaseAndItsTable() {
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlCREATION);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertionSQLHelper(Customer customer, PreparedStatement insertionSQL) throws SQLException {
        insertionSQL.setString(1, customer.getClient_name().toUpperCase());
        insertionSQL.setString(2, customer.getPickup_address().toUpperCase());
        insertionSQL.setString(3, customer.getClient_phone().toUpperCase());
        insertionSQL.setString(4, customer.getDate().toString());
        insertionSQL.setString(5, customer.getContact_person().toUpperCase());
        insertionSQL.setString(6, customer.getDelivery_address().toUpperCase());
        insertionSQL.setString(7, customer.getContact_phone().toUpperCase());
        insertionSQL.setString(8, customer.getStaff());
        insertionSQL.setInt(9, customer.getCharge());
        insertionSQL.setString(10, customer.getCash_Status());

    }

    private java.sql.Date sqlDATE(String datePickerToString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
        java.util.Date parsed = format.parse(datePickerToString);
        return new java.sql.Date(parsed.getTime());
    }

    private List<Customer> querySearchHelper(ResultSet results, List<Customer> customers) throws SQLException, ParseException {
        while (results.next()) {
            Customer customer = new Customer();
            customer.setId(results.getInt(1));
            customer.setClient_name(results.getString(2));
            customer.setPickup_address(results.getString(3));
            customer.setClient_phone(results.getString(4));
            customer.setDate(sqlDATE(results.getString(5)));//
            customer.setContact_person(results.getString(6));
            customer.setDelivery_address(results.getString(7));
            customer.setContact_phone(results.getString(8));
            customer.setStaff(results.getString(9));
            customer.setCharge(results.getInt(10));
            customer.setCash_Status(results.getString(11));

            // indices are more efficient than name.
            customers.add(customer);
        }
        return customers;
    }

    public List<Customer> searchCustomers(String input, String cashPaymentStatus) throws ParseException {//List<Candidate>

        try (Connection conn = this.connect();
             PreparedStatement searchStatement = conn.prepareStatement(SEARCH_CANDIDATES)) {

            searchStatement.setString(1, "[" + cashPaymentStatus + "]*");
            searchStatement.setString(2, "%" + input + "%");
            searchStatement.setString(3, "%" + input + "%");
            searchStatement.setString(4, "%" + input + "%");

            ResultSet results = searchStatement.executeQuery();
            List<Customer> customers = new ArrayList<>();

            return querySearchHelper(results, customers);

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Customer> dateRangeSearch(String dateFrom, String dateTo, String cashPaymentStatus) throws ParseException {//List<Candidate>

        try (Connection conn = this.connect();
             PreparedStatement searchStatement = conn.prepareStatement(DATE_RANGE_SEARCH)) {

            searchStatement.setString(1, "[" + cashPaymentStatus + "]*");
            searchStatement.setString(2, dateFrom);
            searchStatement.setString(3, dateTo);

            ResultSet results = searchStatement.executeQuery();
            List<Customer> customers = new ArrayList<>();

            return querySearchHelper(results, customers);

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void insertIntoTable(Customer customer) {
        try (Connection conn = this.connect();
             PreparedStatement insertionSQL = conn.prepareStatement(TABLE_INSERTION)) {
            insertionSQLHelper(customer, insertionSQL);
            insertionSQL.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> queryCustomers() throws ParseException {//List<Candidate>

        try (Connection conn = this.connect();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(TABLE_QUERY_BY_ID)) {

            List<Customer> customers = new ArrayList<>();

            return querySearchHelper(results, customers);

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void addCustomer(Customer customer) {
        try (Connection conn = this.connect();
             PreparedStatement queryCustomer = conn.prepareStatement(QUERY_CUSTOMER);
             PreparedStatement insertionSQL = conn.prepareStatement(TABLE_INSERTION)) {
            conn.setAutoCommit(false);

            queryCustomer.setString(1, customer.getClient_name());
            queryCustomer.setString(2, customer.getContact_person());
            queryCustomer.setString(3, customer.getContact_phone());

            ResultSet results = queryCustomer.executeQuery();
            if (results.next()) {
                return;
            }

            insertionSQLHelper(customer, insertionSQL);
            int affectedRows = insertionSQL.executeUpdate();
            if (affectedRows == 1) {
                conn.commit();//end Transaction
            } else {
                throw new SQLException("The Customer insertion failed");
            }

        } catch (Exception e) {
            System.out.println("Insert Customer exception: " + e.getMessage());
            e.printStackTrace();

            try (Connection conn = this.connect()) {
                System.out.println("Performing rollback");
                conn.rollback();//remember rollback is an alias of commit hence TRANSACTION END

            } catch (SQLException e2) {
                System.out.println("Oh boy! things are really bad " + e2.getMessage());
            }

        } finally {
            try (Connection conn = this.connect()) {
                System.out.println("=============");
                System.out.println("Resetting default commit behaviour");
                conn.setAutoCommit(true); // =====>>>>> used for INSERT, UPDATE DELETE ONLY
            } catch (SQLException e) {
                System.out.println("Couldn't reset  auto-Commit! " + e.getMessage());
            }
        }
    }


    public boolean updateCustomer(Customer customer) {// where sqlite + java browser
        try (Connection conn = this.connect();
             PreparedStatement updateSQL = conn.prepareStatement(UPDATE_CUSTOMER_ENTRY)) {
            //don't save
            conn.setAutoCommit(false);
            insertionSQLHelper(customer, updateSQL);
            updateSQL.setInt(11, customer.getId());
            int affectedRecords = updateSQL.executeUpdate();

            if (affectedRecords == 1) {
                conn.setAutoCommit(true);
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Customer exception: " + e.getMessage());
            e.printStackTrace();

            try (Connection conn = this.connect()) {
                conn.rollback();//remember rollback is an alias of commit hence TRANSACTION END
                return false;
            } catch (SQLException e2) {
                System.out.println("Oh boy! things are really bad " + e2.getMessage());
            }

        } finally {
            try (Connection conn = this.connect()) {
                System.out.println("Resetting default commit behaviour");
                conn.setAutoCommit(true); // =====>>>>> used for INSERT, UPDATE DELETE ONLY
            } catch (SQLException e) {
                System.out.println("Couldn't reset  auto-Commit! " + e.getMessage());
            }
        }
        return false;
    }

    public boolean deleteCustomer(Customer customer) {
        try (Connection conn = this.connect();
             PreparedStatement deleteSQL = conn.prepareStatement(DELETE_CANDIDATE)) {
            deleteSQL.setInt(1, customer.getId());
            int affectedRecords = deleteSQL.executeUpdate();
            if (affectedRecords == 1) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Insert Candidate exception: " + e.getMessage());
            e.printStackTrace();

            try (Connection conn = this.connect()) {
                conn.rollback();//remember rollback is an alias of commit hence TRANSACTION END
                return false;
            } catch (SQLException e2) {
                System.out.println("Oh boy! things are really bad " + e2.getMessage());
            }

        } finally {
            try (Connection conn = this.connect()) {
                System.out.println("Resetting default commit behaviour");
                conn.setAutoCommit(true); // =====>>>>> used for INSERT, UPDATE DELETE ONLY
            } catch (SQLException e) {
                System.out.println("Couldn't reset  auto-Commit! " + e.getMessage());
            }
        }
        return false;
    }

    public void writeToTextFile() {
        BufferedWriter writer;
        try {
            File fileCheck = new File(System.getProperty("user.home") + "/Holder/stringHolder.txt");
            writer = new BufferedWriter(new FileWriter(fileCheck, true));

            for (String word : WordSet) {
                writer.write(word);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> readToSet() {
        String pathway = System.getProperty("user.home") + "/Holder/";
        File fileCheck = new File(pathway, "stringHolder.txt");

        if (!fileCheck.isDirectory()) {
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(fileCheck, true));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileCheck));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                WordSet.add(line);
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }


        return WordSet;
    }

}
