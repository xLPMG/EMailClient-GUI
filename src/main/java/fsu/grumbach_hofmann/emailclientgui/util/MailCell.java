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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	@FXML
	private ImageView seenIndicatorImage;

	private Image blueDotImg;
	private Image transparentDotImg;

	public MailCell(Image blueDotImg, Image transparentDotImg) {
		loadFXML();
		initElementProperties();
		this.blueDotImg = blueDotImg;
		this.transparentDotImg = transparentDotImg;
	}

	private void loadFXML() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MailCell.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void initElementProperties() {
		cellLabelSender.setStyle("-fx-font-size: 14.0pt");
		cellLabelSender.setWrapText(false);
		cellLabelDate.setWrapText(false);
		cellLabelSubject.setWrapText(false);
		cellLabelContent.setWrapText(true);
	}

	@Override
	protected void updateItem(MailObject mailObject, boolean empty) {
		if (empty || mailObject == null) {
			setText(null);
			setContentDisplay(ContentDisplay.TEXT_ONLY);
		} else {
			if (mailObject.isSeen()) {
				seenIndicatorImage.setImage(transparentDotImg);
			} else {
				seenIndicatorImage.setImage(blueDotImg);
			}

			cellLabelSender.setText(mailObject.getFrom());
			cellLabelDate.setText(dateCalc(mailObject.getDateSent()));
			cellLabelSubject.setText(mailObject.getSubject());
			cellLabelContent.setText(mailObject.getPreview());

			cellLabelSender.maxWidthProperty().bind(this.widthProperty().subtract(10).divide(4).multiply(3));
			cellLabelDate.maxWidthProperty().bind(this.widthProperty().subtract(10).divide(4));
			cellBorderPane.maxWidthProperty().bind(this.widthProperty().subtract(10).subtract(30));
			cellLabelSubject.maxWidthProperty().bind(this.widthProperty().subtract(10).subtract(30));
			cellLabelContent.maxWidthProperty().bind(this.widthProperty().subtract(10).subtract(30));

			setGraphic(cellMainPane);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
		super.updateItem(mailObject, empty);
	}

	private String dateCalc(LocalDateTime date) {
		if (date != null) {
			String dateText = "";
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
			if (fmt.format(date).equals(fmt.format(LocalDateTime.now()))) {
				dateText = "Today";
			} else if (fmt.format(date).equals(fmt.format(LocalDateTime.now().minusDays(1)))) {
				dateText = "Yesterday";
			} else {
				dateText = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.GERMANY).format(date);
			}
			return dateText;
		} else {
			return "unknown";
		}
	}
}
