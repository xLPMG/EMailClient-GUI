package fsu.grumbach_hofmann.emailclientgui.util;

import java.io.IOException;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
    
    @FXML
    private BorderPane cellBorderPane;

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
        	if (mailObject.isSeen()) {
				cellLabelSender.getStyleClass().remove("message-unseen");
			}else {
				cellLabelSender.getStyleClass().add("message-unseen");
			}
        	
        	cellLabelSender.setText(mailObject.getFrom());
        	cellLabelDate.setText(mailObject.getDateSent().toString());
        	cellLabelSubject.setText(mailObject.getSubject());
        	cellLabelContent.setText(mailObject.getPreview());
        	
        	cellLabelSender.setWrapText(false);
        	cellLabelDate.setWrapText(false);
        	cellLabelSender.maxWidthProperty().bind(this.widthProperty().divide(2));
        	cellLabelDate.maxWidthProperty().bind(this.widthProperty().divide(2));
        	
        	cellBorderPane.maxWidthProperty().bind(this.widthProperty().subtract(30));
        	
        	cellLabelSubject.setWrapText(false);
        	cellLabelSubject.maxWidthProperty().bind(this.widthProperty().subtract(30));
        	
        	cellLabelContent.setWrapText(true);
        	cellLabelContent.maxWidthProperty().bind(this.widthProperty().subtract(30));
     
        	setGraphic(cellMainPane);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            
           
        }
    }
}
