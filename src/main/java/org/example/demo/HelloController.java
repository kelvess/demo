package org.example.demo;

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



public class HelloController{
    @FXML
    private Button file;
    @FXML
    private VBox historyVbox;
    @FXML
    private VBox fileMenu;
    @FXML
    private VBox Settings;
    @FXML
    private Label currentFilePath;

    @FXML
    private Button saveSettings;
    @FXML
    private Label blue;
    @FXML
    private Label title;


    private final File historyFile = new File("history");

    @FXML
    private final Button[] historyButtons = new Button[5];



    private final String[] history = new String[5];

    @FXML
    private void initialize(){



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
        Settings.setVisible(!Settings.isVisible());
        System.out.println("Save Settings Button Pressed");
    }

    @FXML
    protected void pressSaveSettings(){
        System.out.println("Положенние окон сохранено");
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