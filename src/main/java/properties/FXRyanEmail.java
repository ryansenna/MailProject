/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jodd.mail.EmailAttachment;

/**
 *
 * @author Railanderson Sena
 */
public class FXRyanEmail {

    private StringProperty fromField;
    private StringProperty subjectField;
    private StringProperty dateField;
    private StringProperty messageField;
    private StringProperty toField;
    private StringProperty ccField;
    private StringProperty folderField;
    private byte[] attachment;

    public FXRyanEmail() {
        super();
        this.fromField = new SimpleStringProperty("");
        this.subjectField = new SimpleStringProperty("");
        this.dateField = new SimpleStringProperty("");
        this.messageField = new SimpleStringProperty("");
        this.toField = new SimpleStringProperty("");
        this.ccField = new SimpleStringProperty("");
        this.folderField = new SimpleStringProperty("");
        this.attachment = new byte[0];
    }

    public String getFromField() {
        return fromField.get();
    }

    public String getSubjectField() {
        return subjectField.get();
    }

    public String getDateField() {
        return dateField.get();
    }

    public String getMessageField() {
        return messageField.get();
    }

    public String getToField() {
        return toField.get();
    }

    public String getCcField() {
        return ccField.get();
    }

    public String getFolderField() {
        return folderField.get();
    }

    public byte[] getAttachment() {
        return attachment;
    }
    

    public void setFromField(String s) {
        fromField.set(s);
    }

    public void setSubjectField(String s) {
        subjectField.set(s);
    }

    public void setDateField(String s) {
        dateField.set(s);
    }

    public void setMessageField(String s) {
        messageField.set(s);
    }

    public void setToField(String s) {
        toField.set(s);
    }

    public void setCcField(String s) {
        ccField.set(s);
    }

    public void setFolderField(String s) {
        folderField.set(s);
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
    

    public StringProperty fromField() {
        return fromField;
    }

    public StringProperty subjectField() {
        return subjectField;
    }

    public StringProperty dateField() {
        return dateField;
    }

    public StringProperty messageField() {
        return messageField;
    }

    public StringProperty toField() {
        return toField;
    }

    public StringProperty ccField() {
        return ccField;
    }

    public StringProperty folderField() {
        return folderField;
    }

}
