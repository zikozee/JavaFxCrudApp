package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public LoginModel loginModel = new LoginModel();

    @FXML
    private Label isConnected;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(loginModel.isDbConnected()){
            isConnected.setText("Connected");
        }else isConnected.setText("Not Connected");
    }

    public void Login(ActionEvent event){
        if(loginModel.isLogin(txtUsername.getText(), txtPassword.getText())){
            isConnected.setText("username and password is correct");
            //to hide login window ==>> code below
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage primaryStage = new Stage();
            // to be used for exit menuItem

            FXMLLoader loader = new FXMLLoader();
            Pane root = null;
            try {
                root = loader.load(getClass().getResource("customerEntry.fxml").openStream());
            } catch (IOException e) {
                System.out.println("Error Loading Window");
                e.printStackTrace();
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add("sample/tableStyler.css");
            primaryStage.setTitle("User Window");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event1 -> {

                //To write words to Holder
                DataSource.getInstance().writeToTextFile();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("CONFIRMATION!!!");
                alert.setHeaderText(null);
                alert.setContentText("Do you really want to close this Window");

                Optional<ButtonType> result =  alert.showAndWait();

                if(result.get() == ButtonType.OK){
                    primaryStage.close();
                    //To close user window and open Login window
                    SignOut();
                }else{
                    event1.consume();
                }
            });
        }else{
            isConnected.setText("username or password is not correct");
        }
    }

    public void onMousePressed(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Show My Username and Password");
        dialog.setContentText("Please Enter Your Login Recovery Answer: ");

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            String[] loginDetails = loginModel.loginReturn(result.get());
//            System.out.println("Login information" + Arrays.toString(loginDetails));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Details");
            alert.setHeaderText(null);
            alert.setContentText("Your username is: " + loginDetails[0] +"\n" +
                    "Your password is: " + loginDetails[1]);
            alert.showAndWait();
        }
    }

    private void SignOut(){
        Stage SecondaryStage = new Stage();
        FXMLLoader newLoader = new FXMLLoader();
        Pane newRoot = null;
        try {
            newRoot = newLoader.load(getClass().getResource("Login.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  Parent root = FXMLLoader.load(getClass().getResource("/sample/Login.fxml"));
        SecondaryStage.setTitle("Login Window");
        SecondaryStage.setScene(new Scene(newRoot));
        SecondaryStage.show();
    }
}
