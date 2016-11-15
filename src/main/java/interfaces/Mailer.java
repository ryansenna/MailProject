/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import business.ConfigModule;
import beans.RyanEmail;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import jodd.mail.EmailAddress;
import jodd.mail.MailAddress;

/**
 *
 * @author 1333612
 */
public interface Mailer {
    
    /**
     * This method will carry out smtp conversations with the server.
     * In other words, it will send emails with attachments or not.
     * @param subject
     * @param content
     * @param receive
     * @param fileAttachPath
     * @param embeddedPath
     * @param cc
     * @param bcc
     * @return
     * @throws Exception 
     */
    public RyanEmail sendEmail(String subject,
            String content, MailAddress[] receive,
            Optional<String> fileAttachPath, Optional<String> embeddedPath,
            Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc) throws Exception;
    /**
     * This method will carry out IMAP conversations with the server.
     * It will read messages from the server into the application.
     * @return 
     */
    public ArrayList<RyanEmail> receiveEmail() throws SQLException;
    /**
     * getter for ConfigBean.
     * @return 
     */
    ConfigModule getConfigBean();
}
