package fsu.grumbach_hofmann.emailclientgui.util;

import java.io.IOException;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class MailCell extends ListCell<MailObject> {

	@FXML
	private AnchorPane cellMainPane;
	
    @FXML
    private Label cellLabelSender;

    @FXML
    private Label cellLabelDate;

    @FXML
    private Label cellLabelSubject;
    
    @FXML
    private Label cellLabelContent;

    public MailCell() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/util/MailCell.fxml"));
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(MailObject mailObject, boolean empty) {
        super.updateItem(mailObject, empty);

        if(empty || mailObject == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
        	cellLabelSender.setText(mailObject.getFrom());
        	cellLabelDate.setText(mailObject.getDateSent());
        	cellLabelSubject.setText(mailObject.getSubject());
        	cellLabelContent.setText(mailObject.getPreview());
        	setGraphic(cellMainPane);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
