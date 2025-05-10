package dev.cs3431.yelpapp;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YelpController {
    private static final Dotenv dotenv = Dotenv.load();

    // Read env variables
    private static final String JDBC_URL = dotenv.get("JDBC_URL");
    private static final String JDBC_USER = dotenv.get("JDBC_USER");
    private static final String JDBC_PASS = dotenv.get("JDBC_PASS");
    private Connection connection;

    @FXML private Label searchText;
    @FXML private ComboBox<String> stateComboBox;
    @FXML private ComboBox<String> cityComboBox;
    @FXML private Button filterButton;
    @FXML private ListView<String> categoryList;
    @FXML private ListView<String> attributeList;
    @FXML private Button searchButton;
    @FXML private Button clearFilterButton;
    @FXML private TableView<Business> businessTable;
    @FXML private TableColumn<Business, String> nameColumn;
    @FXML private TableColumn<Business, String> addressColumn;
    @FXML private TableColumn<Business, String> cityColumn;
    @FXML private TableColumn<Business, Integer> starsColumn;
    @FXML private TableColumn<Business, Integer> tipsColumn;
    @FXML private TableColumn<Business, Float> latitudeColumn;
    @FXML private TableColumn<Business, Float> longitudeColumn;
    @FXML private ComboBox<String> priceSelect;
    @FXML private ComboBox<String> wifiSelect;
    @FXML private Label counter;

    @FXML void initialize() {
        updateStates();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        starsColumn.setCellValueFactory(new PropertyValueFactory<>("starRating"));
        tipsColumn.setCellValueFactory(new PropertyValueFactory<>("numTips"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryList.setItems(FXCollections.observableArrayList());
        attributeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        attributeList.setItems(FXCollections.observableArrayList());
        priceSelect.setItems(FXCollections.observableArrayList("Any","1","2","3","4"));
        wifiSelect.setItems(FXCollections.observableArrayList("Any","Free", "no"));
        stateComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldState, newState) -> {
                    if (newState != null) {
                        updateCategories(newState);
                        updateCities(newState);
                    }
                });
        cityComboBox.getSelectionModel()
                        .selectedItemProperty()
                        .addListener((observable, oldCity, newCity) -> {
                            if (newCity != null) {
                                updateAttributes(newCity);
                                updateCategoriesCity(newCity);
                            }
                        });
        filterButton.setOnAction(event -> {
            updateCategoriesCity(cityComboBox.getSelectionModel().getSelectedItem());
            updateAttributes(cityComboBox.getSelectionModel().getSelectedItem());
        });
        clearFilterButton.setOnAction(event -> {
            clearFilters();
        });
        searchButton.setOnAction(event -> {searchBusiness();});
        businessTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Business business = businessTable.getSelectionModel().getSelectedItem();
                if (business != null) {
                    loadBusinessPage(business);
                }
            }
        });
    }

    private void loadBusinessPage(Business selected) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(YelpApp.class.getResource("businessDetails.fxml"));
            Parent root = fxmlLoader.load();
            BusinessDetailsController controller = fxmlLoader.getController();

            ObservableList<String> selectedCategories = FXCollections.observableArrayList(
                    getSelectedCategories(selected.getId())
            );

            ObservableList<String> selectedAttributes = FXCollections.observableArrayList(
                    getSelectedAttributes(selected.getId())
            );

            ObservableList<Business> businesses = FXCollections.observableArrayList(
                    getSimilarBusinesses(selected)
            );



            controller.initData(selected.getName(), selectedCategories, selectedAttributes, businesses);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(businessTable.getScene().getWindow());
            dialog.setTitle("Business Details");

            Scene scene = new Scene(root, 695, 700);
            scene.getStylesheets()
                    .add(getClass().getResource("/styles/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Helper Function to get the states
    private void updateStates() {
        ObservableList<String> states = FXCollections.observableArrayList();
        String stateQuery = """
            SELECT DISTINCT state
            FROM business
            ORDER BY state
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                states.add(rs.getString("state")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        stateComboBox.setItems(states);
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // Helper Function to get the cities
    private void updateCities(String state) {
        if (state == null) return;
        ObservableList<String> cities = FXCollections.observableArrayList();
        String stateQuery = """
            SELECT DISTINCT city
            FROM business
            WHERE business.state = ?
            ORDER BY city
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, state);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cities.add(rs.getString("city")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        cityComboBox.setItems(cities);
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void clearFilters() {
        categoryList.getSelectionModel().clearSelection();
        attributeList.getSelectionModel().clearSelection();
        stateComboBox.getSelectionModel().clearSelection();
        cityComboBox.getSelectionModel().clearSelection();
        wifiSelect.getSelectionModel().select("Any");
        priceSelect.getSelectionModel().select("Any");
    }

    // Helper Function to get the categories
    private void updateCategories(String state) {
        if (state == null) return;
        ObservableList<String> categories = FXCollections.observableArrayList();
        String stateQuery = """
            SELECT DISTINCT Categories.category
            FROM Categories
            JOIN business ON business.business_ID = Categories.business_ID
            WHERE business.state = ?
            ORDER BY Categories.category
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, state);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        categoryList.setItems(categories);
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // update categories based of city
    private void updateCategoriesCity(String city) {
        if (city == null) return;
        ObservableList<String> categories = FXCollections.observableArrayList();
        String stateQuery = """
            SELECT DISTINCT Categories.category
            FROM Categories
            JOIN business ON business.business_ID = Categories.business_ID
            WHERE business.city = ?
            ORDER BY Categories.category
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        categoryList.setItems(categories);
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void updateAttributes(String city) {
        if (city == null) return;
        ObservableList<String> attributes = FXCollections.observableArrayList();
        String stateQuery = """
            SELECT DISTINCT ba.attribute
            FROM BusinessAttributes ba
            JOIN business ON business.business_ID = ba.business_ID
            WHERE business.city = ? AND ba.attribute_value = 'True'
            ORDER BY ba.attribute
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                attributes.add(rs.getString("attribute")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        attributeList.setItems(attributes);
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }


    private void searchBusiness() {
        String state = stateComboBox.getSelectionModel().getSelectedItem();
        if (state == null) return;
        List<String> cats = new ArrayList<>(categoryList.getSelectionModel().getSelectedItems());
        List<String> attrs = new ArrayList<>(attributeList.getSelectionModel().getSelectedItems());
        String city = cityComboBox.getSelectionModel().getSelectedItem();
        List<Business> results = queryBusinesses(state, cats, attrs, city);
        counter.setText(String.valueOf(results.size()) + " results found");
        businessTable.setItems(FXCollections.observableArrayList(results));
    }

    private List<Business> queryBusinesses(String state, List<String> categories, List<String> attributes, String city) {

        List<Business> res = new ArrayList<>();

        String businessQuery = """
            SELECT DISTINCT *
            FROM business
            WHERE state = ?
            and city = ?
        """;
        if (!categories.isEmpty()) {

            for (int i = 0; i < categories.size(); i++) {
                businessQuery += " AND business_id IN (SELECT business_id FROM Categories WHERE category = ?)";
            }

        }
        if (!attributes.isEmpty()) {
            for (int i = 0; i < attributes.size(); i++) {
                businessQuery += " AND business_id IN (SELECT business_id FROM businessAttributes WHERE attribute = ? " +
                        "and attribute_value = 'True')";
            }
        }
        String wifi = wifiSelect.getSelectionModel().getSelectedItem();
        String price = priceSelect.getSelectionModel().getSelectedItem();

        if (wifi != null && !wifi.equals("Any")) {
            businessQuery += " AND business_id IN (SELECT business_id FROM businessAttributes WHERE attribute ilike '%WIFI%'" +
                    "and attribute_value = '" + wifi.toLowerCase() + "')";
        }
        if (price != null && !price.equals("Any")) {
            businessQuery += " AND business_id IN (SELECT business_id FROM businessAttributes WHERE attribute ilike '%PRICE%'" +
                    "and attribute_value = '" + price + "')";
        }
        businessQuery = businessQuery.concat(" ORDER BY business_name");
        // to debug, you can print your query string and make sure that it is constructed correctly.

        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(businessQuery)) {

            ps.setString(1, state);
            ps.setString(2, city);
            int count = 3;
            for (String cat : categories) {
                ps.setString(count++, cat);
            }
            for (String attr : attributes) {
                ps.setString(count++, attr);
            }
            System.out.println(businessQuery);
            /*
            for (string cat: categories) {
                ps.setString(count, cat);
                count++;
            } */
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Business(
                        rs.getString("business_id"),
                        rs.getString("business_name"),
                        rs.getString("street_address"),
                        rs.getString("city"),
                        rs.getInt("star_rating"),
                        rs.getInt("num_tips"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getInt("zipcode")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        return res;
    }

    private List<String> getSelectedCategories(String selectedId) {
        List<String> categories = new ArrayList<>();
        String stateQuery = """
            SELECT category
            FROM Categories
            WHERE Categories.business_ID = ?
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, selectedId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category")); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }

        return categories;
    }

    private List<String> getSelectedAttributes(String selectedId) {
        List<String> attributes = new ArrayList<>();
        String stateQuery = """
            SELECT attribute, attribute_value
            FROM BusinessAttributes
            WHERE BusinessAttributes.business_ID = ? AND BusinessAttributes.attribute_value != 'False'
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1, selectedId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String attributeAndValue = rs.getString("attribute") + " (" + rs.getString("attribute_value") + ")";
                attributes.add(attributeAndValue); // Adds to observable list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }

        return attributes;
    }

    private List<Business> getSimilarBusinesses(Business selected) {

        List<Business> res = new ArrayList<>();

        String stateQuery = """
            SELECT count_categories(?, business_id) as rank, *
            FROM Business
            WHERE zipcode = ?
            and geodistance(?,?,latitude,longitude) < 20
            order by count_categories(?, business_id) DESC 
            LIMIT 20;
        """;
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(stateQuery)) {
            ps.setString(1,selected.getId());
            ps.setString(2, String.valueOf(selected.getZipcode()));
            ps.setDouble(3,selected.getLatitude());
            ps.setDouble(4,selected.getLongitude());
            ps.setString(5,selected.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Business(
                        rs.getString("business_id"),
                        rs.getString("business_name"),
                        rs.getString("street_address"),
                        rs.getString("city"),
                        rs.getInt("star_rating"),
                        rs.getInt("num_tips"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getInt("zipcode"),
                        rs.getInt("rank")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try { connection.close(); } catch (SQLException ex) { ex.printStackTrace(); }

        return res;
    }
}