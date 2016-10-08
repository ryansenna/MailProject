/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.mail.Flags;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailAttachmentBuilder;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import jodd.mail.ReceivedEmail;

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
    private List<RyanEmail> attachedMessages;
    private Flags flags;
    private int messageNumber;
    private LocalDateTime rcvDate;
    
    public RyanEmail(){
        super();
        attachedMessages = new ArrayList<RyanEmail>();
        flags = new Flags();
        messageNumber =0;
        rcvDate = LocalDateTime.now();
    }
    /**
     * The method compares if two Emails are equals.
     * MUST REDO IT.
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
                    //if(!e.getSentDateAndTime().equals(this.getSentDateAndTime()))
                      //  return false;
                }

            }
        }
        return true;
    }

    public String getFolder() {
        return folder;
    }

    public List<RyanEmail> getAttachedMessages() {
        return attachedMessages;
    }

    public Flags getFlags() {
        return flags;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public LocalDateTime getRcvDate() {
        return rcvDate;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setAttachedMessages(List<RyanEmail> attachedMessages) {
        this.attachedMessages = attachedMessages;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public void setRcvDate(LocalDateTime rcvDate) {
        this.rcvDate = rcvDate;
    }
}
