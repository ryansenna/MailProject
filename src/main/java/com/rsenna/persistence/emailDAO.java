/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.persistence;

import com.rsenna.beans.RyanEmail;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import jodd.mail.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Railanderson Sena
 */
public class emailDAO {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Properties credentials;
    private String user;
    private String pass;
    private String url;
    
    public emailDAO() throws IOException {
        credentials = new Properties();
        getCredentialsFromFile(credentials);
    }
    
    /**
     * This method adds an email object to the Database. 
     * @param email
     * @return The number of rows affected. Which is always 1.
     * @throws SQLException 
     */
    public int create(RyanEmail email) throws SQLException
    {
        log.info("Create method started.");
        int numRowAffected = 0;
        Timestamp sentDate = new Timestamp(email.getSentDate().getTime());
        List<byte[]> emailAttachments = toByteArray(email.getAttachments());
        
        String insertIntoEmail = "INSERT INTO EMAIL(EMAILSUBJECT,EMAILSENTDATE) VALUES(?,?);";
        String insertIntoMessages = "INSERT INTO MESSAGES(EMAILTEXT) VALUES(?);";
        String insertIntoFolder = "INSERT INTO FOLDER(FOLDERNAME) VALUES(?);";
        String insertIntoAttachments = "INSERT INTO ATTACHMENTS(MESSAGEFILE) VALUES(?);";
        
        try(Connection conn = DriverManager.getConnection(url, user, pass);
                PreparedStatement psEmail = conn.prepareStatement(insertIntoEmail);
                PreparedStatement psFolder = conn.prepareStatement(insertIntoFolder);
                PreparedStatement psMessages = conn.prepareStatement(insertIntoMessages);
                PreparedStatement psAttachment = conn.prepareStatement(insertIntoAttachments);)
                
        {
            psEmail.setString(1,email.getSubject());
            psEmail.setTimestamp(2, sentDate);
            psFolder.setString(1, email.getFolder());
            
            numRowAffected = psEmail.executeUpdate();
            log.info("# of records created : " + numRowAffected);
          
            numRowAffected = psFolder.executeUpdate();
            log.info("# of records created : " + numRowAffected);
            
            //load the messages into the Database
            for(int i =0;i < email.getAllMessages().size(); i++)
            {
                psMessages.setString(1,email.getAllMessages().get(i).getContent());
                psMessages.executeUpdate();
            }
            
            //load the attachments into the Database
            for(int j = 0; j < emailAttachments.size(); j++)
            {
                psAttachment.setBytes(1,emailAttachments.get(j));
                psAttachment.executeUpdate();
            }
        }       
        return numRowAffected;
    }
    
    /**
     * This method gets the Db credentials from a file.
     * and stores into a Properties Object.
     * @param p the properties object to be loaded
     * @throws IOException if the file does not exist.
     */
    private void getCredentialsFromFile(Properties p) throws IOException
    {
        log.info("Entered in the getCredentialsFromFile method");
        Path txtFile = Paths.get("C:\\Users\\Railanderson Sena\\Documents\\NetBeans\\MailProject"
                        ,"dbcred.properties");
        log.debug("must exist.", txtFile);
        if(Files.exists(txtFile)){
            try(InputStream propFileStream = Files.newInputStream(txtFile);){
                credentials.load(propFileStream);
            }
        }
        log.debug("check if the properties were loaded", p);
        user = credentials.getProperty("user");
        pass = credentials.getProperty("pass");
        url = credentials.getProperty("url");
        log.debug("user loaded ?",user);
        log.debug("pass loaded ?",pass);
        log.debug("url loaded ?",pass);
    }

    private List<byte[]> toByteArray(List<EmailAttachment> attachments) {
        List<byte[]> emailAttachments = new ArrayList<byte[]>();
        
        for(int i = 0; i < attachments.size(); i++)
        {
            emailAttachments.add(attachments.get(i).toByteArray());
        }
        return emailAttachments;
    }
}
