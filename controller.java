import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
// Alert is used for error handling
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.event.*;

import java.util.List;

public class controller extends Application {
    // Instantiate classes
    public crawler crawlClass = new crawler();
    public searchData searchDataClass = new searchData();
    public searchPrompt searchClass = new searchPrompt();

   /* public void initialize() {
        crawlClass.initialize();
    }

    public void crawl(String seedURL) {
        crawlClass.crawl(seedURL);
    }

    public List<String> getOutgoingLinks(String url) {
        searchDataClass.getOutgoingLinks(url);
    }

    public List<String> getIncomingLinks(String url) {
        searchDataClass.getIncomingLinks(url);

    }

    public double getPageRank(String url) {
        searchDataClass.getPageRank(url);
    }

    public double getIDF(String word) {
        searchDataClass.getIDF(word);
    }

    public double getTF(String url, String word) {
        searchDataClass.getTF(url, word);
    }

    public double getTFIDF(String url, String word) {
        searchDataClass.getTFIDF(url, word);
    }

    public List<SearchResult> search(String query, boolean boost, int X) {
        searchClass.search(query, boost, X);
    } */

    public void start(Stage primaryStage) {
        Pane aPane = new Pane();

        /*
        *
        * Crawl
        *
        * */
        Label crawlRequest = new Label("Please input a seed to be crawled");
        crawlRequest.setFont(new Font(15));
        crawlRequest.relocate(10, 10);

        TextField crawlTextField = new TextField();
        // Makes the TextField bigger because the input will be a link and likely rather long
        crawlTextField.setPrefSize(450, 20);
        crawlTextField.relocate(10, 40);

        Button crawlButton = new Button("Crawl");
        crawlButton.relocate(470, 40);

        crawlButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
//                initialize();
//                crawl(crawlTextField.getText());
                crawlTextField.clear();
            }
        });

        /*
         *
         * Search Data
         *
         * */


        /*
         *
         * Search
         *
         * */
        Label searchRequest = new Label("What would you like to search for?");
        searchRequest.setFont(new Font(15));
        searchRequest.relocate(10, 80);

        TextField searchWordTextField = new TextField();
        searchWordTextField.setPrefSize(200, 20);
        searchWordTextField.relocate(10, 110);

        Button searchWordButton = new Button("Search");
        searchWordButton.relocate(220, 110);

        searchWordButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
//                search(searchWordTextField.getText());
                searchWordTextField.clear();
            }
        });


        // BackgroundFill object is being made
        // Color.valueOf obviously takes an RGB color code
        // new CornerRadii indicates how rounded the corners are
        // new Insets sets the padding of the background
        BackgroundFill backgroundColor = new BackgroundFill(Color.valueOf("#a6edde"), new CornerRadii(0),
                new Insets(0));
        Background background = new Background(backgroundColor);
        aPane.setBackground(background);

        // Adding everything to the pane
        aPane.getChildren().addAll(crawlRequest, crawlTextField, crawlButton, searchRequest, searchWordButton,
                searchWordTextField);
        aPane.setPrefSize(1000,600);

        primaryStage.setTitle("Web Crawler and Searcher");
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(aPane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
