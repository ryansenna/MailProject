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

    public EmailDAO(ConfigModule c) throws SQLException {
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

        String insertIntoEmail = "INSERT INTO EMAIL(EMAILSUBJECT,EMAILSENTDATE, EMAILRCVDDATE) VALUES(?,?,?);";
        String insertIntoMessages = "INSERT INTO MESSAGES(EMAILID,EMAILTEXT) VALUES(?,?);";
        String insertIntoFolder = "INSERT INTO FOLDER(FOLDERNAME,EMAILID) VALUES(?,?);";
        String insertIntoAttachments = "INSERT INTO ATTACHMENTS(MESSAGEFILE, MESSAGEID) VALUES(?, ?);";
        String insertIntoEmailAddesses
                = "INSERT INTO EMAILADDRESS(ADDRESS, EMAILTYPE, EMAILID, SENTADDRESS) VALUES(?,?,?,?)";

        try (PreparedStatement psEmail = conn.prepareStatement(insertIntoEmail);
                PreparedStatement psFolder = conn.prepareStatement(insertIntoFolder);
                PreparedStatement psMessages = conn.prepareStatement(insertIntoMessages);
                PreparedStatement psAttachment = conn.prepareStatement(insertIntoAttachments);
                PreparedStatement psEmailA = conn.prepareStatement(insertIntoEmailAddesses);) {
            //load subjects and dates into database
            psEmail.setString(1, email.getSubject());
            psEmail.setTimestamp(2, sentDate);
            psEmail.setTimestamp(3, email.getRcvDate());

            numRowAffected = psEmail.executeUpdate();
            log.info("# of records created : " + numRowAffected);
            int emailId = getEmailIdFromDb(sentDate);
            //load the folder
            psFolder.setString(1, email.getFolder());
            //load the email Id that this folder belongs to.
            psFolder.setInt(2, emailId);

            numRowAffected = numRowAffected + psFolder.executeUpdate();
            log.info("# of records created : " + numRowAffected);

            //load To Addesses into the database
            loadToAddressesToDb(psEmailA, email, emailId);
            //load CC addresses into the Database
            loadCCAddressesToDb(psEmailA, email, emailId);
            //load BCC addresses into the Database
            loadBCCAddressesToDb(psEmailA, email,emailId);
            //load the messages into the Database
            loadMessagesToDb(psMessages, email, emailId);

            //load the attachments into the Database
            if (emailAttachments != null) {
                loadAttachmentsToDb(psAttachment, emailAttachments, emailId);
            }
        }
        return numRowAffected;
    }

    /**
     * This method creates a list of byte[] from a list of Email Attachments in
     * order to save it to the data base.
     *
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
     *
     * @param t
     * @return the id from email
     * @throws SQLException
     */
    private int getEmailIdFromDb(Timestamp t) throws SQLException {

        String query = "SELECT EMAILID FROM EMAIL WHERE EMAILSENTDATE = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setTimestamp(1, t);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    private void loadToAddressesToDb(PreparedStatement psEmailA,
            RyanEmail email, int emailId)
            throws SQLException {
        
        for (int k = 0; k < email.getTo().length; k++) {
            //load the To field address
            psEmailA.setString(1, email.getTo()[k].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "to");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //load From to the Db
            psEmailA.setString(4, email.getFrom().getEmail());
            //exec.
            psEmailA.executeUpdate();
        }

    }

    private void loadMessagesToDb(PreparedStatement psMessages,
            RyanEmail email, int emailId) throws SQLException {
        for (int i = 0; i < email.getAllMessages().size(); i++) {
            psMessages.setInt(1, emailId);
            psMessages.setString(2, email.getAllMessages().get(i).getContent());
            psMessages.executeUpdate();
        }
    }

    private void loadAttachmentsToDb(PreparedStatement psAttachment,
            List<byte[]> emailAttachments,int emailId) throws SQLException {

        //get the message Id form the messages table
        int mId = getMessageIdFromDb(emailId);
        //load the attachments into the Database
        for (int j = 0; j < emailAttachments.size(); j++) {
            psAttachment.setBytes(1, emailAttachments.get(j));
            psAttachment.setInt(2, mId);
            psAttachment.executeUpdate();
        }
    }

    private int getMessageIdFromDb(int emailId) throws SQLException {
        String query = "SELECT MESSAGEID FROM MESSAGES WHERE EMAILID = ?";
        
         try (PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    private void loadCCAddressesToDb(PreparedStatement psEmailA, 
            RyanEmail email, int emailId) throws SQLException
    {
        for(int i = 0; i < email.getCc().length; i++)
        {
            //load the To field address
            psEmailA.setString(1, email.getCc()[i].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "cc");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //load From to the Db
            psEmailA.setString(4, email.getFrom().getEmail());
            //exec.
            psEmailA.executeUpdate();
        }
    }

    private void loadBCCAddressesToDb(PreparedStatement psEmailA,
            RyanEmail email, int emailId) throws SQLException
    {
        for(int i = 0; i < email.getBcc().length; i++)
        {
            //load the To field address
            psEmailA.setString(1, email.getBcc()[i].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "bcc");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //load From to the Db
            psEmailA.setString(4, email.getFrom().getEmail());
            //exec.
            psEmailA.executeUpdate();
        }
    }
}
