package me.braydenmeyer.lightweb.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import org.w3c.dom.Document;
import me.braydenmeyer.lightweb.nodes.WebTab;
import me.braydenmeyer.lightweb.util.ResizeHelper;
import me.braydenmeyer.lightweb.util.URLUtil;

import static me.braydenmeyer.lightweb.TheLightWeb.stage;

public class BrowserController {

    @FXML private TabPane tabPane;
    @FXML private TextField addressBar;
    @FXML private JFXButton newTabButton;

    private boolean dragging;
    private Tab previousTab;
    public static IntegerProperty amountOfTabs;
    private double xOffset, yOffset;

    @FXML
    private void initialize() {
        final double maxWidth = 298.0;
        tabPane.getTabs().add(new WebTab());
        amountOfTabs = new SimpleIntegerProperty(tabPane.getTabs().size());

        tabPane.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                double minWidth = ((tabPane.getWidth() - 36.0) / amountOfTabs.doubleValue()) - 10;
                tabPane.setTabMinWidth(minWidth < maxWidth ? minWidth : maxWidth);
            }
        });

        amountOfTabs.addListener((observable, oldValue, newValue) -> {
            if (tabPane.isHover()) return;
            double minWidth = ((tabPane.getWidth() - 36.0) / newValue.doubleValue()) - 10;
            tabPane.setTabMinWidth(minWidth < maxWidth ? minWidth : maxWidth);
        });

        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double minWidth = ((newValue.doubleValue() - 36.0) / amountOfTabs.doubleValue()) - 10;
            tabPane.setTabMinWidth(minWidth < maxWidth ? minWidth : maxWidth);
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof WebTab) {
                Document document = ((WebTab) newValue).getWebView().getEngine().getDocument();
                addressBar.setText(document == null ? "" : document.getDocumentURI());
            }

            if (!dragging)
                previousTab = oldValue;
        });
    }

    /**************************************************************************
     *                                                                         *
     * Window                                                                  *
     *                                                                         *
     **************************************************************************/
    @FXML
    private void onTabPanePress(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && !ResizeHelper.resizingProperty.get()) {
            xOffset = stage.getX() - e.getScreenX();
            yOffset = stage.getY() - e.getScreenY();
        }
    }

    @FXML
    private void onTabPaneDrag(MouseEvent e) {
        dragging = true;
        tabPane.getSelectionModel().select(previousTab);
        for (Tab tab : tabPane.getTabs()) {
            tab.setDisable(true);
        }

        if (stage.isMaximized()) stage.setMaximized(false);

        if (e.getButton() == MouseButton.PRIMARY && !ResizeHelper.resizingProperty.get()) {
            stage.setX(e.getScreenX() + xOffset);
            stage.setY(e.getScreenY() + yOffset);
        }
        dragging = false;
}

    @FXML
    private void onTabPaneRelease() {
        for (Tab tab : tabPane.getTabs()) {
            tab.setDisable(false);
        }
    }

    @FXML
    private void onClose() {
        Platform.exit();
    }

    @FXML
    private void onMaximize() {
        if (!stage.isMaximized()) {
            stage.setMaximized(true);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setHeight(bounds.getHeight());
            stage.setWidth(bounds.getWidth());
            stage.centerOnScreen();
        } else
            stage.setMaximized(false);
    }

    @FXML
    private void onMinimize() {
        stage.setIconified(!stage.isIconified());
    }


    /**************************************************************************
     *                                                                         *
     * Controls                                                                *
     *                                                                         *
     **************************************************************************/
    @FXML
    private void onAddressBarKeyPress(KeyEvent e) {
        if (e.getCode() != KeyCode.ENTER) return;

        WebView content = ((WebTab) tabPane.getSelectionModel().getSelectedItem()).getWebView();
        String url = URLUtil.parse(addressBar.getText());
        content.getEngine().load(url);
        addressBar.setText(url);
    }

    @FXML
    private void onNewTab(ActionEvent e) {
        WebTab webTab = new WebTab();
        tabPane.getTabs().add(webTab);
        tabPane.getSelectionModel().select(webTab);

        amountOfTabs.setValue(amountOfTabs.get() + 1);
    }

    @FXML
    private void onBackButtonPress() {
        WebTab webTab = (WebTab) tabPane.getSelectionModel().getSelectedItem();
        webTab.getWebView().getEngine().getHistory().go(-1);
    }

    @FXML
    private void onForwardButtonPress() {
        WebTab webTab = (WebTab) tabPane.getSelectionModel().getSelectedItem();
        webTab.getWebView().getEngine().getHistory().go(1);
    }

    @FXML
    private void onReloadButtonPress() {
        WebView webView = ((WebTab) tabPane.getSelectionModel().getSelectedItem()).getWebView();
        webView.getEngine().reload();
    }
}
