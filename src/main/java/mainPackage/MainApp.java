/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPackage;

import fileIO.PropertiesIO;
import fxcontrollers.FXMLEmailPageController;
import fxcontrollers.FrontPageFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import properties.ConfigProperty;

/**
 *
 * @author Railanderson Sena
 */
public class MainApp extends Application {

    private FXMLEmailPageController epc;
    private Stage stage;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Default constructor
     */
    public MainApp() {
        super();
    }

    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;
        Scene scene2 = createFXMLPageController();
        Scene scene1 = createFXMLFrontPageController(scene2);

        if (!checkForProperties()) {
            this.stage.setScene(scene1);// set front page
            
        } else {
            this.stage.setScene(scene2);//
        }

        this.stage.setTitle("Ryan's Email Service");

        this.stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private boolean checkForProperties() throws Exception {
        ConfigProperty cp = new ConfigProperty();
        PropertiesIO pio = new PropertiesIO();
        
        if(pio.loadTextProperties(cp, "", "MailConfig")){
            return true;
        }
        return false;
    }

    private Scene createFXMLFrontPageController(Scene scene2) throws Exception {
        FXMLLoader loader = new FXMLLoader();// create FXMLLoader
        
        //Retrieve the FXML file from the front page.
        loader.setLocation(this.getClass().getResource("/fxml/frontPage.fxml"));
        
        //Declare the root of this page
        Parent root = (GridPane) loader.load();
        
        //get the controller of this page.
        FrontPageFXMLController frontPageContr = loader.getController();
        
        // set the references to the second page.
        frontPageContr.setSceneStateSecPage(scene2, stage, epc);
        
        Scene scene = new Scene(root);
        return scene;
    }

    private Scene createFXMLPageController() throws Exception{
        FXMLLoader loader = new FXMLLoader();// create FXMLLoader
        
        boolean loaded = false;
        
        //Retrieve where is my FXML file
        loader.setLocation(this.getClass().getResource("/fxml/FXMLEmailPage.fxml"));
        
        //Declare the parent/root of all my elements in the pane.
        Parent root = (BorderPane) loader.load();
        
        epc = loader.getController();// get the controller
        loaded = epc.loadCPProperties();
        log.error(""+loaded);// load the properties to the config property.
        Scene scene = new Scene(root);// set up the scene
        return scene; // return it.
    }

}
