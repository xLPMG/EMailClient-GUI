package fsu.grumbach_hofmann.emailclientgui.util;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MailCellFactory implements Callback<ListView<MailObject>, ListCell<MailObject>> {

    @Override
    public ListCell<MailObject> call(ListView<MailObject> param) {
        return new MailCell();
    }
}
