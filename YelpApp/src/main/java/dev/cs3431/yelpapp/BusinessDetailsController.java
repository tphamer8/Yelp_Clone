package dev.cs3431.yelpapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BusinessDetailsController {

    @FXML private Label titleLabel;
    @FXML private TableView<Business> similarBusinesses;
    @FXML private TableColumn<Business, String> nameColumn;
    @FXML private TableColumn<Business, String> addressColumn;
    @FXML private TableColumn<Business, String> cityColumn;
    @FXML private TableColumn<Business, String> rankColumn;
    @FXML private TableColumn<Business, String> latColumn;
    @FXML private TableColumn<Business, String> longColumn;
    @FXML private TableColumn<Business, String> starsColumn;
    @FXML private ListView<String> selectedCategoryList;
    @FXML private ListView<String> selectedAttributeList;

    @FXML public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        latColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        starsColumn.setCellValueFactory(new PropertyValueFactory<>("starRating"));
        selectedCategoryList.setItems(FXCollections.observableArrayList());
        selectedAttributeList.setItems(FXCollections.observableArrayList());
    }

    public void initData (String businessName,
                          ObservableList<String> selectedCategories,
                          ObservableList<String> selectedAttributes,
                          ObservableList<Business> similar) {
        titleLabel.setText("Similar to : " + businessName);
        selectedCategoryList.setItems(selectedCategories);
        selectedAttributeList.setItems(selectedAttributes);
        similarBusinesses.setItems(similar);
    }
}
