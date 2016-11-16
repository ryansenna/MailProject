

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.StringProperty;
import javax.mail.Flags;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
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
    private List<ReceivedEmail> attachedMessages;
    private List<EmailAttachment> attachments;
    private Flags flags;
    private int messageNumber;
    private Timestamp rcvDate;

    public RyanEmail() {
        super();
        attachedMessages = new ArrayList<ReceivedEmail>();
        attachments = new ArrayList<EmailAttachment>();
        flags = new Flags();
        messageNumber = 0;
        rcvDate = Timestamp.valueOf(LocalDateTime.now());
    }

    /**
     * This Method compares two emails by: From field, Subject field, list of
     * tos, list of ccs and list of attachments.
     *
     * @param e
     * @return true if they are equal. False if they are not.
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
        if (!e.getFrom().getEmail().equals(this.getFrom().getEmail())) {
            return false;
        }
        if (!e.getSubject().equalsIgnoreCase(this.getSubject())) {
            return false;
        }
        // check list of tos
        if (!isToFieldEqual(e)) {
            return false;
        }
        //check list of ccs
        if (!isCCFieldEqual(e)) {
            return false;
        }
        //check list of attachments.
        if (!areAttachmentsEqual(e)) {
            return false;
        }
        return true;
    }

    public String getFolder() {
        return folder;
    }

    public List<ReceivedEmail> getAttachedMessages() {
        return attachedMessages;
    }

    public Flags getFlags() {
        return flags;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public Timestamp getRcvDate() {
        return rcvDate;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setAttachedMessages(List<ReceivedEmail> attachedMessages) {
        this.attachedMessages = attachedMessages;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public void setRcvDate(Timestamp rcvDate) {
        this.rcvDate = rcvDate;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * This method checks if the To field list is equal to another.
     *
     * @param e
     * @return
     */
    private boolean isToFieldEqual(RyanEmail e) {

        MailAddress[] toFromE = e.getTo();
        MailAddress[] toFromThis = this.getTo();

        if (toFromE.length != toFromThis.length) {
            return false;
        }
        for (int i = 0; i < toFromE.length; i++) {

            if (!toFromE[i].getEmail().equalsIgnoreCase(toFromThis[i].getEmail())) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks if the CC field list is equal to another.
     *
     * @param e
     * @return
     */
    private boolean isCCFieldEqual(RyanEmail e) {
        //check for the list of cc
        MailAddress[] ccFromE = e.getCc();
        MailAddress[] ccFromThis = this.getCc();
        //if they dont have the same size, therefore they are not equal.
        if (ccFromE.length != ccFromThis.length) {
            return false;
        }
        //iterate through all the cc list comparing each one for equality.
        //if one doesnt match, the object is not equal.
        for (int i = 0; i < ccFromE.length; i++) {
            if (!ccFromE[i].getEmail().equalsIgnoreCase(ccFromThis[i].getEmail())) {
                return false;
            }
        }

        return true;
    }

    /**
     * This Method checks if a list of attachments is equal to another.
     *
     * @param e
     * @return
     */
    private boolean areAttachmentsEqual(RyanEmail e) {

        //check the list of attachments
        List<EmailAttachment> fromE = e.getAttachments();//might return null
        List<EmailAttachment> fromThis = this.getAttachments();//might return null

        if (fromE == null && fromThis == null) {
            return true;
        }
        // if one of then references null so they are not equal
        if ((fromE == null && fromThis != null) || (fromE != null && fromThis == null)) {
            return false;
        }
        // if their sizes are different therefore they are not equal.
        if (fromE.size() != fromThis.size()) {
            return false;
        }
        //if one of the elements is different so they are not equal.
        for (int i = 0; i < fromE.size(); i++) {
            //compare every email attachment object by its size.
            if (fromE.get(i).getSize() != fromThis.get(i).getSize()) {
                return false;
            }
        }

        return true;
    }

}
