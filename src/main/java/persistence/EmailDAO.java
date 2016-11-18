/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import beans.RyanEmail;
import business.ConfigModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jodd.mail.*;
import jodd.util.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.ConfigProperty;
import properties.FXRyanEmail;

/**
 *
 * @author Railanderson Sena
 */
public class EmailDAO {

    private ConfigProperty c;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public EmailDAO(ConfigProperty c) {
        this.c = c;
        //buildDB();
    }

    public void buildDB() {
        final String seedDataScript = loadAsString("emailTables.sql");
        try (Connection connection = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
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

        String insertIntoEmail = "INSERT INTO EMAIL(EMAILSUBJECT,EMAILSENTDATE, EMAILRCVDDATE, SENDERADDRESS) VALUES(?,?,?,?);";
        String insertIntoMessages = "INSERT INTO MESSAGES(EMAILID,EMAILTEXT) VALUES(?,?);";
        String insertIntoFolder = "INSERT INTO FOLDER(FOLDERNAME,EMAILID) VALUES(?,?);";
        String insertIntoAttachments = "INSERT INTO ATTACHMENTS(MESSAGEFILE, MESSAGEID) VALUES(?, ?);";
        String insertIntoEmailAddesses
                = "INSERT INTO EMAILADDRESS(ADDRESS, EMAILTYPE, EMAILID) VALUES(?,?,?)";
        log.error(c.getUrl() + " " + c.getDbUsername() + " " + c.getDbPass());
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement psEmail = conn.prepareStatement(insertIntoEmail);
                PreparedStatement psFolder = conn.prepareStatement(insertIntoFolder);
                PreparedStatement psMessages = conn.prepareStatement(insertIntoMessages);
                PreparedStatement psAttachment = conn.prepareStatement(insertIntoAttachments);
                PreparedStatement psEmailA = conn.prepareStatement(insertIntoEmailAddesses);) {
            //load subjects and dates into database
            psEmail.setString(1, email.getSubject());
            psEmail.setTimestamp(2, sentDate);
            psEmail.setTimestamp(3, email.getRcvDate());
            psEmail.setString(4, email.getFrom().getEmail());

            numRowAffected = psEmail.executeUpdate();
            log.info("# of records created : " + numRowAffected);
            int emailId = getEmailIdFromDb(email.getRcvDate());
            //load the folder
            psFolder.setString(1, email.getFolder());
            //load the email Id that this folder belongs to.
            psFolder.setInt(2, emailId);

            numRowAffected = numRowAffected + psFolder.executeUpdate();
            log.info("# of records created : " + numRowAffected);

            //load To Addesses into the database
            numRowAffected += loadToAddressesToDb(psEmailA, email, emailId);
            //load CC addresses into the Database
            numRowAffected += loadCCAddressesToDb(psEmailA, email, emailId);
            //load BCC addresses into the Database
            numRowAffected += loadBCCAddressesToDb(psEmailA, email, emailId);
            //load the messages into the Database
            numRowAffected += loadMessagesToDb(psMessages, email, emailId);

            //load the attachments into the Database
            if (emailAttachments != null) {
                numRowAffected += loadAttachmentsToDb(psAttachment, emailAttachments, emailId);
            }
        }
        return numRowAffected;
    }

    /**
     * This method deletes an Email from the database based on its received date.
     *
     * @param email
     * @return the number of rows affected
     */
    
    public int delete(String time) throws SQLException {
        int numOfRowsAffected = 0;
        Timestamp t = Timestamp.valueOf(time);
        int id = this.getEmailIdFromDb(t);
        int msgId = this.getMessageIdFromDb(id);

        numOfRowsAffected += deleteFromEmail(id);
        numOfRowsAffected += deleteFromFolder(id);
        numOfRowsAffected += deleteFromMessage(id);
        numOfRowsAffected += deleteFromEmailAddress(id);
        numOfRowsAffected += deleteFromAttachment(msgId);
        
        return numOfRowsAffected;
    }

