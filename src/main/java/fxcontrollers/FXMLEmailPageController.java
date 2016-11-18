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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jodd.mail.EmailAttachment;
import jodd.mail.MailAddress;
import jodd.util.MimeTypes;
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
    private Button replyBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private AnchorPane sendField;
    @FXML
    private TableView<FXRyanEmail> tableReceiveField;
    @FXML
    private TableColumn<FXRyanEmail, String> fromColumnField;
    @FXML
    private TableColumn<FXRyanEmail, String> subjectColumnField;
    @FXML
    private TableColumn<FXRyanEmail, String> dateColumnField;
    @FXML
    private TreeView<String> treeFolders;

    // a logger for erros
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConfigProperty cp;
    private EmailDAO edao;
    private ActionModule am;
    private RyanEmail sentEmail;
    private List<String> folderNames;
    private Stage stage;
    private ObservableList<FXRyanEmail> allEmails;

    private int ctr = 0;

    public FXMLEmailPageController() {
        cp = new ConfigProperty();// create a new ConfigProperty.
        cp.setUrl("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612");
        sentEmail = new RyanEmail();
        am = new ActionModule(cp);// create a module for sending and receiving.
        folderNames = new ArrayList<String>();// create a new list of folders to hold all new folder names.
        allEmails = FXCollections.observableArrayList();// create a list of emails to display.
        folderNames.add("inbox");
        folderNames.add("sent");
    }

    @FXML
    private void initialize() {
        fromColumnField.setCellValueFactory(cellData -> cellData.getValue().fromField());
        subjectColumnField.setCellValueFactory(cellData -> cellData.getValue().subjectField());
        dateColumnField.setCellValueFactory(cellData -> cellData.getValue().dateField());
        treeFolders.setEditable(true);

        setTreeView();
        tableReceiveField.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showDetails(newValue));

    }

    private void setTreeView() {
        TreeItem<String> root = new TreeItem<>();
        root.setValue("Folders");
        root.setExpanded(true);
        treeFolders.setRoot(root);

        treeFolders.setCellFactory(tree -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                }
            };
            return cell;
        });
        // this will create a TreeItem  for every different folder
        // stored in database.
        for (String folderName : folderNames) {
            createItem(folderName);
        }

        treeFolders.getSelectionModel().selectedItemProperty()
                .addListener((b, oldValue, newValue) -> {
                    if (newValue != null) {
                        displayOnlyForThisItem(newValue.getValue());
                    }
                });
    }

    /**
     * This method creates a tree item into my tree view.
     *
     * @param itemName
     */
    private void createItem(String itemName) {
        TreeItem<String> newItem = new TreeItem<>();
        newItem.setValue(itemName);

        treeFolders.getRoot().getChildren().add(newItem);
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
        edao = new EmailDAO(cp);// Create an EDAO for persistence
        am = new ActionModule(cp);// create a module for sending and receiving.

        log.error("THIS VAR WAS LOADED :" + cp.getSmtpServerName());

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

    @FXML
    void onRefreshClicked(ActionEvent event) {
        try {
            am.receiveEmail();// get the emails from the server and save to my db.
            displayTheTable();// display on the screen.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onNewFolderClicked(ActionEvent event) {
        setUpPopUpWindow();
    }

    @FXML
    void onNewEmailClicked(ActionEvent event) {
        clearFields();
        sendField.setDisable(false);
    }

    /**
     * This method will simply set upo the pop up window for the creation of a
     * new folder.
     */
    private void setUpPopUpWindow() {
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Create a New Folder");
        Label label = new Label();
        label.setText("Enter New Folder Name");
        Button okBtn = new Button("OK");
        TextField newFolderName = new TextField();
        newFolderName.setPromptText("Enter a New Folder Name");
        okBtn.setOnAction(e -> okClicked(e, newFolderName, popUpStage));

        VBox layout = new VBox(10);
        layout.getChildren().add(label);
        layout.getChildren().add(newFolderName);
        layout.getChildren().add(okBtn);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 200, 200);
        popUpStage.setScene(scene);
        popUpStage.show();

    }

    /**
     * This helper method is for when the person clicks the ok button at the pop
     * up stage. When the person clicks, it adds whatever the user wrote to the
     * list of folder names.
     *
     * @param e the event
     * @param the text field in the second stage.
     * @param s the stage itself.
     */
    private void okClicked(ActionEvent e, TextField t, Stage s) {
        if (t.getText() != null && !t.getText().isEmpty()) {
            folderNames.add(t.getText());
            createItem(t.getText());
            s.close();
        }

    }

    public void setDAO(EmailDAO e) {
        edao = e;
    }

    public void setStage(Stage s) {
        stage = s;
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
        log.error("" + tableReceiveField);
        loadFolderNamesFromDatabase();// this will get all folder names
        // that are not inbox or sent, since inbox and sent are pre loaded by default.
        allEmails = this.edao.findAllForFX();
        ObservableList<FXRyanEmail> inboxEmails = FXCollections
                .observableArrayList();
        for (FXRyanEmail e : allEmails) {
            if (e.getFolderField().equalsIgnoreCase("inbox")) {
                inboxEmails.add(e);
            }
        }
        tableReceiveField.setItems(inboxEmails);

    }

    /**
     * This helper method will set the email field.
     */
    private void setEmail() {
        sentEmail.subject(this.getSubject());
        sentEmail.addMessage(this.getMessage(), MimeTypes.MIME_TEXT_HTML);
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

        String s = messageField.getHtmlText().trim();
        if (!v.isValidMessage(s)) {
            throw new IllegalArgumentException("The number of characters in your message is too big.");
        }

        return s;
    }

    /**
     * This Method will show the details about a particular email such as its
     * messages, to, subject, etc.
     *
     * @param newValue
     */
    private void showDetails(FXRyanEmail newValue) {
        log.error(newValue.toString());
        if (newValue.getAttachment().length == 0) {
            this.messageField.setHtmlText(newValue.getMessageField());
        } else {
            this.messageField.setHtmlText(newValue.getMessageField() + "\n"
                    + replaceCid(newValue.getAttachment()));
        }
        this.subjectField.setText(newValue.getSubjectField());
        this.toField.setText(newValue.getToField());
        enableImportantBtns(newValue);

    }

    /**
     * This method will enable important buttons such as reply and delete to the
     * user.
     *
     * @param email
     */
    private void enableImportantBtns(FXRyanEmail email) {
        this.deleteBtn.setDisable(false);
        this.replyBtn.setDisable(false);

        //attach listeners to it.
        deleteBtn.setOnAction(e -> onDeleteClicked(e, email));
        replyBtn.setOnAction(e -> onReplyClicked(e, email));

    }

    /**
     * When the user clicks to delete an email, it will delete that email from
     * the view and from the database.
     *
     * @param e the event
     * @param email the email to be deleted.
     */
    private void onDeleteClicked(ActionEvent e, FXRyanEmail email) {
        try {
            edao.delete(email.getRcvdDateField());
            //update the view
            displayTheTable();
        } catch (SQLException s) {
            s.printStackTrace();
            alertUserMistake(s.getMessage());
        }
    }

    /**
     * When the user clicks to reply, it will switch the TO field, add two
     * letters to the subject, and append the old message to the new message.
     *
     * @param e the event
     * @param email the email to be changed.
     */
    private void onReplyClicked(ActionEvent e, FXRyanEmail email) {
        String line = "<br><br>===============================================<br><br>";
        if (email.getAttachment().length == 0) {
            this.messageField.setHtmlText(line + email.getMessageField());
        } else {
            this.messageField.setHtmlText(line + email.getMessageField() + "\n"
                    + replaceCid(email.getAttachment()));
        }
        this.subjectField.setText("Re: " + email.getSubjectField());
        this.toField.setText(email.getFromField());
    }

    /**
     * Based on the foder name, this method will search for the email that
     * belongs to that fodler and set the view for that specific folder. Instead
     * of making a query to the database to look for this, I load all the emails
     * and look for it at a local observable list. Since I was getting very slow
     * performances this way seems to be much quicker.
     *
     * @param item the folder name.
     */
    private void displayOnlyForThisItem(String item) {
        try {
            ObservableList<FXRyanEmail> itemEmails = FXCollections
                    .observableArrayList();
            for (FXRyanEmail email : allEmails) {
                if (email.getFolderField().equalsIgnoreCase(item)) {
                    itemEmails.add(email);
                }
            }
            tableReceiveField.setItems(itemEmails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFolderNamesFromDatabase() {
        try {
            folderNames.addAll(edao.getFolderNamesThatAreNotInboxOrSent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String replaceCid(byte[] attachment) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n"
                + "<head>\n"
                + "<meta charset=\"ISO-8859-1\">\n"
                + "<title>Insert title here</title>\n"
                + "</head>\n"
                + "<body>\n");
        sb.append("<img src=\"data:image/jpg;base64,")
                // Encode a byte array to a Base64 string
                .append(Base64.getMimeEncoder().encodeToString(attachment))
                .append("\"/>");
        sb.append("</body>\n"
                + "</html>");
        return sb.toString();
    }
}
