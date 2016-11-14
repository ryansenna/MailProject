/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import jodd.mail.ImapSslServer;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;

/**
 *
 * @author 1333612
 */
public class ConfigModule {

    private String smtpServerName;
    private String imapServerName;
    private String sendEmail;
    private String sendEmailPwd;
    private String receiveEmail;
    private String receiveEmailPwd;
    private String url;
    private String user;
    private String pass;
    
    public ConfigModule(){
        
        smtpServerName = "";
        imapServerName = "";
        sendEmail = "";
        sendEmailPwd = "";
        receiveEmail = "";
        receiveEmailPwd="";
        url= "";
        user= "";
        pass = "";
    }
    public ConfigModule(String smtpServerName, String imapServerName,
            String sendEmail, String sendEmailPwd, String receiveEmail,
            String receiveEmailPwd)
    {
        setSmtpServerName(smtpServerName);
        setImapServerName(imapServerName);
        this.sendEmail = sendEmail;
        this.sendEmailPwd = sendEmailPwd;
        this.receiveEmail = receiveEmail;
        this.receiveEmailPwd = receiveEmailPwd;
    }
    
    public ConfigModule(String smtpServerName, String imapServerName,
            String sendEmail, String sendEmailPwd, String receiveEmail,
            String receiveEmailPwd, String url, String user, String pass)
    {
        this(smtpServerName, imapServerName, sendEmail, sendEmailPwd,
                receiveEmail, receiveEmailPwd);
        this.url = url;
        this.user = user;
        this.pass = pass;
    }
    public String getSmtpServerName() {
        return smtpServerName;
    }

    public String getImapServerName() {
        return imapServerName;
    }

    public String getSendEmail() {
        return sendEmail;
    }

    public String getSendEmailPwd() {
        return sendEmailPwd;
    }

    public String getReceiveEmail() {
        return receiveEmail;
    }

    public String getReceiveEmailPwd() {
        return receiveEmailPwd;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public void setSendEmailPwd(String sendEmailPwd) {
        this.sendEmailPwd = sendEmailPwd;
    }

    public void setReceiveEmail(String receiveEmail) {
        this.receiveEmail = receiveEmail;
    }

    public void setReceiveEmailPwd(String receiveEmailPwd) {
        this.receiveEmailPwd = receiveEmailPwd;
    }
    
    public void setSmtpServerName(String smtpServerName) {
        this.smtpServerName = smtpServerName;
    }

    public void setImapServerName(String imapServerName) {
        this.imapServerName = imapServerName;
    }
    /**
     * This method will make the configurations of the smtp server.
     * @param emailAddress 
     * @param emailPwd
     * @return smtpServer
     */
    public SmtpServer<SmtpSslServer> configSmtpServer() {

        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(sendEmail, sendEmailPwd);

        return smtpServer;
    }
    /**
     * This method will make the configurations of the imap server.
     * @param emailReceive
     * @param emailReceivePwd
     * @return imapSslServer
     */
    public ImapSslServer configImapServer() {
        
        ImapSslServer imapSslServer = new ImapSslServer(imapServerName,
                receiveEmail, receiveEmailPwd);
        return imapSslServer;
        
    }

}
