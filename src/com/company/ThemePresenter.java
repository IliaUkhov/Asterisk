package com.company;

import com.company.ui.observers.StylesheetChangeObserver;
import com.company.ui.observers.StylesheetSetter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemePresenter implements StylesheetSetter {
    private static final String CSS_FOLDER_PATH = "/Users/ilia/IdeaProjects/asterisk/src/com/company/ui/stylesheets";
    private static final String CSS_DEFAULT_PATH = "/com/company/ui/stylesheets/";

    private ObservableList<String> themes = FXCollections.observableList(new ArrayList<>());
    private File stylesheets_folder =
            new File(CSS_FOLDER_PATH);
    private String stylesheetPath = CSS_FOLDER_PATH + "Basic.css";
    private List<StylesheetChangeObserver> observers = new ArrayList<>();

    ThemePresenter() {
        themes.add("Basic");
        refreshThemesList();
    }

    ObservableList<String> getThemesList() {
        return themes;
    }

    private void refreshThemesList() {
        themes.clear();
        try {
            for (File file : stylesheets_folder.listFiles()) {
                if (file.getName().endsWith(".css")) {
                    themes.add(file.getName().replace(".css", ""));
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Stylesheets folder not found");
        }
    }

    boolean applyStylesheet(String stylesheetName) {
        if (themes.contains(stylesheetName)) {
            stylesheetPath = CSS_DEFAULT_PATH + stylesheetName + ".css";
            notifyObservers();
            return true;
        } else {
            return false;
        }
    }

    public String getCurrentStylesheet() {
        return this.stylesheetPath;
    }

    @Override
    public void addObserver(StylesheetChangeObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (StylesheetChangeObserver observer : observers) {
            observer.handleStylesheetChange(stylesheetPath);
        }
    }
}
