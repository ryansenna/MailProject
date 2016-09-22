/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.interfaces;

import com.rsenna.business.ConfigBean;
import com.rsenna.business.RyanEmail;
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
     * In Other words it will send emails to the server.
     * 
     * @param subject
     * @param content
     * @param receiveEmail
     * @param cc
     * @param bcc
     * @return JagEmail
     */
    public RyanEmail sendEmail(String subject, String content,
            MailAddress[] receive,
            Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc);
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
    public RyanEmail sendWithEmbeddedAndAttachments(String subject,
            String content, MailAddress[] receive,
            Optional<String> fileAttachPath, Optional<String> embeddedPath,
            Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc) throws Exception;
    /**
     * This method will carry out IMAP conversations with the server.
     * It will read messages from the server into the application.
     * @return 
     */
    public ArrayList<RyanEmail> receiveEmail(String received, String receiveedPwd);
    /**
     * It will set the configurations of sending and receiving messages.
     * @param c 
     */
    void setConfigBean(ConfigBean c);
    /**
     * getter for ConfigBean.
     * @return 
     */
    ConfigBean getConfigBean();
}
