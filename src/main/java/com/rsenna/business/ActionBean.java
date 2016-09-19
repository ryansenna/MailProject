/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import com.rsenna.interfaces.Mailer;
import java.util.Arrays;
import java.util.Optional;
import jodd.mail.EmailAddress;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import jodd.util.MimeTypes;

/**
 *
 * @author 1333612
 */
public class ActionBean implements Mailer{
    
    private ConfigBean c;
    private String sendEmail;
    private String sendEmailPwd;
    
    public ActionBean(ConfigBean c, String sendEmail, String sendEmailPwd)
    {
        setConfigBean(c);
        setSendEmail(sendEmail);
        setSendEmailPwd(sendEmailPwd);
    }

    @Override
    public RyanEmail sendEmail(String subject, String content,
            MailAddress[] receiveEmail,
            Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc)
    {
        //Create SMTP Server
        SmtpServer<SmtpSslServer> smtpServer = 
                c.configSmtpServer(sendEmail, sendEmailPwd);
        
        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);
        
        //Create an Email
        RyanEmail email = null;
        if(cc.isPresent() && bcc.isPresent())
            email = createEmailWithBoth(subject,content,receiveEmail, cc, bcc);
        else if(cc.isPresent())
            email = createEmailWithCc(subject,content,receiveEmail, cc);
        else if(bcc.isPresent())
            email = createEmailWithBcc(subject,content,receiveEmail,bcc);
        else
            email = createEmail(subject,content,receiveEmail);
        
         // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the message and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
        //return the email db purposes.
        return email;
    }

    @Override
    public RyanEmail[] receiveEmail()
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setConfigBean(ConfigBean c)
    {
        this.c = new ConfigBean(c.getSmtpServerName(), c.getImapServerName());
    }

    @Override
    public ConfigBean getConfigBean()
    {
        return c;
    }

    private RyanEmail createEmail(String subject, String content,
            MailAddress[] receiveEmail)
    {
        //EmailAddress sendAddress = new EmailAddress(sendEmail);
        MailAddress sending = new MailAddress(sendEmail);
        
        //MailAddress[] receiving = copy(receiveEmail);
        RyanEmail email = new RyanEmail();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        return email;
        
    }

    private MailAddress[] copy(EmailAddress[] receiveEmail) {
        
        MailAddress[] ma = new MailAddress[receiveEmail.length];
        for(int i = 0; i < receiveEmail.length; i++)
        {
            ma[i] = new MailAddress(receiveEmail[i]);
        }
        return ma;
    }

    private RyanEmail createEmailWithBoth(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc) {
        
        MailAddress sending = new MailAddress(sendEmail);
        
        RyanEmail email = new RyanEmail();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        
        return email;
    }

    private RyanEmail createEmailWithBcc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> bcc) {
        
        MailAddress sending = new MailAddress(sendEmail);
        
        RyanEmail email = new RyanEmail();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        
        return email;
    }

    private RyanEmail createEmailWithCc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc)
    {
     
        MailAddress sending = new MailAddress(sendEmail);
        
        RyanEmail email = new RyanEmail();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        
        return email;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public void setSendEmailPwd(String sendEmailPwd) {
        this.sendEmailPwd = sendEmailPwd;
    }
    
    
    
    
}