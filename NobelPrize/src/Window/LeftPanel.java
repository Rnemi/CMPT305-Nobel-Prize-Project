package Window;

import API.APISearcher;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.RangeSlider;

/**
 * Contains one of the VBox object to be used as a node in the left panel of 
 * the root BorderPane. Once an item is selected in each of the children of the
 * VBox, it will update the center portion of the root BorderPane. Ex: selecting
 * "Physics" from the "Prizes" ComboBox will display all Physics prize winners.
 * @author Nemi
 */
public class LeftPanel {
    /**
     * Attribute variables.
     */
    private final BorderPane      root;
    private final APISearcher     api;
    private final CenterPanel     centerPanel;
    private final ComboBox        prizeComboBox;
    private final ComboBox        countryComboBox;
    private final ComboBox        genderComboBox;
    private final RangeSlider     yearSlider;
    private final Label           sliderLabel;
    private final Label           minYearResult;
    private final Label           maxYearResult;
    private final TabPane         tabPane;
    private final TextField       searchField;
    private final VBox            advancedSearch;
    private final VBox            basicSearch;
    /**
     * Class constructor.
     * @param r root BorderPane
     * @param c the CenterPanel
     * @param a the api data
     */
    public LeftPanel(BorderPane r, CenterPanel c, APISearcher a) {
        root            = r;
        api             = a;
        centerPanel     = c;
        prizeComboBox   = createComboBox(200);
        countryComboBox = createComboBox(200);
        genderComboBox  = createComboBox(200);
        yearSlider      = createYearSlider();
        sliderLabel    = new Label("Select year range");
        minYearResult   = new Label(" ");
        maxYearResult   = new Label(" ");
        tabPane         = initTabPane();
        searchField     = createTextField("Search", 125);
        basicSearch     = initBasicSearch(new Insets(10,10,10,10), 10, 200, 700);
        advancedSearch  = initAdvancedSearch(new Insets(10,10,10,10), 10, 200, 700);
        
        
        updateDisplay();
    }
    /**
     * Updates the TabPane node in the BorderPane.
     */
    private void updateDisplay() {
        root.setLeft(tabPane);
    }
    /**
     * Creates a ComboBox with the given width.
     * @param width the horizontal length of the ComboBox
     * @return a new ComboBox Object
     */
    private ComboBox createComboBox(int width) {
        ComboBox comboBox = new ComboBox();
        comboBox.setPrefWidth(width);
        return comboBox;
    }
    private RangeSlider createYearSlider() {
        RangeSlider slider = new RangeSlider(1901, 2019, 1950, 2019);
        slider.setShowTickMarks(true); 
        slider.setShowTickLabels(true); 
        slider.setBlockIncrement(10); 
        
        slider.lowValueProperty().addListener(o -> {
            int lowValue = (int) slider.getLowValue();
            minYearResult.setText("Low: " + lowValue);
            try {
                centerPanel.updateMinYear(lowValue);
            } catch (IOException ex) {
                Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        slider.highValueProperty().addListener(o -> {
            int highValue = (int) slider.getHighValue();
            maxYearResult.setText("High: " + highValue);
            try {
                centerPanel.updateMinYear(highValue);
            } catch (IOException ex) {
                Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return slider;
    }
    /**
     * Creates a TabPane.
     * @return a new TabPane Object
     */
    private TabPane initTabPane () {
        TabPane tabPane = new TabPane();
        return tabPane;
    }
    /**
     * Create a TextField with a given prompt and size.
     * @param prompt the prompt to display in the text field
     * @param size   length of the TextField
     * @return       a new TextField Object
     */
    private TextField createTextField(String prompt, double size) {
        // Create search field
        TextField newField = new TextField();
        newField.setPromptText(prompt);
        newField.setPrefWidth(size);
        newField.getText();
        
        newField.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
        @Override
        public void handle(KeyEvent ke)
        {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                Map results = (HashMap) api.search(searchField.getText());
                try {
                    centerPanel.getCenterList().updateBasicSearchDisplay(results);
                } catch (IOException ex) {
                    Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    });
        return newField;
    }
    /**
     * Initializes the advancedSearch VBox with ComboBox selections.
     * @param padding padding of the VBox
     * @param width   width of the VBox
     * @param height  height of the VBox
     * @return VBox
     */
    private VBox initAdvancedSearch (Insets padding, int spacing, int width, int height) {
        // Create the VBox
        VBox vBox = new VBox();
        vBox.setPadding(padding);
        vBox.setPrefWidth(width);
        vBox.setPrefHeight(height);
        vBox.setSpacing(spacing);
        // Create the ComboBox selections and search button
        createPrizeSelection();
        createCountrySelection();
        createGenderSelection();
        // Add more ComboBoxes here;
        Button button = createAdvancedSearchButton();
        // Add the ComboBoxes to the VBox
        vBox.getChildren().addAll(prizeComboBox, countryComboBox, genderComboBox,
                                  sliderLabel, yearSlider, minYearResult, maxYearResult,
                                  button);
        // Create the tab and add the VBox to it and add the tab to the TabPane
        Tab advanced    = new Tab("Advanced");
        advanced.setClosable(false);
        advanced.setContent(vBox);
        tabPane.getTabs().add(advanced);
        return vBox;
    }
    /**
     * 
     * @param padding
     * @param spacing
     * @param width
     * @param height
     * @return 
     */
    private VBox initBasicSearch(Insets padding, int spacing, int width, int height) {
        // Create the VBox
        VBox vBox = new VBox();
        vBox.setPadding(padding);
        vBox.setPrefWidth(width);
        vBox.setPrefHeight(height);
        vBox.setSpacing(spacing);
        // Create the HBox that holds the TextField and search button
        HBox searchBox  = new HBox();
        Button searchButton = createButton("Go!", searchField);
        searchBox.getChildren().addAll(searchField, searchButton);
        vBox.getChildren().add(searchBox);
        // Create the tab and add the VBox to it and add the tab to the TabPane
        Tab basic = new Tab("Basic");
        basic.setClosable(false);
        basic.setContent(vBox);
        tabPane.getTabs().add(basic);
        return vBox;
    }
    /**
     * Creates a button with an ActionEvent that searches the laureate data.
     * @param prompt text to display on the button
     * @return new Button Object
     */
    private Button createButton(String prompt, TextField text) {
        Button button = new Button(prompt);
        
        button.setOnAction((ActionEvent event) -> {
            Map results = (HashMap) api.search(searchField.getText());
            try {
                centerPanel.getCenterList().updateBasicSearchDisplay(results);
            } catch (IOException ex) {
                Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return button;
    }
    /**
     * Creates the prize selection ComboBox. Has an event that updates the
     * center display with new prize parameters.
     */
    private void createPrizeSelection() {
        prizeComboBox.getItems().add("Prizes");
        prizeComboBox.getSelectionModel().selectFirst();
        // Get the keys in order to display in the ComboBox, and add them
        List<String> categoryKeys = api.getPrizeKeysInOrder();
        for (String category : categoryKeys) {
            prizeComboBox.getItems().add(category);
            /**
             * Update the prize parameter in the CenterPanel.
             */
            prizeComboBox.setOnAction((Event e) -> {
                try {
                    if (prizeComboBox.getValue().equals("Prizes")) {
                        centerPanel.updatePrize("");
                    } else {
                        centerPanel.updatePrize((String)prizeComboBox.getValue());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
        }
    }
    /**
     * Creates the country selection comboBox. Has an event that updates the
     * center display with new country parameters.
     */
    private void createCountrySelection() {
        countryComboBox.getItems().add("Countries");
        countryComboBox.getSelectionModel().selectFirst();
        // Get the keys in order to display in the ComboBox, and add them
        List<String> countryKeys = api.getCountryKeysInOrder();
        for (String country : countryKeys) {
            if (api.checkIfCountryInUse(country)) {
                countryComboBox.getItems().add(country);
            }
            /**
             * Update the country parameter in the CenterPanel.
             */
            countryComboBox.setOnAction((Event e) -> {
                try {
                    if (countryComboBox.getValue().equals("Countries")) {
                        centerPanel.updateCountry("");
                    } else {
                        centerPanel.updateCountry((String)countryComboBox.getValue());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
    /**
     * Creates the gender selection comboBox. Has an event that updates the
     * center display with new country parameters.
     */
    private void createGenderSelection() {
        genderComboBox.getItems().add("Gender");
        genderComboBox.getSelectionModel().selectFirst();
        genderComboBox.getItems().add("female");
        genderComboBox.getItems().add("male");
        /**
         * Update the gender parameter in the CenterPanel.
         */
            genderComboBox.setOnAction((Event e) -> {
                try {
                    // DOES NOT WORK!!!! Will always go to the "else"
                    if (countryComboBox.getValue().equals("Gender")) {
                        centerPanel.updateGender("");
                    } else {
                        String updateValue = (String)genderComboBox.getValue();
                        centerPanel.updateGender(updateValue.toLowerCase());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
        });
        
    }
    /**
     * Creates a button for the advancedSearch VBox.
     * @return a new Button Object
     */
    private Button createAdvancedSearchButton() {
        Button button = new Button("Go!");
        button.setOnAction((ActionEvent event) -> {
            try {
                centerPanel.updateDisplay();
            } catch (IOException ex) {
                Logger.getLogger(LeftPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        return button;
    }
}
   