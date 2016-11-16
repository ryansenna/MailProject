/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Railanderson Sena
 */
public class FXRyanEmail {

    private StringProperty fromField;
    private StringProperty subjectField;
    private StringProperty dateField;

    public FXRyanEmail() {
        super();
        this.fromField = new SimpleStringProperty("");
        this.subjectField = new SimpleStringProperty("");
        this.dateField = new SimpleStringProperty("");
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

    public void setFromField(String s) {
        fromField.set(s);
    }

    public void setSubjectField(String s) {
        subjectField.set(s);
    }

    public void setDateField(String s) {
        dateField.set(s);
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

}
