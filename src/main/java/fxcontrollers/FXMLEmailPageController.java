/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxcontrollers;

import beans.RyanEmail;
import business.ActionModule;
import business.ConfigModule;
import fileIO.PropertiesIO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import jodd.mail.MailAddress;
import org.jsoup.Jsoup;
import org.slf4j.LoggerFactory;
import persistence.EmailDAO;
import properties.ConfigProperty;

/**
 *
 * @author Railanderson Sena
 */
public class FXMLEmailPageController {

    @FXML
    private TextField toField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextField ccField;

    @FXML
    private TextField bccField;
    
    @FXML
    private HTMLEditor messageField;
    
    // a logger for erros
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConfigProperty cp;
    private EmailDAO edao;
    private ActionModule am;
    private ConfigModule c;
    
    public FXMLEmailPageController() {
        cp = new ConfigProperty();// create a new ConfigProperty.
    }

    @FXML
    void onAttachmentsClicked(ActionEvent event) {

    }

    @FXML
    void onEmbeddedClicked(ActionEvent event) {

    }

    @FXML
    void onSendClicked(ActionEvent event) {
        
        // GET WHAT FIELDS ARE NULL AND WHAT ARE NOT.
        // RIGHT NOW ASSUME YOU HAVE THE REQUIRED ONES.
        
        c = cp.toConfigModule();// create a new ConfigModule
        edao = new EmailDAO(c);// Create an EDAO for persistence
        am = new ActionModule(c);// create a module for sending and receiving.
        
        log.error("THIS VAR WAS LOADED :" + c.getSmtpServerName());
        MailAddress[] toAddresses = this.getMailAddresses(toField);// get the To Addresses.
        String subject = this.getSubject();
        String message = Jsoup.parse(messageField.getHtmlText()).text();
        RyanEmail sentEmail = new RyanEmail();
        
        try {
            sentEmail = am.sendEmail(subject, message, toAddresses, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty());
            edao.create(sentEmail);
        }catch (SQLException sqlE){
            log.error("Error 200: The message was not properly saved into the database.");
            sqlE.printStackTrace();
        } 
        catch (Exception ex) {
            log.error("Error 101: The Message was not sent.");
            ex.printStackTrace();
        }
       finally{
            clearFields();
        }
        
    }

    public boolean loadCPProperties() {
        PropertiesIO pm = new PropertiesIO();
        boolean result = false;// if the properties were loaded or not.
        
        try {
            result =  pm.loadTextProperties(cp, "", "MailConfig");
            log.error("THIS VAR WAS LOADED: " + cp.getSmtpServerName());
        } catch (IOException ex) {
            log.error("Error 100: The properties were not loaded.");
        }
        return result;// if it is true, the properties were loaded.
    }
    /**
     * This helper method will get the raw input from a field and
     * transform into a workable Mail Address array.
     * @return an array with emails from the to field.
     */
    private MailAddress[] getMailAddresses(TextField field){
        String addressesAsString = field.getText();// get the raw addresses
        
        // >>>>>>> VALIDATION HERE <<<<<<<
        
        
        String[] addresses = addressesAsString.split(",");// split into an array
        MailAddress[] returnList = new MailAddress[addresses.length];// create my list of Mail Addresses.
        
        // loop trough the string addresses, adding to my return list.
        for(int i =0; i < addresses.length; i++){
            MailAddress m = new MailAddress(addresses[i]);
            returnList[i] = m;
        }
        return returnList;
    }
    
    private String getSubject()
    {
        // >>>>>>> VALIDATION HERE <<<<<<<
        
        return subjectField.getText();
    }
    
    private void clearFields(){
        subjectField.setText("");
        messageField.setHtmlText("");
    }

}
