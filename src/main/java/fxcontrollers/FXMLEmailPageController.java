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
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jodd.mail.EmailAttachment;
import jodd.mail.MailAddress;
import jodd.util.MimeTypes;
import org.jsoup.Jsoup;
import org.slf4j.LoggerFactory;
import persistence.EmailDAO;
import properties.ConfigProperty;
import properties.FXRyanEmail;

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

    @FXML
    private TableView<FXRyanEmail> tableReceiveField;

    @FXML
    private TableColumn<FXRyanEmail, String> fromColumnField;

    @FXML
    private TableColumn<FXRyanEmail, String> subjectColumnField;

    @FXML
    private TableColumn<FXRyanEmail, String> dateColumnField;

    // a logger for erros
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConfigProperty cp;
    private EmailDAO edao;
    private ActionModule am;
    private ConfigModule c;
    private RyanEmail sentEmail;

    public FXMLEmailPageController() {
        cp = new ConfigProperty();// create a new ConfigProperty.
        sentEmail = new RyanEmail();
        am = new ActionModule(cp);// create a module for sending and receiving.
    }

    @FXML
    private void initialize() {
        //this.saveEmailsToDatabase(cp);
        fromColumnField.setCellValueFactory(cellData -> cellData.getValue().fromField());
        subjectColumnField.setCellValueFactory(cellData -> cellData.getValue().subjectField());
        dateColumnField.setCellValueFactory(cellData -> cellData.getValue().dateField());

        tableReceiveField.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showDetails(newValue));
    }

    @FXML
    void onAttachmentsClicked(ActionEvent event) {
        FileChooser fc = new FileChooser();
        Stage s = (Stage) toField.getScene().getWindow();

        List<File> files = fc.showOpenMultipleDialog(s);
        if (files != null) {
            for (File f : files) {
                //log
                sentEmail.attach(EmailAttachment.attachment().file(f));
                alertSuccessful("Attached!");
            }
        }
    }

    @FXML
    void onEmbeddedClicked(ActionEvent event) {
        FileChooser fc = new FileChooser();
        Stage s = (Stage) toField.getScene().getWindow();

        List<File> files = fc.showOpenMultipleDialog(s);
        if (files != null) {
            for (File f : files) {
                //log
                sentEmail.embed(EmailAttachment.attachment().file(f));
                alertSuccessful("Embeded!");
            }
        }
    }

    /**
     * It takes care of sending an email to its destination and saving it to the
     * database.
     *
     * @param event
     */
    @FXML
    void onSendClicked(ActionEvent event) {
        c = cp.toConfigModule();// create a new ConfigModule
        edao = new EmailDAO(cp);// Create an EDAO for persistence
        am = new ActionModule(cp);// create a module for sending and receiving.

        log.error("THIS VAR WAS LOADED :" + c.getSmtpServerName());

        try {
            setEmail();
            am.sendEmail(sentEmail);// send the email.
            edao.create(sentEmail);// add to the database.
            alertSuccessful("Your message was sent.");// tell the user that its message was sent.
        } catch (IllegalArgumentException iae) {
            log.error(iae.getMessage());
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
            sentEmail = new RyanEmail();
        }

    }

    public void setDAO(EmailDAO e) {
        edao = e;
    }

    public boolean loadCPProperties() {
        PropertiesIO pm = new PropertiesIO();
        boolean result = false;// if the properties were loaded or not.
        try {
            result = pm.loadTextProperties(cp, "", "MailConfig");
        } catch (IOException ex) {
            log.error("Error 100: The properties were not loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;// if it is true, the properties were loaded.
    }

    public void displayTheTable() throws SQLException {
        // Add observable list data to the table
        log.error(""+tableReceiveField);
        tableReceiveField.setItems(this.edao.findAllForFX());
    }

    /**
     * This helper method will set the email field.
     */
    private void setEmail() {
        sentEmail.subject(this.getSubject());
        sentEmail.addMessage(this.getMessage(), MimeTypes.MIME_TEXT_PLAIN);
        sentEmail.setTo(this.getToField(toField));// get the To Addresses.
        sentEmail.setCc(this.getMailAddresses(ccField));
        sentEmail.setBcc(this.getMailAddresses(bccField));
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
        if (!v.isSubjectValid(s)) {
            throw new IllegalArgumentException("Subject Field contain too many characters.");
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

    private void alertSuccessful(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Ryan's Email Service");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method will get the plain text message from the html message field.
     *
     * @return
     */
    private String getMessage() {
        Validator v = new Validator();

        String s = v.stripTags(messageField.getHtmlText()).trim();
        if (!v.isValidMessage(s)) {
            throw new IllegalArgumentException("The number of characters in your message is too big.");
        }

        return s;
    }

    private void showDetails(FXRyanEmail newValue) {
        System.out.println(newValue);
    }
}
