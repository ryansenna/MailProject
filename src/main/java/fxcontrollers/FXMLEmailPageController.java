/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxcontrollers;

import beans.RyanEmail;
import business.ActionModule;
import business.ConfigModule;
import business.Validator;
import fileIO.PropertiesIO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

        try {
            String subject = this.getSubject();
            String message = this.getMessage();
            RyanEmail sentEmail = new RyanEmail();
            MailAddress[] toAddresses = this.getToField(toField);// get the To Addresses.
            MailAddress[] ccAddresses = this.getMailAddresses(ccField);
            MailAddress[] bccAddresses = this.getMailAddresses(bccField);
            sentEmail = am.sendEmail(subject, message, toAddresses, Optional.empty(),
                    Optional.empty(), Optional.of(ccAddresses), Optional.of(bccAddresses));
            edao.create(sentEmail);
            alertSuccessful();
        } catch (IllegalArgumentException iae) {
            alertUserMistake(iae.getMessage());//"One of your email fields was not properly set.");
            clearFields();
        } catch (SQLException sqlE) {
            log.error("Error 200: The message was not properly saved into the database.");
            sqlE.printStackTrace();
        } catch (Exception ex) {
            log.error("Error 101: The Message was not sent.");
            ex.printStackTrace();
        } finally {
            clearFields();
        }

    }

    public boolean loadCPProperties() {
        PropertiesIO pm = new PropertiesIO();
        boolean result = false;// if the properties were loaded or not.

        try {
            result = pm.loadTextProperties(cp, "", "MailConfig");
            log.error("THIS VAR WAS LOADED: " + cp.getSmtpServerName());
        } catch (IOException ex) {
            log.error("Error 100: The properties were not loaded.");
        }
        return result;// if it is true, the properties were loaded.
    }

    /**
     * This helper method will get the raw input from a field and transform into
     * a workable Mail Address array.
     *
     * @return an array with emails from the to field.
     */
    private MailAddress[] getToField(TextField field) {
        Validator v = new Validator();
        String addressesAsString = field.getText();// get the raw addresses

        if (v.isEmailAddressFieldValid(field)) {// if the addresses field are valid.
            String[] addresses = addressesAsString.split(",");// split into an array
            MailAddress[] returnList = new MailAddress[addresses.length];// create my list of Mail Addresses.

            // loop trough the string addresses, adding to my return list.
            for (int i = 0; i < addresses.length; i++) {
                if (addresses[i].isEmpty()) {
                    throw new IllegalArgumentException("Error 303: To Field is Mandatory");
                }
                MailAddress m = new MailAddress(addresses[i].trim());
                returnList[i] = m;
            }
            return returnList;// if they are all valid, return that list.
        }
        //otherwise, throw an exception.
        throw new IllegalArgumentException("Error 300: one of The Email Fields are not properly set.");
    }

    private MailAddress[] getMailAddresses(TextField field) {
        Validator v = new Validator();
        String addressesAsString = field.getText();// get the raw addresses

        if (v.isEmailAddressFieldValid(field)) {// if the addresses field are valid.
            String[] addresses = addressesAsString.split(",");// split into an array
            MailAddress[] returnList = new MailAddress[addresses.length];// create my list of Mail Addresses.

            // loop trough the string addresses, adding to my return list.
            for (int i = 0; i < addresses.length; i++) { 
                MailAddress m = new MailAddress(addresses[i].trim());
                returnList[i] = m;
            }
            return returnList;// if they are all valid, return that list.
        }
        //otherwise, throw an exception.
        throw new IllegalArgumentException("Error 300: one of The Email Fields are not properly set.");
    }

    private String getSubject() {
        Validator v = new Validator();
        String s = v.stripTags(subjectField.getText()).trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Error 301: Subject Field is mandatory");
        }
        return s;
    }

    private void clearFields() {
        toField.setText("");
        subjectField.setText("");
        messageField.setHtmlText("");
    }

    private void alertUserMistake(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Somethig went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void alertSuccessful() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Ryan's Email Service");
        alert.setContentText("Your message was sent.");
        alert.showAndWait();
    }

    /**
     * This method will get the plain text message from the html message field.
     *
     * @return
     */
    private String getMessage() {
        Validator v = new Validator();
        String s = v.stripTags(subjectField.getText()).trim();
        return s;
    }

}
