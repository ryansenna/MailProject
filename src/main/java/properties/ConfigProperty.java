/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import business.ConfigModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jodd.mail.ImapSslServer;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1333612
 */
public class ConfigProperty {

    private final StringProperty userEmailAddress;
    private final StringProperty password;
    private final StringProperty smtpServerName;
    private final StringProperty imapServerName;
    private final StringProperty dbUsername;
    private final StringProperty dbPass;
    private String url;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public ConfigProperty() {
        super();
        this.userEmailAddress = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.imapServerName = new SimpleStringProperty("");
        this.smtpServerName = new SimpleStringProperty("");
        this.dbUsername = new SimpleStringProperty("");
        this.dbPass = new SimpleStringProperty("");
        url = "";
    }

    public String getUserEmailAddress() {
        return userEmailAddress.get();
    }

    public String getPassword() {
        return password.get();
    }

    public String getSmtpServerName() {
        return smtpServerName.get();
    }

    public String getImapServerName() {
        return imapServerName.get();
    }

    public String getDbUsername() {
        return dbUsername.get();
    }

    public String getDbPass() {
        return dbPass.get();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String s) {
        url = s;
    }

    public void setUserEmailAddress(String s) {
        userEmailAddress.set(s);
    }

    public void setPassword(String s) {
        password.set(s);
    }

    public void setSmtpServerName(String s) {
        smtpServerName.set(s);
    }

    public void setImapServerName(String s) {
        imapServerName.set(s);
    }

    public void setDbUsername(String s) {
        dbUsername.set(s);
    }

    public void setDbPass(String s) {
        dbPass.set(s);
    }

    public StringProperty userEmailAddress() {
        return userEmailAddress;
    }

    public StringProperty password() {
        return password;
    }

    public StringProperty smtpServerName() {
        return smtpServerName;
    }

    public StringProperty imapServerName() {
        return imapServerName;
    }

    public StringProperty dbUsername() {
        return dbUsername;
    }

    public StringProperty dbPass() {
        return dbPass;
    }

    /**
     * This method will make the configurations of the imap server.
     *
     * @param emailReceive
     * @param emailReceivePwd
     * @return imapSslServer
     */
    public ImapSslServer configImapServer() {
        ImapSslServer imapSslServer = new ImapSslServer(getImapServerName(),
                getUserEmailAddress(), getPassword());
        return imapSslServer;

    }

    /**
     * This method will make the configurations of the smtp server.
     *
     * @param emailAddress
     * @param emailPwd
     * @return smtpServer
     */
    public SmtpServer<SmtpSslServer> configSmtpServer() {

        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(getSmtpServerName())
                .authenticateWith(getUserEmailAddress(), getPassword());

        return smtpServer;
    }

    /**
     * This method will turn this Config Property into a Config Module in order
     * to be used with the business classes.
     *
     * @return
     */
    public ConfigModule toConfigModule() {

        ConfigModule c = new ConfigModule();

        c.setSendEmail(this.getUserEmailAddress());
        c.setSendEmailPwd(this.getPassword());
        c.setReceiveEmail(this.getUserEmailAddress());
        c.setReceiveEmailPwd(this.getPassword());
        c.setSmtpServerName(getSmtpServerName());
        log.error(c.getSmtpServerName());
        c.setImapServerName(getImapServerName());
        log.error(c.getImapServerName());
        c.setUrl("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612");
        c.setUser(this.getDbUsername());
        c.setPass(this.getDbPass());

        return c;
    }

    @Override
    public String toString() {
        return "MailConfigProperties\n{\t" + "\n\tuserEmailAddress=" + getSmtpServerName() + "\n\tpassword=" + getImapServerName() + "\n}";
    }

}
