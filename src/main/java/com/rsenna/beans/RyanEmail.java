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
import org.joda.time.DateTime;

/**
 * RyanEmail is a definition class with the purpose to give a definition for an
 * e-mail. The class uses mostly Jodd Email class. However, it contains a method
 * that merges ReceivedEmail with Email. RyanEmail overrides Equals, HashCode
 * and toString.
 *
 * @author Railanderson "Ryan" Sena
 */
public class RyanEmail extends Email {

    private String folder = "";
    private DateTime sentDateAndTime;

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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

   
    public DateTime getSentDateAndTime() {
        return sentDateAndTime;
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

    public void setSentDateAndTime(DateTime sentDateAndTime) {
        this.sentDateAndTime = sentDateAndTime;
    }

    /**
     * The method compares if two Emails are equals.
     *
     * @param e
     * @return
     */
    public boolean compareEmails(RyanEmail e) {
        if (e == null) {
            return false;
        }
        if (e == this) {
            return true;
        }
        if (!(e instanceof RyanEmail)) {
            return false;
        }
        // Two Emails are equal if they were sent by the same person,
        // if they have the same subject and list of attachments.
        if (e.getFrom().getEmail().equals(this.getFrom().getEmail())) {
            if (e.getSubject().equalsIgnoreCase(this.getSubject())) {
                if (e.getAttachments() != null && this.getAttachments() != null) {
                    if (!e.getAttachments().isEmpty() && !this.getAttachments().isEmpty()) {
                        List<EmailAttachment> attachments = e.getAttachments();

                        for (int i = 0; i < attachments.size(); i++) {
                            if (!attachments.get(i).getName().
                                    equalsIgnoreCase(this.getAttachments().get(i).getName())) {
                                return false;
                            }
                        }
                    }
                    else
                        return false;
                } else {
                    if(!e.getSentDateAndTime().equals(this.getSentDateAndTime()))
                        return false;
                }

            }
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * This static method takes in Received email array and transforms into a
     * List of Ryan Emails.
     *
     * @param emails
     * @return
     */
    public static ArrayList<RyanEmail> convertToRyanEmail(ReceivedEmail[] receivedEmails) {
        //declare types
        ArrayList<RyanEmail> myEmails = new ArrayList<RyanEmail>();
        RyanEmail ryanEmail = new RyanEmail();
        DateTime dt = new DateTime(); 
       //loop through all the Received emails
        for (int i = 0; i < receivedEmails.length; i++) {
            dt = new DateTime(receivedEmails[i].getSentDate());
            ryanEmail.setFrom(receivedEmails[i].getFrom());
            ryanEmail.setTo(receivedEmails[i].getTo());
            ryanEmail.setCc(receivedEmails[i].getCc());
            ryanEmail.setBcc(receivedEmails[i].getBcc());
            ryanEmail.setSentDateAndTime(dt);
            ryanEmail.setSubject(receivedEmails[i].getSubject());
            ryanEmail.setMessages(receivedEmails[i].getAllMessages());
            ryanEmail.setAttachments((ArrayList<EmailAttachment>) receivedEmails[i].getAttachments());
            ryanEmail.setFolder("inbox");

            myEmails.add(ryanEmail);
            ryanEmail = new RyanEmail();
        }
        //return my list of emails
        return myEmails;
    }

}
