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

    public ConfigModule(String smtpServerName, String imapServerName) {
        setSmtpServerName(smtpServerName);
        setImapServerName(imapServerName);
    }

    public String getSmtpServerName() {
        return smtpServerName;
    }

    public String getImapServerName() {
        return imapServerName;
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
    public SmtpServer<SmtpSslServer> configSmtpServer(String emailAddress,
            String emailPwd) {

        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailAddress, emailPwd);

        return smtpServer;
    }
    /**
     * This method will make the configurations of the imap server.
     * @param emailReceive
     * @param emailReceivePwd
     * @return imapSslServer
     */
    public ImapSslServer configImapServer(String emailReceive,
            String emailReceivePwd) {
        
        ImapSslServer imapSslServer = new ImapSslServer(imapServerName,
                emailReceive, emailReceivePwd);
        return imapSslServer;
        
    }

}
