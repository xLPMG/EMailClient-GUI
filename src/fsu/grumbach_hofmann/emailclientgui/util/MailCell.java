package fsu.grumbach_hofmann.emailclientgui.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MailCell.fxml"));
            loader.setController(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    
    @Override
    protected void updateItem(MailObject mailObject, boolean empty) {
        

        if(empty || mailObject == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
        	if (mailObject.isSeen()) {
        		cellLabelSender.setStyle("-fx-text-fill:#FFFFFF; -fx-font-size: 14.0pt");
			}else {
				cellLabelSender.setStyle("-fx-text-fill:#0A84FF; -fx-font-size: 16.0pt");
			}
        	
        	cellLabelSender.setText(mailObject.getFrom());
        	cellLabelDate.setText(dateCalc(mailObject.getDateSent()));
        	
        	cellLabelSubject.setText(mailObject.getSubject());
        	cellLabelContent.setText(mailObject.getPreview());
        	
        	cellLabelSender.setWrapText(false);
        	cellLabelDate.setWrapText(false);
        	cellLabelSender.maxWidthProperty().bind(this.widthProperty().divide(4).multiply(3));
        	cellLabelDate.maxWidthProperty().bind(this.widthProperty().divide(4));
        	
        	cellBorderPane.maxWidthProperty().bind(this.widthProperty().subtract(30));
        	
        	cellLabelSubject.setWrapText(false);
        	cellLabelSubject.maxWidthProperty().bind(this.widthProperty().subtract(30));
         
        	cellLabelContent.setWrapText(true);
        	cellLabelContent.maxWidthProperty().bind(this.widthProperty().subtract(30));
     
        	setGraphic(cellMainPane);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        super.updateItem(mailObject, empty);
    }
    
    private String dateCalc(LocalDateTime date) {
		if (date != null) {
			String dateText="";
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
			if(fmt.format(date).equals(fmt.format(LocalDateTime.now()))) {
				dateText = "Today";
			}else if(fmt.format(date).equals(fmt.format(LocalDateTime.now().minusDays(1)))) {
				dateText = "Yesterday";
			}else {
			dateText = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.GERMANY).format(date);
			}
			return dateText;
		} else {
			return "unknown";
		}
	}
}
