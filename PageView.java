// The View portion of the MVC
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.List;

public class PageView extends Pane {

    // Variables
    private TextField crawlTextField;
    private Label crawlRequest;
    private Button crawlButton;
    private Label searchRequest;
    private TextField searchWordTextField;
    private Button searchWordButton;
    private RadioButton pageRankButton;
    private ComboBox<Integer> searchQuantity;
    private Label searchQuantityRequest;
    private ListView<SearchResult> topLinks;
    private Label topLinksLabel;
    private Rectangle screenDivider;
    private static final String FONT = "verdana";

    // Getters
    public TextField getCrawlTextField() { return crawlTextField; }
    public Button getCrawlButton() { return crawlButton; }
    public TextField getSearchWordTextField() { return searchWordTextField; }
    public Button getSearchWordButton() { return searchWordButton; }
    public RadioButton getPageRankButton() { return pageRankButton; }
    public ComboBox<Integer> getSearchQuantity() { return searchQuantity; }
    public ListView<SearchResult> getTopLinks() { return topLinks; }
    public Label getTopLinksLabel() { return topLinksLabel; }
    public Rectangle getScreenDivider() { return screenDivider; }
    public void updateTable(List<SearchResult> topResults) {
        topLinksLabel.setText("Top " + searchQuantity.getValue() + " links:");
        topLinks.getItems().clear();
        topLinks.getItems().addAll(topResults);
    }
    public void updateOrganization(double newWidth) {
        screenDivider.setX(newWidth);
        topLinks.relocate(newWidth + 50, 40);
        topLinksLabel.relocate(newWidth + 50, 20);
    }
    public PageView() {
        // Initializing every variable
        crawlTextField = new TextField();
        crawlRequest = new Label("Please input a seed to be crawled");
        crawlButton = new Button("Crawl");
        searchRequest = new Label("What would you like to search for?");
        searchWordTextField = new TextField();
        searchWordButton = new Button("Search");
        pageRankButton = new RadioButton("Page Rank");
        searchQuantity = new ComboBox<>();
        searchQuantityRequest = new Label("How many search results?");
        topLinks = new ListView<>();
        topLinksLabel = new Label("Top links:");
        screenDivider = new Rectangle(650,0,10,9999);
        /*
         *
         * Crawl
         *
         * */

        crawlRequest.setFont(Font.font(FONT, 15));
        crawlRequest.relocate(10, 10);
        // Makes the TextField bigger because the input will be a link and likely rather long
        crawlTextField.setPrefSize(450, 20);
        crawlTextField.relocate(10, 40);
        crawlButton.relocate(470, 40);

        /*
         *
         * Search
         *
         * */
        searchRequest.setFont(Font.font(FONT, 15));
        searchRequest.relocate(10, 80);

        searchWordTextField.setPrefSize(200, 20);
        searchWordTextField.relocate(10, 110);

        searchWordButton.relocate(220, 110);

        // Page rank checker
        pageRankButton.setFont(Font.font(FONT, 12));
        pageRankButton.relocate(10, 145);

        // Drop down to select the amount of searches to provide
        searchQuantity.relocate(10, 170);
        int counter = 1;
        int maxSelection = 20; // Could be anything but has to be bigger than 10
        while (counter <= maxSelection) {
            searchQuantity.getItems().add(counter);
            counter++;
        }
        // Sets the default selection to 10
        searchQuantity.getSelectionModel().select(9);

        searchQuantityRequest.setFont(Font.font(FONT, 12));
        searchQuantityRequest.relocate(75, 175);

        // List of top 10
        topLinks.relocate(700, 40);
        topLinks.setPrefSize(560, 400);
        /*
         * There are a LARGE number of requirements in other to make a black border.
         *
         * The setBorder method requires a new Border object which requires a new BorderStroke object which then
         * requires a paint value (color code), a border stroke style (dotted, solid, etc.), a new CornerRadii object
         * that serves as the value determining how curved the border is (0 being no curve), and a new BorderWidths
         * object that is simply how thick the border is.
         */
        topLinks.setBorder(new Border(new BorderStroke(Paint.valueOf("#000000"), BorderStrokeStyle.SOLID,
                new CornerRadii(0), new BorderWidths(2))));

        topLinksLabel.relocate(700, 20);
        topLinksLabel.setFont(Font.font(FONT, 15));

        getChildren().addAll(crawlTextField, crawlRequest, crawlButton, searchRequest, searchWordTextField,
                searchWordButton, pageRankButton, searchQuantity, searchQuantityRequest, topLinks, screenDivider,
                topLinksLabel);
    }
}
