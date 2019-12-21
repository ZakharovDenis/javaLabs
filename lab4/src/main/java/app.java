import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class app extends Application {
    private final ObservableList<PieChart.Data> details = FXCollections.observableArrayList();
    private BorderPane root;
    private PieChart pChart;

    @Override
    public void start(Stage primaryStage) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
          new PieChart.Data("IT", 40),
          new PieChart.Data("Hello", 2)
        );
        pChart = new PieChart(pieData);
        pChart.setData(details);

        root = new BorderPane();
//        Group root = new Group(pChart);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Circle");

        details.addAll(
                new PieChart.Data("Hello", 15),
                new PieChart.Data("darkness", 20)
        );

        for (final PieChart.Data data : pChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    pChart.setData(details);

                }
            });
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public class MainController {
        public void btn (ActionEvent event) {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
