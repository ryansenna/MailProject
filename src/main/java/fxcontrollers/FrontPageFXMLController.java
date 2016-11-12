/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxcontrollers;

import fileIO.PropertiesIO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import properties.ConfigProperty;

/**
 *
 *
 * @author Railanderson Sena
 */
public class FrontPageFXMLController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passField;

    @FXML
    private Button enterBtn;

    @FXML
    private TextField smtpField;

    @FXML
    private TextField imapField;

    @FXML
    private TextField dbUsernameField;

    @FXML
    private TextField dbPassField;
    
    private Scene scene;
    private Stage stage;
    private EmailPageFXMLController epc;
    private ConfigProperty cp;// model for the properties.
    
    /**
     * Default constructor
     */
    public FrontPageFXMLController(){
        super();
        cp = new ConfigProperty();//init the Model.
    }
   
    /**
     * Since we cannot pass values trough the constructor,
     * This method does this dirty job.
     * 
     * @param scene 
     * @param stage
     * @param epc 
     */
    public void setSceneStateSecPage(Scene scene, Stage stage, EmailPageFXMLController epc){
        this.scene = scene;
        this.stage = stage;
        this.epc = epc;
    }
    
    @FXML
    private void initialize(){
        
        Bindings.bindBidirectional(emailField.textProperty(), cp.userEmailAddress());
        Bindings.bindBidirectional(passField.textProperty(), cp.password());
        Bindings.bindBidirectional(smtpField.textProperty(), cp.smtpServerName());
        Bindings.bindBidirectional(imapField.textProperty(), cp.imapServerName());
        Bindings.bindBidirectional(dbUsernameField.textProperty(), cp.dbUsername());
        Bindings.bindBidirectional(dbPassField.textProperty(), cp.dbPass());
    }

    @FXML
    void onEnter(ActionEvent event) {
        PropertiesIO pIO = new PropertiesIO();
        try {
            pIO.writeTextProperties("", "MailConfig", cp);
            // Change the scene on the stage
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            // Need to do more than just log the error
        }
    }
}
