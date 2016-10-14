/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import beans.RyanEmail;
import business.ConfigModule;
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
public class EmailDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConfigModule c;
    private Connection conn;

    public EmailDAO(ConfigModule c) throws SQLException{
        this.c = c;
        conn = DriverManager.getConnection(c.getUrl(), c.getUser(), c.getPass());
    }

    /**
     * This method adds an email object to the Database.
     *
     * @param email
     * @return The number of rows affected. Which is always 1.
     * @throws SQLException
     */
    public int create(RyanEmail email) throws SQLException {
        log.info("Create method started.");
        int numRowAffected = 0;
        Timestamp sentDate = new Timestamp(email.getSentDate().getTime());
        List<byte[]> emailAttachments = null;
        if (email.getAttachments() != null) {
            emailAttachments = toByteArray(email.getAttachments());
        }

        String insertIntoEmail = "INSERT INTO EMAIL(EMAILSUBJECT,EMAILSENTDATE) VALUES(?,?);";
        String insertIntoMessages = "INSERT INTO MESSAGES(EMAILID,EMAILTEXT) VALUES(?,?);";
        String insertIntoFolder = "INSERT INTO FOLDER(FOLDERNAME) VALUES(?);";
        String insertIntoAttachments = "INSERT INTO ATTACHMENTS(MESSAGEFILE) VALUES(?);";
        String insertIntoEmailAddesses=
                "INSERT INTO EMAILADDRESS(ADDRESS, EMAILTYPE, EMAILID) VALUES(?,?,?)";

        try (PreparedStatement psEmail = conn.prepareStatement(insertIntoEmail);
                PreparedStatement psFolder = conn.prepareStatement(insertIntoFolder);
                PreparedStatement psMessages = conn.prepareStatement(insertIntoMessages);
                PreparedStatement psAttachment = conn.prepareStatement(insertIntoAttachments);
                PreparedStatement psEmailA = conn.prepareStatement(insertIntoEmailAddesses);)
        {
            //load subjects and dates into database
            psEmail.setString(1, email.getSubject());
            psEmail.setTimestamp(2, sentDate);
            
        
            //load the folder
            psFolder.setString(1, email.getFolder());

            numRowAffected = psEmail.executeUpdate();
            log.info("# of records created : " + numRowAffected);

            numRowAffected = numRowAffected + psFolder.executeUpdate();
            log.info("# of records created : " + numRowAffected);

            //load To Addesses into the database
            for (int k = 0; k < email.getTo().length; k++) {
                psEmailA.setString(1, email.getTo()[k].getEmail());
                psEmailA.setString(2,"to");
                psEmailA.setInt(3,getEmailIdFromDb(sentDate));
                psEmailA.executeUpdate();
            }
            //load the messages into the Database
            for (int i = 0; i < email.getAllMessages().size(); i++) {
                psMessages.setInt(1,getEmailIdFromDb(sentDate));
                psMessages.setString(2, email.getAllMessages().get(i).getContent());
                psMessages.executeUpdate();
            }

            if (emailAttachments != null) {
                //load the attachments into the Database
                for (int j = 0; j < emailAttachments.size(); j++) {
                    psAttachment.setBytes(1, emailAttachments.get(j));
                    psAttachment.executeUpdate();
                }
            }
        }
        return numRowAffected;
    }
    /**
     * This method creates a list of byte[] from a list of Email Attachments
     * in order to save it to the data base.
     * @param attachments
     * @return a list of byte[]
     */
    private List<byte[]> toByteArray(List<EmailAttachment> attachments) {
        List<byte[]> emailAttachments = new ArrayList<byte[]>();

        for (int i = 0; i < attachments.size(); i++) {
            emailAttachments.add(attachments.get(i).toByteArray());
        }
        return emailAttachments;
    }
    
    /**
     * This method will get an emailID based on the Date that it got into the db
     * since sent dates are unique, this is the ebst form to add to it.
     * @param t
     * @return the id from email
     * @throws SQLException 
     */
    private int getEmailIdFromDb(Timestamp t) throws SQLException{
        
        String query = "SELECT EMAILID FROM EMAIL WHERE EMAILSENTDATE = ?";
        try(PreparedStatement ps  = conn.prepareStatement(query);)
        {
            ps.setTimestamp(1, t);
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    return rs.getInt(1);
                }
                else
                    return -1;
            }
        }
    }
}
