import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.*;

public class Controller extends Application {

    public void start(Stage primaryStage) {
        Model model = new Model();

        PageView view = new PageView();

        Pane aPane = new Pane();

        aPane.setPrefSize(1300,600);
        primaryStage.setTitle("Web Crawler and Searcher");
        primaryStage.setMinWidth(1300);
        primaryStage.setMinHeight(500);
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(aPane));
        primaryStage.show();

        // BackgroundFill object is being made
        // Color.valueOf obviously takes an RGB color code
        // new CornerRadii indicates how rounded the corners are
        // new Insets sets the padding of the background
        BackgroundFill backgroundColor = new BackgroundFill(Color.valueOf("#a6edde"), new CornerRadii(0),
                new Insets(0));
        Background background = new Background(backgroundColor);
        aPane.setBackground(background);

        /*
        *
        * Events
        *
         */
        view.getCrawlButton().setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                model.initialize();
                model.crawl(view.getCrawlTextField().getText());
                view.getCrawlTextField().clear();
            }
        });

        view.getSearchWordButton().setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                // Calls the search action and updates the table together in one line
                view.updateTable(model.search(view.getSearchWordTextField().getText(),
                        view.getPageRankButton().isSelected(), (int) view.getSearchQuantity().getValue()));
                view.getSearchWordTextField().clear();
            }
        });

        // Checks if the width of the screen has changed then updates the necessary objects
        aPane.widthProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                view.updateOrganization(aPane.getWidth() / 2);
            }
        });

        // Adding everything to the pane
        aPane.getChildren().addAll(view);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
