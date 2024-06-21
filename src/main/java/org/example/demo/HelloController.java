package org.example.demo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.prefs.Preferences;





public class HelloController{
    @FXML
    private Button file;
    @FXML
    private VBox historyVbox;
    @FXML
    private VBox fileMenu;
    @FXML
    private VBox settings;
    @FXML
    private Label currentFilePath;

    @FXML
    private Button saveSettings;
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
    private final Button[] historyButtons = new Button[5];


    private final File historyFile = new File("history");

    private final String[] history = new String[5];
    private Preferences settingsPref;


    @FXML
    private void initialize(){
        settingsPref = Preferences.userRoot().node("demo/settings");
        getSettings();
        initHistoryButtons(readHistory());//инициализация кнопок истории при успешном чтении истории из файла
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
    }

    @FXML
    protected void openFile(){
        FileChooser chooser = new FileChooser();
        File currentFile=chooser.showOpenDialog(null);
        if (currentFile!=null) {
            saveHistory(currentFile);
        }

    }
    @FXML
    protected void pressSettings(){
        settings.setVisible(!settings.isVisible());
        System.out.println("Save Settings Button Pressed");
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
        //проверка существования файла истории или его создание
        if (!historyFile.exists()){
            try {
                historyFile.createNewFile();
            }
            catch (IOException e){
                System.out.println("Ошибка в создании файла истории");
                System.err.println(e);
            }
        }
        else {
            //чтение из файла истории
            if (!readHistory()){
                System.out.println("Ошибка повторного чтения истории");
                return;
            }
            //сдвиг истории на 1 вправо
            swapHistory();
            //запись новой истории в файл
            try (FileWriter writer = new FileWriter(historyFile)){
                history[0]=file.getAbsolutePath();
                for (int i=0;i<5;i++){
                    System.out.println(history[i]);
                    if (history[i]!=null)
                        writer.write(history[i]+"\n");
                }
            }
            catch (Exception e){
                System.out.println("Ошибка в методе записи истории");
                System.err.println(e);
            }
            finally {
                updateHistoryButtons();
            }
        }

    }
    //сдвиг истории на 1 вправо
    private void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }

    private boolean readHistory(){

        try(BufferedReader reader = new BufferedReader( new FileReader(historyFile))){
            String line;
            int count=0;
            while ((line = reader.readLine()) != null && count<5) {
                history[count]=line;
                count++;
            }
            System.out.println("Успешное чтение истории");
            return true;
        }
        catch (Exception e){
            System.out.println("Ошибка в методе чтения истории");
            System.err.println(e);
            return false;
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
            historyButtons[i].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("Нажата кнопка чтения файла из истории : "+history[finalI]);
                }
            });
        }
    }
    //первоначальное создание кнопок с историей
    private void initHistoryButtons(boolean historyIsRead){
        for(int i=0;i<5;i++) {
            historyButtons[i] = new Button();
            historyButtons[i].setMinHeight(32);
            historyButtons[i].setMaxHeight(32);
            historyButtons[i].setMaxWidth(190);
            historyButtons[i].setMinWidth(190);
            historyButtons[i].getStylesheets().add(getClass().getResource("Button.css").toString());
            historyButtons[i].getStyleClass().add("file");
            VBox.setMargin(historyButtons[i],new Insets(-1,0,0,0));
            historyVbox.getChildren().add(historyButtons[i]);
            System.out.println("КНОПКА СОЗДАНА");
        }
        updateHistoryButtons();
    }


    private void fillFilePath(File file){
        currentFilePath.setText(file.getAbsolutePath());
    }


}