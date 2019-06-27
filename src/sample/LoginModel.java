package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {
    private Connection connection;
    public LoginModel() {
        connection = SqliteConnection.ConnectToLogin();
        if(connection == null) {
            System.out.println("Connection not successful");
            System.exit(1);
        }
    }

    public boolean isDbConnected(){
        try{
            return !connection.isClosed();
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }
    public boolean isLogin(String user, String pass){

        String query = "Select * from customerLogin where username = ? and password = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);

            ResultSet resultSet = preparedStatement.executeQuery();

//            String username = resultSet.getString("username");
//            String password = resultSet.getString(4);
//            System.out.println("Username= " + username + " Password= " + password);

            return resultSet.next();
        }catch (Exception e){
            return false;
        }
    }

    public String[] loginReturn(String securityQuestion) {

        String query = "Select * from customerLogin where security_question = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, securityQuestion);

            ResultSet resultSet = preparedStatement.executeQuery();

            String username = resultSet.getString("username");
            String password = resultSet.getString(4);
//            System.out.println("Username= " + username + " Password= " + password);
            String[] loginArray = new String[2];
            loginArray[0] = username;
            loginArray[1] = password;
           return loginArray;
        }catch (SQLException e){
            System.out.println("Query failed");
            return null;
        }
    }
}
