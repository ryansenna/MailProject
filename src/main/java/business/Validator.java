/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import javafx.scene.control.TextField;
import org.apache.commons.validator.routines.EmailValidator;
import org.jsoup.Jsoup;

/**
 *
 * @author 1333612
 */
public class Validator {
    
    public Validator(){
        
    }
   
    /**
     * This method checks if the Email is valid,
     * based on the EmailValidator API from Apache.
     * @param email
     * @return 
     */
    public boolean isValidEmailAddress(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    
    /**
     * This method evaluates if the email address field is in fact composed of emails
     * coma separated.
     * 
     * @param field The text box.
     * @return 
     */
    public boolean isEmailAddressFieldValid(TextField field){
        String s = field.getText();
        String [] addresses = s.split(",");
        
        for(int i = 0; i < addresses.length; i++){
            if(!isValidEmailAddress(addresses[i].trim()))
                return false;
        }
        return true;
    }
    
    public String stripTags(String htmlMessage){
        return Jsoup.parse(htmlMessage).text();
    }
    
}
