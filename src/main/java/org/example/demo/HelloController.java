package org.example.demo;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;





public class HelloController{
    @FXML
    private Button file;
    @FXML
    private Button saveSettings;


    @FXML
    private VBox historyVbox;
    @FXML
    private VBox fileMenu;
    @FXML
    private VBox settings;
    @FXML
    private VBox Catalog;


    @FXML
    private Label currentFilePath;
    @FXML
    private Label blue;
    @FXML
    private Label title;


    @FXML
    private SplitPane leftPane;
    @FXML
    private SplitPane centerPane;
    @FXML
    private SplitPane rightPane;


    @FXML
    private TextArea text;


    private final Button[] historyButtons = new Button[5];
    private final String[] history = new String[5];


    private Preferences settingsPref;
    private Preferences historyPref;
    private ArrayList<Button> catalogButtons;
    @FXML
    private void initialize(){
        settingsPref = Preferences.userRoot().node("demo/settings");
        historyPref = Preferences.userRoot().node("demo/history");
        catalogButtons = new ArrayList<>();

        getSettings();
        readHistory();
        initHistoryButtons();//инициализация кнопок истории при успешном чтении истории из файла
        fileMenu.setVisible(false);

        file.setMinHeight(32);
        blue.setMinHeight(32);
        saveSettings.setMinHeight(32);

        file.setMaxHeight(32);
        blue.setMaxHeight(32);
        saveSettings.setMaxHeight(32);

        file.setMinWidth(60);
        saveSettings.setMinWidth(90);
        blue.setMinWidth(300);

        title.setText("Траектории");

    }

    @FXML
    protected void pressFileButton(){
        fileMenu.setVisible(!fileMenu.isVisible());
        historyVbox.setVisible(false);
        settings.setVisible(false);
    }
    @FXML
    protected void openFile(){
        openFile(null);
    }

    @FXML
    protected void clearAll() {
        text.setText("");
        catalogButtons.clear();
        fileMenu.setVisible(false);
        Catalog.getChildren().clear();
        title.setText("Траектории");

    }

    private void openFile(File file) {
        if (file == null) {
            FileChooser chooser = new FileChooser();
            file = chooser.showOpenDialog(null);
            saveHistory(file);
        }
        if (catalogButtons.size() > 0) {
            for (Button button : catalogButtons) {
                if (button.getText().equals(file.getName()))
                    return;
            }
        }

        Button catalogButton = new Button();
        catalogButton.setMinHeight(30);
        catalogButton.setMaxHeight(30);
        catalogButton.setMaxWidth(1920);
        VBox.setMargin(catalogButton, new Insets(-1, -1, 0, -1));
        catalogButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Button.css")).toString());
        catalogButton.getStyleClass().add("file");
        catalogButton.setText(file.getName());

        File finalFile = file;
        File finalFile1 = file;
        catalogButton.setOnAction(actionEvent -> {
            fillFilePath(finalFile);
            title.setText("Траектории - " + finalFile1.getName());
            try {
                text.setText(Files.readString(Paths.get(finalFile.getAbsolutePath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        catalogButtons.add(catalogButton);
        Catalog.getChildren().add(catalogButton);


    }
    @FXML
    protected void pressSettings(){
        settings.setVisible(!settings.isVisible());
        fileMenu.setVisible(false);
    }

    @FXML
    protected void pressSaveSettings(){
        settingsPref.putDouble("left", leftPane.getDividerPositions()[0]);
        settingsPref.putDouble("right", rightPane.getDividerPositions()[0]);
        settingsPref.putDouble("center", centerPane.getDividerPositions()[0]);
        settings.setVisible(false);
    }

    private void getSettings() {
        leftPane.setDividerPosition(0, settingsPref.getDouble("left", 0.5));
        rightPane.setDividerPosition(0, settingsPref.getDouble("right", 0.5));
        centerPane.setDividerPosition(0, settingsPref.getDouble("center", 0.5));
    }
    @FXML
    protected void showHistory(){
        historyVbox.setVisible(!historyVbox.isVisible());
    }



    private void saveHistory(File file){

            //чтение из файла истории
            readHistory();

            //сдвиг истории на 1 вправо
            swapHistory();
            history[0] = file.getAbsolutePath();
            //запись новой истории
            for (int i = 0; i < 5; i++) {
                historyPref.put(Integer.toString(i), history[i]);
            }

            updateHistoryButtons();

    }
    //сдвиг истории на 1 вправо
    private void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }

    private void readHistory() {
        for (int i = 0; i < 5; i++) {
            history[i] = historyPref.get(Integer.toString(i), "нет данных");
        }
    }

    //обновление текста и функции при нажатии на кнопки истории
    private void updateHistoryButtons(){
        for(int i=0;i<5;i++){
            if (history[i]==null)
                historyButtons[i].setText("нет данных");
            else
                historyButtons[i].setText(history[i].substring(history[i].lastIndexOf("\\")+1));

            int finalI = i;
            historyButtons[i].setOnAction(actionEvent -> {
                System.out.println(STR."Нажата кнопка чтения файла из истории : \{history[finalI]}");
                openFile(new File(history[finalI]));
            });
        }
    }
    //первоначальное создание кнопок с историей
    private void initHistoryButtons() {
        for(int i=0;i<5;i++) {
            historyButtons[i] = new Button();
            historyButtons[i].setMinHeight(32);
            historyButtons[i].setMaxHeight(32);
            historyButtons[i].setMaxWidth(190);
            historyButtons[i].setMinWidth(190);
            historyButtons[i].getStylesheets().add(Objects.requireNonNull(getClass().getResource("Button.css")).toString());
            historyButtons[i].getStyleClass().add("file");
            VBox.setMargin(historyButtons[i],new Insets(-1,0,0,0));
            historyVbox.getChildren().add(historyButtons[i]);
        }
        updateHistoryButtons();
    }


    private void fillFilePath(File file){
        currentFilePath.setText(file.getAbsolutePath());
    }


}