/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

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
    
    private void setSmtpServerName(String smtpServerName) {
        this.smtpServerName = smtpServerName;
    }

    private void setImapServerName(String imapServerName) {
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
