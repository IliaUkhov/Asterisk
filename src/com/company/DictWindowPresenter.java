package com.company;

import com.company.model.Word;
import com.company.model.interfaces.DictionaryOperator;
import com.company.model.interfaces.StressSignedWord;
import com.company.ui.observers.DictChangeObserver;
import com.company.ui.observers.ObservableDict;
import com.company.ui.observers.StylesheetChangeObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static java.lang.Integer.valueOf;

public class DictWindowPresenter
        implements Initializable, StylesheetChangeObserver, ObservableDict {

    @FXML private VBox dict_v_box;
    @FXML private ListView<String> dict_list;
    @FXML private TextField dict_text_field;
    @FXML private Button dict_add_button;
    @FXML private Button dict_rm_button;

    private DictionaryOperator dict;
    private ObservableList<String> listViewData;

    private List<DictChangeObserver> observers = new ArrayList<>();

    public DictWindowPresenter(DictionaryOperator dict) {
        this.dict = dict;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewData = FXCollections.observableList(new ArrayList<>());
        listViewData.addAll(dict.getAll(true));
        dict_list.setItems(listViewData.sorted());
        dict_add_button.setOnAction(event -> {
            addWord(dict_text_field.getText());
            dict_text_field.setText("");
        });
        dict_rm_button.setOnAction(event -> {
            removeWord(dict_list.getSelectionModel().getSelectedItem());
            dict_text_field.setText("");
        });
        dict_text_field.textProperty().addListener((observable, oldValue, newValue)
                -> getProbableWords(newValue));
    }

    private void addWord(String wordAndStressesIndices) {
        String[] input = wordAndStressesIndices.split(" ");
        int[] stresses = new int[input.length - 1];
        for (int i = 1; i < input.length; ++i) {
            stresses[i - 1] = valueOf(input[i]);
        }

        StressSignedWord newWord = new Word(input[0], stresses);
        if (dict.write(newWord)) {
            listViewData.add(wordAndStressesIndices);
            dict.save();
            observers.forEach(DictChangeObserver::handleDictChange);
        }
    }

    private void removeWord(String word) {
        if (word != null && dict.remove(word)) {
            listViewData.remove(word);
            dict.save();
            observers.forEach(DictChangeObserver::handleDictChange);
        }
    }

    private void getProbableWords(String substring) {
        List<String> newList = new ArrayList<>();
        for (String s : dict.getAll(true)) {
            if (s.contains(substring)) {
                newList.add(s);
            }
        }
        listViewData.clear();
        listViewData.addAll(newList);
    }

    public void open(String currentStylesheet) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/dict_window.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root, 300, 200);
        handleStylesheetChange(currentStylesheet);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Dictionary");
        stage.show();
    }

    @Override
    public void handleStylesheetChange(String newStylesheetPath) {
        dict_v_box.getStylesheets().clear();
        dict_v_box.getStylesheets().add("/com/company/ui/stylesheets/settings/default_settings.css");
        dict_v_box.getStylesheets().add(newStylesheetPath);
    }

    @Override
    public void addObserver(DictChangeObserver observer) {
        observers.add(observer);
    }
}