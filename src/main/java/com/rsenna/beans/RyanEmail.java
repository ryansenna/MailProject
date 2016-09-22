/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailAttachmentBuilder;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import jodd.mail.ReceivedEmail;

/**
 *
 * @author 1333612
 */
public class RyanEmail extends Email {
    

    public ArrayList<EmailAttachment> getAttachments() {
        return attachments;
    }

    public static String getX_PRIORITY() {
        return X_PRIORITY;
    }

    public static int getPRIORITY_HIGHEST() {
        return PRIORITY_HIGHEST;
    }

    public static int getPRIORITY_HIGH() {
        return PRIORITY_HIGH;
    }

    public static int getPRIORITY_NORMAL() {
        return PRIORITY_NORMAL;
    }

    public static int getPRIORITY_LOW() {
        return PRIORITY_LOW;
    }

    public static int getPRIORITY_LOWEST() {
        return PRIORITY_LOWEST;
    }

    public MailAddress getFrom() {
        return from;
    }

    public MailAddress[] getTo() {
        return to;
    }

    public MailAddress[] getReplyTo() {
        return replyTo;
    }

    public MailAddress[] getCc() {
        return cc;
    }

    public MailAddress[] getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getSubjectEncoding() {
        return subjectEncoding;
    }

    public List<EmailMessage> getMessages() {
        return messages;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setAttachments(ArrayList<EmailAttachment> attachments) {
        this.attachments = attachments;
    }

    public void setFrom(MailAddress from) {
        
        this.from = from;
    }

    public void setTo(MailAddress[] to) {
        this.to = to;
    }

    public void setReplyTo(MailAddress[] replyTo) {
        this.replyTo = replyTo;
    }

    public void setCc(MailAddress[] cc) {
        this.cc = cc;
    }

    public void setBcc(MailAddress[] bcc) {
        this.bcc = bcc;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSubjectEncoding(String subjectEncoding) {
        this.subjectEncoding = subjectEncoding;
    }

    public void setMessages(List<EmailMessage> messages) {
        this.messages = messages;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This static method takes in Received email array and transforms into a
     * List of Ryan Emails.
     * @param emails
     * @return 
     */
    public static ArrayList<RyanEmail> convertToRyanEmail(ReceivedEmail[] receivedEmails)
    {
        //declare types
        ArrayList<RyanEmail> myEmails = new ArrayList<RyanEmail>();
        RyanEmail ryanEmail = new RyanEmail();
        
        //loop through all the Received emails
        for(int i = 0; i < receivedEmails.length;i++)
        {
            ryanEmail.setFrom(receivedEmails[i].getFrom());
            ryanEmail.setTo(receivedEmails[i].getTo());
            ryanEmail.setCc(receivedEmails[i].getCc());
            ryanEmail.setBcc(receivedEmails[i].getBcc());
            ryanEmail.setSubject(receivedEmails[i].getSubject());
            ryanEmail.setMessages(receivedEmails[i].getAllMessages());
            ryanEmail.setAttachments((ArrayList<EmailAttachment>) receivedEmails[i].getAttachments());
            
            myEmails.add(ryanEmail);
            ryanEmail = new RyanEmail();
        }
        //return my list of emails
        return myEmails;
    }
    
}
