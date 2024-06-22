package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
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
    private VBox clearVBox;

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
    private AnchorPane tablePane;

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
        initHistoryButtons();
        readHistory();

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
        clearVBox.setVisible(false);
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
        clearVBox.setVisible(false);
        text.clear();
        catalogButtons.clear();
        fileMenu.setVisible(false);
        Catalog.getChildren().clear();
        title.setText("Траектории");
        if (tablePane.getChildren().size() != 1)
            tablePane.getChildren().remove(1);
        fillClear();
    }

    @FXML
    protected void pressClear() {
        clearVBox.setVisible(!clearVBox.isVisible());
        historyVbox.setVisible(false);
    }

    private void fillClear() {
        clearVBox.getChildren().clear();
        for (int i = 0; i < catalogButtons.size(); i++) {
            Button clearOneFile = createButton(catalogButtons.get(i).getText());
            int finalI = i;
            clearOneFile.setOnAction(_ -> {
                if (title.getText().substring(13).equals(catalogButtons.get(finalI).getText())) {
                    if (tablePane.getChildren().size() != 1)
                        tablePane.getChildren().remove(1);
                    currentFilePath.setText("Filepath");
                    text.clear();
                }
                Catalog.getChildren().remove(catalogButtons.get(finalI));
                catalogButtons.remove(finalI);
                fillClear();
            });
            VBox.setMargin(clearOneFile, new Insets(-1, 0, 0, 0));
            clearVBox.getChildren().add(clearOneFile);

        }
    }

    @FXML
    protected void clearHistory() {
        try {
            historyPref.clear();
            readHistory();
            for (int i = 0; i < 5; i++) {
                history[i] = null;
            }
            updateHistoryButtons();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile(File file) {
        if (file == null) {
            FileChooser chooser = new FileChooser();
            file = chooser.showOpenDialog(null);
            if (file == null)
                return;
            saveHistory(file);
        }
        //проверка на то, чтобы не добавлять 2 раза одинаковые кнопки
        //эта проверка создаёт ошибку под номером 1
        if (!catalogButtons.isEmpty()) {
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
        catalogButton.setOnAction(_ -> {
            //заполнение пути
            fillFilePath(finalFile);
            title.setText("Траектории - " + finalFile1.getName());

            try {
                //уставновка названия
                text.setText(Files.readString(Paths.get(finalFile.getAbsolutePath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //заполнение таблицы
            if (tablePane.getChildren().size() != 1)
                tablePane.getChildren().remove(1);
            try (BufferedReader reader = new BufferedReader(new FileReader(finalFile))) {

                ObservableList<line> lines = FXCollections.observableArrayList();

                String str = reader.readLine();
                while (str != null) {
                    if (str.matches("[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8}")) {
                        lines.add(new line(str));
                    }
                    // read next line
                    str = reader.readLine();
                }

                TableView<line> table = new TableView<>(lines);
                TableColumn<line, Double> T = new TableColumn<>("T,c");
                T.setCellValueFactory(new PropertyValueFactory<>("T"));

                TableColumn<line, Double> X = new TableColumn<>("X,м");
                X.setCellValueFactory(new PropertyValueFactory<>("X"));

                TableColumn<line, Double> Y = new TableColumn<>("Y,м");
                Y.setCellValueFactory(new PropertyValueFactory<>("Y"));

                TableColumn<line, Double> Z = new TableColumn<>("Z,м");
                Z.setCellValueFactory(new PropertyValueFactory<>("Z"));

                TableColumn<line, Double> Vx = new TableColumn<>("Vx,м/c");
                Vx.setCellValueFactory(new PropertyValueFactory<>("Vx"));

                TableColumn<line, Double> Vy = new TableColumn<>("Vy,м/c");
                Vy.setCellValueFactory(new PropertyValueFactory<>("Vy"));

                TableColumn<line, Double> Vz = new TableColumn<>("Vz,м/c");
                Vz.setCellValueFactory(new PropertyValueFactory<>("Vz"));

                if (table.getColumns().addAll(T, X, Y, Z, Vx, Vy, Vz)) {

                    AnchorPane.setTopAnchor(table, 24.0);
                    AnchorPane.setLeftAnchor(table, 0.0);
                    AnchorPane.setRightAnchor(table, 0.0);
                    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

                    tablePane.getChildren().add(table);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
        catalogButtons.add(catalogButton);
        Catalog.getChildren().add(catalogButton);
        fillClear();


    }
    @FXML
    protected void pressSettings(){
        settings.setVisible(!settings.isVisible());
        fileMenu.setVisible(false);
        clearVBox.setVisible(false);
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
        clearVBox.setVisible(false);
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
                if (history[i] != null)
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
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        updateHistoryButtons();
    }

    //обновление текста и выполняемой функции кнопки истории
    private void updateHistoryButtons(){
        for(int i=0;i<5;i++){
            if (history[i] == null) {
                historyButtons[i].setText("нет данных");
            } else {
                historyButtons[i].setText(history[i].substring(history[i].lastIndexOf("\\") + 1));
                int finalI = i;
                historyButtons[i].setOnAction(_ -> openFile(new File(history[finalI])));
            }
        }
    }
    //первоначальное создание кнопок с историей
    private void initHistoryButtons() {
        for(int i=0;i<5;i++) {
            historyButtons[i] = createButton(null);
            VBox.setMargin(historyButtons[i],new Insets(-1,0,0,0));
            historyVbox.getChildren().add(historyButtons[i]);
        }
    }


    private void fillFilePath(File file){
        currentFilePath.setText(file.getAbsolutePath());
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(190);
        button.setMaxWidth(190);
        button.setMinHeight(32);
        button.setMaxHeight(32);
        button.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Button.css")).toString());
        button.getStyleClass().add("file");
        return button;

    }

}