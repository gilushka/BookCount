import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;


public class Main extends Application {

    private static String imageName = "C:\\Users\\user\\Work\\BookCount\\src\\resources\\book.jpg";
    private static String fileName = "C:\\Users\\user\\Work\\BookCount\\src\\resources\\history.dat";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //region Elements
        Button buttonAdd = new Button("Add");
        buttonAdd.setFont(Font.font(18));
        Button buttonRemove = new Button("Remove");
        buttonRemove.setFont(Font.font(18));
        Label valueLabel = new Label("0");
        valueLabel.setMinHeight(200);
        valueLabel.setMinWidth(150);
        valueLabel.setFont(Font.font(230));
        valueLabel.setOpacity(40);
        setStartValueInLabel(valueLabel);
        Image image = new Image(new FileInputStream(imageName));
        ImageView imageView = new ImageView(image);
        //endregion

        //region Menu
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((ActionEvent t) -> {
            try {
                optimizeLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
        menuFile.getItems().addAll(exit);
        menuBar.getMenus().addAll(menuFile);
        //endregion

        //region topBorder
        BorderPane topBorder = new BorderPane();
        topBorder.setTop(menuBar);
        topBorder.setCenter(valueLabel);
        //endregion

        //region bottomBorder
        BorderPane bottomBorder = new BorderPane();
        bottomBorder.setMinHeight(50);
        bottomBorder.setMinWidth(300);
        bottomBorder.setLeft(buttonRemove);
        bottomBorder.setRight(buttonAdd);
        //endregion

        //region mainBorder
        BorderPane mainBorder = new BorderPane();
        mainBorder.setMinWidth(300);
        mainBorder.setMinHeight(400);
        mainBorder.setTop(topBorder);
        mainBorder.setBottom(bottomBorder);
        //endregion

        BorderPane deepBorder = new BorderPane();
        deepBorder.setCenter(imageView);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(deepBorder, mainBorder);

        buttonAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String value = String.valueOf(Integer.parseInt(valueLabel.getText()) + 1);
                valueLabel.setText(value);
                try {
                    writeValueInFile(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonRemove.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String value = String.valueOf(Integer.parseInt(valueLabel.getText()) - 1);
                valueLabel.setText(value);
                try {
                    writeValueInFile(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Group group = new Group();
        group.getChildren().addAll(stackPane);
        primaryStage.setTitle("Book balance");
        primaryStage.setScene(new Scene(group, 300, 400));
        primaryStage.show();
    }

    private void setStartValueInLabel(Label label) throws FileNotFoundException {
        File file = new File(fileName);

        Scanner scanner = new Scanner(file);
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!scanner.hasNextLine()) {
                    label.setText(new LogString(line).getCount());
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void optimizeLog() throws IOException {
        File file = new File(fileName);
        String date = LocalDate.now().toString();
        List<String> listOfLogs = new ArrayList<>();
        int difference = 0;
        int startValue = 100000;

        Scanner scanner = new Scanner(file);
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                LogString logString = new LogString(line);
                if (scanner.hasNextLine()) {
                    if (startValue == 100000) {
                        startValue = logString.getIntCount();
                        listOfLogs.add(line);
                    } else {
                        if (logString.getDifference() == null) {
                            if (abs(difference) > abs(logString.getIntCount() - startValue)) {
                                listOfLogs.add(date + " " + (startValue + difference) + " " + (difference < 0 ? difference : "+" + difference));
                                startValue = startValue + difference;
                                difference = logString.getIntCount() - startValue;
                            } else {
                                difference =  logString.getIntCount() - startValue;
                                date = logString.getDate();
                            }
                        } else {
                            startValue = logString.getIntCount();
                            listOfLogs.add(line);
                        }
                    }
                } else {
                    if (abs(difference) > abs(logString.getIntCount() - startValue)) {
                        listOfLogs.add(date + " " + (startValue + difference) + " " + (difference < 0 ? difference : "+" + difference));
                        startValue = startValue + difference;
                        int count = logString.getIntCount() - startValue;
                        listOfLogs.add(date + " " + logString.getIntCount() + " " + (count < 0 ? count : "+" + count));
                    } else {
                        int count = logString.getIntCount() - startValue;
                        listOfLogs.add(date + " " + logString.getIntCount() + " " + (count < 0 ? count : "+" + count));
                    }
                }
            }
        } finally {
            scanner.close();
        }

        FileWriter fileWriter = new FileWriter(file);
        try {
            listOfLogs.forEach(
                    item -> {
                        try {
                            fileWriter.write(item + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        } finally {
            fileWriter.close();
        }

    }

    private void writeValueInFile(String value) throws IOException {
        File file = new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        FileWriter writer = new FileWriter(file, true);
        LocalDate date = LocalDate.now();
        String log = date + " " + value + "\n";

        writer.write(log);
        writer.close();
    }
}