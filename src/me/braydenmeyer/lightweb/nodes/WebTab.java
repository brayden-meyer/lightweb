package me.braydenmeyer.lightweb.nodes;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import me.braydenmeyer.lightweb.controllers.BrowserController;
import me.braydenmeyer.lightweb.util.ResizeHelper;

import static me.braydenmeyer.lightweb.TheLightWeb.stage;

public class WebTab extends Tab {

    private WebView webView;
    private Region cursorRegion = new Region();

    public WebTab() {
        setText("New Tab");
        setOnCloseRequest(e -> {
            BrowserController.amountOfTabs.set(BrowserController.amountOfTabs.get() - 1);
            if (getTabPane().getTabs().size() == 1) {
                e.consume();
                Platform.exit();
            }
        });

        webView = new WebView();

        WebHistory history = webView.getEngine().getHistory();
        history.currentIndexProperty().addListener((observable, oldValue, newValue) -> {
            WebHistory.Entry entry = history.getEntries().get(newValue.intValue());

            Button backButton = (Button) stage.getScene().lookup("#backButton"),
                    forwardButton = (Button) stage.getScene().lookup("#forwardButton");

            if (newValue.doubleValue() == 0)
                backButton.setDisable(true);
            else
                backButton.setDisable(false);


            if (newValue.doubleValue() == webView.getEngine().getHistory().getEntries().size() - 1)
                forwardButton.setDisable(true);
            else
                forwardButton.setDisable(false);

            ((TextField) stage.getScene().lookup("#addressBar"))
                    .setText(history.getEntries().get(newValue.intValue()).getUrl());
        });

        cursorRegion.pickOnBoundsProperty().bind(ResizeHelper.resizingProperty);
        cursorRegion.cursorProperty().bind(ResizeHelper.cursorProperty);
        StackPane sp = new StackPane(webView, cursorRegion);
        setContent(sp);
    }

    public WebView getWebView() {
        return webView;
    }
}
