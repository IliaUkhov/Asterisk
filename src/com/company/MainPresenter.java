package com.company;

import com.company.model.Dictionary;
import com.company.model.interfaces.DictionaryOperator;
import com.company.model.FootMatcher;
import com.company.model.interfaces.PoemFootMatcher;
import com.company.ui.observers.DictChangeObserver;
import com.company.ui.observers.StylesheetChangeObserver;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class MainPresenter extends Application
        implements Initializable, StylesheetChangeObserver, DictChangeObserver {

    private DictionaryOperator dict = new Dictionary();
    private PoemFootMatcher matcher = new FootMatcher();
    private ThemePresenter themePresenter = new ThemePresenter();
    private DictWindowPresenter dictPresenter = new DictWindowPresenter(dict);

    private String currentStylesheet = "/com/company/ui/stylesheets/Basic.css";

    @FXML private StackPane stack_pane;
    @FXML private TextArea text_view;
    @FXML private Button dict_button;
    @FXML private Text result;
    @FXML private ChoiceBox<String> select_theme;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dict_button.setOnAction(event -> openDictionaryWindow());

        themePresenter.addObserver(this);

        select_theme.setItems(themePresenter.getThemesList());
        select_theme.getSelectionModel().select("Basic");
        select_theme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            themePresenter.applyStylesheet(newValue);
        });
        text_view.textProperty().addListener(((observable, oldValue, newValue) -> {
            showAnalysisResult();
        }));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui/main_window.fxml"));
        Scene scene = new Scene(root, 400, 267);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Asterisk");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showAnalysisResult() {
        result.setText(matcher.analyzePoem(dict, text_view.getText(), 0.5));
    }

    private void openDictionaryWindow() {
        try {
            dictPresenter.open(currentStylesheet);
            dictPresenter.addObserver(this);
            themePresenter.addObserver(dictPresenter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleStylesheetChange(String newStylesheetPath) {
        stack_pane.getStylesheets().clear();
        stack_pane.getStylesheets().add("/com/company/ui/stylesheets/settings/default_settings.css");
        stack_pane.getStylesheets().add(newStylesheetPath);
        currentStylesheet = newStylesheetPath;
    }

    @Override
    public void handleDictChange() {
        showAnalysisResult();
    }
}
