package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main extends Application {


    // Date associated with this pane
    private LocalDate date;
    private static Stage stage;
    private static PieChart pChart;
    private static ObservableList<PieChart.Data> details = FXCollections.observableArrayList();
    private static BorderPane root;


    private static Main instance;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    public static Parent replaceSceneContent(LocalDate date) throws Exception {
        Parent page = (Parent) FXMLLoader.load(Main.class.getResource("fullCalendar.fxml"), null, new JavaFXBuilderFactory());
        Scene scene = stage.getScene();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("00:00 - 01:00", 1),
                new PieChart.Data("01:00 - 02:00", 1),
                new PieChart.Data("02:00 - 03:00", 1),
                new PieChart.Data("03:00 - 04:00", 1),
                new PieChart.Data("04:00 - 05:00", 1),
                new PieChart.Data("05:00 - 06:00", 1),
                new PieChart.Data("06:00 - 07:00", 1),
                new PieChart.Data("07:00 - 08:00", 1),
                new PieChart.Data("08:00 - 09:00", 1),
                new PieChart.Data("09:00 - 10:00", 1),
                new PieChart.Data("10:00 - 11:00", 1),
                new PieChart.Data("11:00 - 12:00", 1),
                new PieChart.Data("12:00 - 13:00", 1),
                new PieChart.Data("13:00 - 14:00", 1),
                new PieChart.Data("14:00 - 15:00", 1),
                new PieChart.Data("15:00 - 16:00", 1),
                new PieChart.Data("16:00 - 17:00", 1),
                new PieChart.Data("17:00 - 18:00", 1),
                new PieChart.Data("18:00 - 19:00", 1),
                new PieChart.Data("19:00 - 20:00", 1),
                new PieChart.Data("20:00 - 21:00", 1),
                new PieChart.Data("21:00 - 22:00", 1),
                new PieChart.Data("22:00 - 23:00", 1),
                new PieChart.Data("23:00 - 0:00", 1)
        );
        pChart = new PieChart(pieData);
        pChart.setLegendVisible(false);
        pChart.setStartAngle(90);
        root = new BorderPane();
        Group root = new Group(pChart);

        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream("database.out"));
        HashMap db = (HashMap) objectInputStream.readObject();
        objectInputStream.close();

        boolean isCurrentDate = false;
        Object rightDate = (Object) "";

        for (Object key: db.keySet()){
            if (LocalDate.parse((String) key, DateTimeFormatter.ofPattern("yyyy/MM/dd")).equals(date)){
                isCurrentDate = true;
                rightDate = key;
            }
        }

        HashMap times = (HashMap) db.get(rightDate);

        for (final PieChart.Data data : pChart.getData()) {
            data.getNode().setStyle("-fx-pie-color: #ffffff");
            for (Object time: times.keySet()){
                String parsedTime = time.toString().replace(", "," - ").replace("[","").replace("]","");
                if (parsedTime.equals(data.getName())){
                    HashMap hour = (HashMap) times.get(time);
                    HashMap<String, Long> comparingMap = new HashMap<>();

                    for (Object workType: hour.keySet()){
                        Long workTypeTime = 0L;
                        HashMap detailedWorkType = (HashMap) hour.get(workType);
                        for (Object appTime:detailedWorkType.values()){
                            workTypeTime+=(Long) appTime;
                        }
                        comparingMap.put((String) workType, workTypeTime);
                    }
                    String winner = "";
                    Long comparingValue = 0L;

                    for (String workType: comparingMap.keySet()){
                        if (comparingMap.get(workType) > comparingValue){
                            winner = workType;
                            comparingValue = comparingMap.get(workType);
                        }
                    }

                    if (winner.equals("Browsing")){
                        data.getNode().setStyle("-fx-pie-color: #d93838");
                    } else if (winner.equals("Work")){
                        data.getNode().setStyle("-fx-pie-color: #38d970");
                    } else if (winner.equals("Messaging")){
                        data.getNode().setStyle("-fx-pie-color: #38c9d9");
                    }


                    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                        @Override
                        public void handle(MouseEvent event) {
                            for (Object workType: hour.keySet()){
                                HashMap detailedWorkType = (HashMap) hour.get(workType);
                                for (Object app: detailedWorkType.keySet()){
                                    details.add(new PieChart.Data((String) app, (Long) detailedWorkType.get(app)));
                                }
                            }
                            pChart.setData(details);
                        }
                    });
                }
            }
        }



        if (scene == null && isCurrentDate) {
            scene = new Scene(root, 800, 800);
//            scene.getStylesheets().add(Main.class.getResource("demo.css").toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
        stage.sizeToScene();
        return page;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle("Full Calendar Example");
        primaryStage.setScene(new Scene(new FullCalendarView(YearMonth.now()).getView()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
