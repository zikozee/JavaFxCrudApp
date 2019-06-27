package sample;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SqliteConnection {

    public static Connection ConnectToLogin(){
        try{

           Class.forName("org.sqlite.JDBC");
          Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:sample/CustomerDb.db");

            return conn;
        }catch (Exception e){
            System.out.println("Connection Failed " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Connection ConnectToDatabase(String CONNECTION_NAME){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(CONNECTION_NAME);
        }catch (SQLException e){
            System.out.println("Connection failed " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}
