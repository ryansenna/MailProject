/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxcontrollers;

import business.ActionModule;
import fileIO.PropertiesIO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import persistence.EmailDAO;
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
    private PasswordField passField;

    @FXML
    private Button enterBtn;

    @FXML
    private TextField smtpField;

    @FXML
    private TextField imapField;

    @FXML
    private TextField dbUsernameField;

    @FXML
    private PasswordField dbPassField;

    private Scene scene;
    private Stage stage;
    private FXMLEmailPageController epc;
    private ConfigProperty cp;// model for the properties.
    private ActionModule actions;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Default constructor
     */
    public FrontPageFXMLController() {
        super();
        cp = new ConfigProperty();//init the Model.
    }

    /**
     * Since we cannot pass values trough the constructor, This method does this
     * dirty job.
     *
     * @param scene
     * @param stage
     * @param epc
     */
    public void setSceneStateSecPage(Scene scene, Stage stage, FXMLEmailPageController epc) {
        this.scene = scene;
        this.stage = stage;
        this.epc = epc;
    }

    @FXML
    private void initialize() {

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
        EmailDAO edao;
        ActionModule actions;
        try {
            pIO.writeTextProperties("", "MailConfig", cp);
            cp.setUrl("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612");
            edao = new EmailDAO(cp);
            actions = new ActionModule(cp);
            actions.receiveEmail();
            // Change the scene on the stage
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            // Need to do more than just log the error
        }
    }
}