    private int deleteFromEmail(int id) throws SQLException {
        int numOfRowsAffected = 0;
        String deleteFromEmail = "DELETE FROM EMAIL WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(deleteFromEmail);) {
            ps.setInt(1, id);
            numOfRowsAffected = ps.executeUpdate();
        }
        return numOfRowsAffected;

    }

    private int deleteFromFolder(int id) throws SQLException {
        int numOfRowsAffected = 0;
        String deleteFromFolder = "DELETE FROM FOLDER WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(deleteFromFolder);) {
            ps.setInt(1, id);
            numOfRowsAffected = ps.executeUpdate();
        }
        return numOfRowsAffected;
    }

    private int deleteFromMessage(int id) throws SQLException {
        int numOfRowsAffected = 0;
        String deleteFromMessage = "DELETE FROM MESSAGES WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(deleteFromMessage);) {
            ps.setInt(1, id);
            numOfRowsAffected = ps.executeUpdate();
        }
        return numOfRowsAffected;
    }

    private int deleteFromEmailAddress(int id) throws SQLException {
        int numOfRowsAffected = 0;
        String deleteFromEmailAddress = "DELETE FROM EMAILADDRESS WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(deleteFromEmailAddress);) {
            ps.setInt(1, id);
            numOfRowsAffected = ps.executeUpdate();
        }
        return numOfRowsAffected;
    }

    private int deleteFromAttachment(int id) throws SQLException {
        int numOfRowsAffected = 0;
        String query = "DELETE FROM ATTACHMENTS WHERE MESSAGEID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, id);
            numOfRowsAffected = ps.executeUpdate();
        }
        return numOfRowsAffected;
    }

    /**
     * This method will retrieve all Emails from the database.
     *
     * @return the list of emails.
     * @throws SQLException
     */
    public ObservableList<FXRyanEmail> findAllForFX() throws SQLException {
        ObservableList<FXRyanEmail> emails = FXCollections
                .observableArrayList();

        String query = "SELECT * FROM EMAIL";
        log.error(c.getUrl() + ", " + c.getDbUsername() + ", " + c.getDbPass());
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                RyanEmail email = createEmail(rs);
                emails.add(email.toFX());
            }

        }
        return emails;
    }

    /**
     * This method will get a folder name based on the Id
     *
     * @param emailId
     * @return
     * @throws SQLException
     */
    public String getFolderName(int emailId) throws SQLException {
        String folderName = "";
        log.error("Email Id was entered " + emailId);
        log.error(c.getUrl() + ", " + c.getDbUsername() + ", " + c.getDbPass());

        String query = "select folderName from folder where emailId = ?";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                folderName = rs.getString("folderName");
            }

        }
        return folderName;

    }

    /**
     * This method will get all the folder names in my database.
     *
     * @return
     * @throws SQLException
     */
    public List<String> getAllFolderNames() throws SQLException {
        List<String> folderNames = new ArrayList<>();
        log.error(c.getUrl() + ", " + c.getDbUsername() + ", " + c.getDbPass());

        String query = "select folderName from folder";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                String folderName = rs.getString("folderName");
                folderNames.add(folderName);
            }

        }
        return folderNames;
    }

    /**
     * This method will get all folder names that are not inbox or sent since
     * those are the default ones.
     *
     * @return
     * @throws SQLException
     */
    public List<String> getFolderNamesThatAreNotInboxOrSent() throws SQLException {
        List<String> otherThanInboxSent = new ArrayList<String>();
        List<String> folderNames = getAllFolderNames();
        for (String folderName : folderNames) {
            if (!folderName.equalsIgnoreCase("inbox") && !folderName.equalsIgnoreCase("sent")) {
                otherThanInboxSent.add(folderName);
            }
        }
        return otherThanInboxSent;
    }

    /**
     * This method will retrieve all Emails from the database.
     *
     * @return the list of emails.
     * @throws SQLException
     */
    public List<RyanEmail> findAll() throws SQLException {
        List<RyanEmail> emails = new ArrayList<RyanEmail>();

        String query = "SELECT * FROM EMAIL";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                emails.add(createEmail(rs));
            }

        }
        return emails;
    }

    /**
     * This method, given a folder name, will find all email ids that belong to
     * that folder name, retrieve the email correspondent to that email id add
     * to a list of emails and return it.
     *
     * @param folderName
     * @return
     * @throws SQLException
     */
    public ObservableList<FXRyanEmail> findAllForFolder(String folderName) throws SQLException {
        ObservableList<FXRyanEmail> emails = FXCollections
                .observableArrayList();
        List<Integer> emailIds = getEmailIdsFromFolder(folderName);// get all email ids that belong to a folder.
        // loop trough the ids to get a single email so I can add to my list and display.
        for (Integer i : emailIds) {
            RyanEmail email = findEmail(i.intValue());
            log.error(email.getFolder());
            emails.add(email.toFX());
        }
        return emails;
    }

    /**
     * This method will find a particular email based on its id.
     *
     * @param id
     * @return the email found
     * @throws SQLException
     */
    public RyanEmail findEmail(int id) throws SQLException {

        RyanEmail email = new RyanEmail();

        String query = "SELECT * FROM EMAIL WHERE EMAILID = ?";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    email = createEmail(rs);
                }
            }
        }
        return email;
    }

    /**
     * This method will save to the database the received emails.
     *
     * @param emails
     * @throws SQLException
     */
    public void saveReceivedEmailsToDb(ArrayList<RyanEmail> emails) throws SQLException {
        for (int i = 0; i < emails.size(); i++) {
            this.create(emails.get(i));
        }
    }

    /**
     * This method will get all emails given a particular folder name.
     *
     * @param folderName
     * @return
     */
    private List<Integer> getEmailIdsFromFolder(String folderName) throws SQLException {
        List<Integer> emails = new ArrayList<Integer>();
        String query = "SELECT EMAILID FROM FOLDER WHERE FOLDERNAME = ?";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, folderName);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    emails.add(rs.getInt("emailId"));
                }
            }
        }
        return emails;
    }

    /**
     * This method will create an Email based on the result set(row) passed
     * through.
     *
     * @param rs the row.
     * @return the email created.
     * @throws SQLException
     */
    private RyanEmail createEmail(ResultSet rs) throws SQLException {

        int emailId = rs.getInt("emailId");
        int messageId = getMessageIdFromDb(emailId);
        RyanEmail email = new RyanEmail();
        email.setSubject(rs.getString("emailsubject"));
        email.setSentDate(rs.getTimestamp("emailSentDate"));
        email.setRcvDate(rs.getTimestamp("emailRcvdDate"));
        email.setFrom(new MailAddress(rs.getString("senderAddress")));

        // get the messages at the messages table
        email.addMessage(getMessageTextFromDb(emailId));
        // get the folder from the folder table.
        email.setFolder(getFolderNameFromDb(emailId));
        //get the TO addresses from the emailaddress table.
        email.setTo(getReceiverAddressFromDb(emailId));
        //get the CC addresses from the emailaddress table
        email.setCc(getCCAddressFromDb(emailId));
        //get the bcc addresses from the emailaddress table.
        email.setBcc(getBccAddressFromDb(emailId));
        //get the attachments from the attachments table
        List<byte[]> a = getAttachmentFromDb(messageId);
        for (int i = 0; i < a.size(); i++) {
            email.attach(EmailAttachment.attachment().bytes(a.get(i)));
        }

        return email;
    }

    private FXRyanEmail createFXRyanEmail(ResultSet rs) throws SQLException {

        int emailId = rs.getInt("emailId");
        int messageId = getMessageIdFromDb(emailId);
        FXRyanEmail email = new FXRyanEmail();
        email.setSubjectField(rs.getString("emailsubject"));
        email.setDateField(rs.getTimestamp("emailRcvdDate").toString());
        email.setFromField(rs.getString("senderAddress"));

        return email;
    }

    /**
     * This method will find all the attachments related to a message id.
     *
     * @param mId
     * @return a list of attachments
     * @throws SQLException
     */
    private List<byte[]> getAttachmentFromDb(int mId) throws SQLException {
        String query = "SELECT MESSAGEFILE FROM ATTACHMENTS WHERE MESSAGEID = ?";
        List<byte[]> attachments = new ArrayList<byte[]>();
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, mId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    attachments.add(rs.getBytes("messageFile"));
                }
            }
        }
        return attachments;
    }

    /**
     * This method will find all the bcc addresses from the db.
     *
     * @param emailId
     * @return a list of bcc addresses.
     * @throws SQLException
     */
    private MailAddress[] getBccAddressFromDb(int emailId) throws SQLException {
        String query = "SELECT ADDRESS FROM EMAILADDRESS WHERE EMAILID = ? AND EMAILTYPE = 'bcc'";
        List<MailAddress> ma = new ArrayList<MailAddress>();
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    ma.add(new MailAddress(rs.getString("address")));
                }
            }
        }
        MailAddress[] m = new MailAddress[ma.size()];
        return ma.toArray(m);
    }

    /**
     * This method will find all the cc addresses from the db.
     *
     * @param emailId
     * @return a list of cc addresses.
     * @throws SQLException
     */
    private MailAddress[] getCCAddressFromDb(int emailId) throws SQLException {
        String query = "SELECT ADDRESS FROM EMAILADDRESS WHERE EMAILID = ? AND EMAILTYPE = 'cc'";
        List<MailAddress> ma = new ArrayList<MailAddress>();
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    ma.add(new MailAddress(rs.getString("address")));
                }
            }
        }
        MailAddress[] m = new MailAddress[ma.size()];
        return ma.toArray(m);
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

        String query = "SELECT EMAILID FROM EMAIL WHERE EMAILRCVDDATE = ?";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
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

    /**
     * This method will find the folder name related to a email id in the db.
     *
     * @param emailId
     * @return the folder name
     * @throws SQLException
     */
    private String getFolderNameFromDb(int emailId) throws SQLException {
        String query = "SELECT FOLDERNAME FROM FOLDER WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return "";
                }
            }
        }
    }

    /**
     * This method will find the message Id from db based on the email id.
     *
     * @param emailId
     * @return message id.
     * @throws SQLException
     */
    private int getMessageIdFromDb(int emailId) throws SQLException {
        String query = "SELECT MESSAGEID FROM MESSAGES WHERE EMAILID = ?";

        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
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

    /**
     * This method will find the text related to an email from the db.
     *
     * @param emailId
     * @return the email message
     * @throws SQLException
     */
    private EmailMessage getMessageTextFromDb(int emailId) throws SQLException {
        String query = "SELECT EMAILTEXT FROM MESSAGES WHERE EMAILID = ?";
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new EmailMessage(rs.getString(1), MimeTypes.MIME_TEXT_PLAIN);
                } else {
                    return new EmailMessage("", MimeTypes.MIME_TEXT_PLAIN);
                }
            }
        }
    }

    /**
     * this method will find the receiver addresses from the db.
     *
     * @param emailId
     * @return a list of mail addresses.
     * @throws SQLException
     */
    private MailAddress[] getReceiverAddressFromDb(int emailId) throws SQLException {
        String query = "SELECT ADDRESS FROM EMAILADDRESS WHERE EMAILID = ? AND EMAILTYPE = 'TO'";
        List<MailAddress> ma = new ArrayList<MailAddress>();
        try (Connection conn = DriverManager.getConnection(c.getUrl(), c.getDbUsername(), c.getDbPass());
                PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, emailId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    ma.add(new MailAddress(rs.getString("address")));
                }
            }
        }
        MailAddress[] m = new MailAddress[ma.size()];
        return ma.toArray(m);
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }

    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                Scanner scanner = new Scanner(inputStream)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    /**
     * This method will load all the attachments of an email into the db.
     *
     * @param psAttachment
     * @param emailAttachments
     * @param emailId
     * @throws SQLException
     */
    private int loadAttachmentsToDb(PreparedStatement psAttachment,
            List<byte[]> emailAttachments, int emailId) throws SQLException {
        int numRowAffected = 0;
        //get the message Id form the messages table
        int mId = getMessageIdFromDb(emailId);
        //load the attachments into the Database
        for (int j = 0; j < emailAttachments.size(); j++) {
            psAttachment.setBytes(1, emailAttachments.get(j));
            psAttachment.setInt(2, mId);
            numRowAffected += psAttachment.executeUpdate();
        }
        return numRowAffected;
    }

    /**
     * This method will load all the bcc addresses to the database.
     *
     * @param psEmailA
     * @param email
     * @param emailId
     * @throws SQLException
     */
    private int loadBCCAddressesToDb(PreparedStatement psEmailA,
            RyanEmail email, int emailId) throws SQLException {
        int numRowAffected = 0;
        for (int i = 0; i < email.getBcc().length; i++) {
            //load the To field address
            psEmailA.setString(1, email.getBcc()[i].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "bcc");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //exec.
            numRowAffected += psEmailA.executeUpdate();
        }

        return numRowAffected;
    }

    /**
     * This method will load all the cc addresses to Db.
     *
     * @param psEmailA the prepare stmt
     * @param email the email obj.
     * @param emailId the email id.
     * @throws SQLException
     */
    private int loadCCAddressesToDb(PreparedStatement psEmailA,
            RyanEmail email, int emailId) throws SQLException {
        int numRowAffected = 0;
        for (int i = 0; i < email.getCc().length; i++) {
            //load the To field address
            psEmailA.setString(1, email.getCc()[i].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "cc");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //exec.
            numRowAffected += psEmailA.executeUpdate();
        }

        return numRowAffected;
    }

    /**
     * This method will load the messages of an email into the db.
     *
     * @param psMessages
     * @param email
     * @param emailId
     * @throws SQLException
     */
    private int loadMessagesToDb(PreparedStatement psMessages,
            RyanEmail email, int emailId) throws SQLException {
        int numRowAffected = 0;
        for (int i = 0; i < email.getAllMessages().size(); i++) {
            psMessages.setInt(1, emailId);
            psMessages.setString(2, email.getAllMessages().get(i).getContent());
            numRowAffected += psMessages.executeUpdate();
        }

        return numRowAffected;
    }

    /**
     * This method will load all the email addresses of type TO into the Db.
     *
     * @param psEmailA The prepared statemnt
     * @param email the email object
     * @param emailId the email id in the db
     * @throws SQLException
     */
    private int loadToAddressesToDb(PreparedStatement psEmailA,
            RyanEmail email, int emailId)
            throws SQLException {
        int numRowAffected = 0;
        for (int k = 0; k < email.getTo().length; k++) {
            //load the To field address
            psEmailA.setString(1, email.getTo()[k].getEmail());
            //load the type of addresses
            psEmailA.setString(2, "to");
            //load emailId that it belongs to.
            psEmailA.setInt(3, emailId);
            //exec.
            numRowAffected += psEmailA.executeUpdate();
        }

        return numRowAffected;
    }

    private List<String> splitStatements(Reader reader, String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
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
}
