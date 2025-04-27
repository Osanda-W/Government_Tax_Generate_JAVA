package org.iit;
import javafx.collections.ListChangeListener;
import service.TransactionService;
import service.TransactionService.TaxCalculationResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
//import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for the main application view.
 * Handles all user interactions and business logic for the tax system.
 */

public class MainController {
    // FXML injected UI components
    @FXML private TextField filePathField;
    @FXML private Label importStatusLabel;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> billNumberColumn;
    @FXML private TableColumn<Transaction, String> itemCodeColumn;
    @FXML private TableColumn<Transaction, Double> internalPriceColumn;
    @FXML private TableColumn<Transaction, Double> salePriceColumn;
    @FXML private TableColumn<Transaction, Integer> quantityColumn;
    @FXML private TableColumn<Transaction, Double> discountColumn;
    @FXML private TableColumn<Transaction, Integer> checksumColumn;
    @FXML private TableColumn<Transaction, Double> profitColumn;
    @FXML private TableColumn<Transaction, String> statusColumn;
    @FXML
    Label totalRecordsLabel;
    @FXML
    Label validRecordsLabel;
    @FXML
    Label invalidRecordsLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TextField taxRateField;
    @FXML private Label taxResultLabel;
    @FXML private Label totalProfitLabel;
    @FXML private Label totalLossLabel;
    @FXML private Label netProfitLabel;
    @FXML private Label netProfitSummaryLabel;


    // Service layer for business logic
    private final TransactionService transactionService = new TransactionService();
    // Observable list to hold transaction data for the table
    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    /**
     * Initializes the controller after FXML loading.
     * Sets up table columns and initial UI state.
     */
    @FXML
    private void initialize() {
        // Configure the table view
        transactionsTable.setItems(transactions);
        transactionsTable.setEditable(true);
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set cell value factories using PropertyValueFactory
        // Bind table columns to Transaction properties
        billNumberColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        internalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        checksumColumn.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Enable edit/delete buttons only when a row is selected
        transactionsTable.getSelectionModel().selectedItemProperty().addListener(( newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });

    }

    /**
     * Handles the browse button click to select a transaction file.
     */
    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Transaction File");
        File file = fileChooser.showOpenDialog(filePathField.getScene().getWindow());
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Imports transactions from the selected file.
     */
    @FXML
    void handleImport() {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isEmpty()) {
            showAlert("Error", "Please specify a file path.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            showAlert("Error", "File does not exist.");
            return;
        }

        try {
            // Import transactions and update the table
            List<Transaction> imported = transactionService.importTransactionsFromFile(filePath);
            transactions.setAll(imported);
            importStatusLabel.setText("Successfully imported " + transactions.size() + " transactions.");
            updateSummary();
        } catch (IOException e) {
            showAlert("Error", "Failed to read file: " + e.getMessage());
        }
    }

    /**
     * Validates all transactions in the table.
     */
    @FXML
    void handleValidate() {
        transactionService.validateTransactions(transactions);
        updateSummary();

        // Show validation results
        long validCount = transactions.stream().filter(t -> t.getStatus().startsWith("Valid")).count();
        long invalidCount = transactions.size() - validCount;
        showAlert("Validation Complete", "Valid records: " + validCount + "\nInvalid records: " + invalidCount);
    }


    /**
     * Handles editing a selected transaction.
     */
    @FXML
    private void handleEdit() {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) return;

        // Create a dialog to edit the transaction
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Edit transaction details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField billNumberField = new TextField(selectedTransaction.getBillNumber());
        TextField itemCodeField = new TextField(selectedTransaction.getItemCode());
        TextField internalPriceField = new TextField(String.valueOf(selectedTransaction.getInternalPrice()));
        TextField salePriceField = new TextField(String.valueOf(selectedTransaction.getSalePrice()));
        TextField quantityField = new TextField(String.valueOf(selectedTransaction.getQuantity()));
        TextField discountField = new TextField(String.valueOf(selectedTransaction.getDiscount()));

        // Add form fields to grid
        grid.add(new Label("Bill Number:"), 0, 0);
        grid.add(billNumberField, 1, 0);
        grid.add(new Label("Item Code:"), 0, 1);
        grid.add(itemCodeField, 1, 1);
        grid.add(new Label("Internal Price:"), 0, 2);
        grid.add(internalPriceField, 1, 2);
        grid.add(new Label("Sale Price:"), 0, 3);
        grid.add(salePriceField, 1, 3);
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantityField, 1, 4);
        grid.add(new Label("Discount:"), 0, 5);
        grid.add(discountField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a transaction when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Create updated transaction from form data
                    String billNumber = billNumberField.getText();
                    String itemCode = itemCodeField.getText();
                    double internalPrice = Double.parseDouble(internalPriceField.getText());
                    double salePrice = Double.parseDouble(salePriceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    double discount = Double.parseDouble(discountField.getText());

                    Transaction updatedTransaction = new Transaction(
                            billNumber, itemCode, internalPrice, salePrice, quantity, discount, 0);
                    updatedTransaction.setChecksum(transactionService.calculateChecksum(updatedTransaction));
                    updatedTransaction.setStatus("Valid"); // Assume edited records are valid

                    // Calculate profit if it was already calculated
                    if (selectedTransaction.getProfit() != null) {
                        double profit = (internalPrice * quantity) - (salePrice * quantity - discount);
                        updatedTransaction.setProfit(profit);
                    }

                    return updatedTransaction;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers for price, quantity, and discount.");
                    return null;
                }
            }
            return null;
        });

        // Show dialog and update table if saved
        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(updatedTransaction -> {
            int selectedIndex = transactionsTable.getSelectionModel().getSelectedIndex();
            transactions.set(selectedIndex, updatedTransaction);
            updateSummary();
        });
    }

    /**
     * Deletes the selected transaction from the table.
     */
    @FXML
    private void handleDelete() {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            transactions.remove(selectedTransaction);
            updateSummary();
        }
    }

    /**
     * Calculates profit for all transactions.
     */
    @FXML
    void handleCalculateProfit() {
        transactionService.calculateProfits(transactions);
        transactionsTable.refresh();
        updateSummary();
    }

    /**
     * Removes transactions with zero profit.
     */
    @FXML
    void handleRemoveZeroProfit() {
        if (transactions.stream().noneMatch(t -> t.getProfit() != null)) {
            showAlert("Error", "Please calculate profit first.");
            return;
        }

        transactionService.removeZeroProfitTransactions(transactions);
        updateSummary();
        showAlert("Success", "Removed all transactions with zero profit.");
    }

    /**
     * Calculates tax based on the entered tax rate.
     */
    @FXML
    void handleCalculateTax() {
        try {
            double taxRate = Double.parseDouble(taxRateField.getText());

            if (transactions.stream().noneMatch(t -> t.getProfit() != null)) {
                showAlert("Error", "Please calculate profit first.");
                return;
            }

            // Calculate tax and update UI
            TaxCalculationResult result = transactionService.calculateTax(transactions, taxRate);

            totalProfitLabel.setText(String.format("Total Profit: Rs.%.2f", result.getTotalProfit()));
            totalLossLabel.setText(String.format("Total Loss: Rs.%.2f", result.getTotalLoss()));
            netProfitLabel.setText(String.format("Net Profit: Rs.%.2f", result.getNetProfit()));
            taxResultLabel.setText(String.format("Tax Due: Rs.%.2f (%.2f%% of Rs.%.2f)",
                    result.getTax(), taxRate, result.getNetProfit()));
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid tax rate.");
        }
    }

    /**
     * Saves transactions to a CSV file.
     */
    @FXML
    private void handleSaveToCSV() {
        if (transactions.isEmpty()) {
            showAlert("Error", "No transactions to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transactions to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(filePathField.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("BillNumber,ItemCode,InternalPrice,SalePrice,Quantity,Discount,Checksum");

                // Write data
                for (Transaction transaction : transactions) {
                    writer.println(String.format("%s,%s,%.2f,%.2f,%d,%.2f,%d",
                            transaction.getBillNumber(),
                            transaction.getItemCode(),
                            transaction.getInternalPrice(),
                            transaction.getSalePrice(),
                            transaction.getQuantity(),
                            transaction.getDiscount(),
                            transaction.getChecksum()));
                }

                showAlert("Success", "Transactions saved successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to save file: " + e.getMessage());
            }
        }
    }

    /**
     * Updates the summary labels with current transaction statistics.
     */
    void updateSummary() {
        // Calculate record counts
        int total = transactions.size();
        int valid = (int) transactions.stream().filter(t -> t.getStatus().startsWith("Valid")).count();
        int invalid = total - valid;

        // Update count labels
        totalRecordsLabel.setText("Total: " + total);
        validRecordsLabel.setText("Valid: " + valid);
        invalidRecordsLabel.setText("Invalid: " + invalid);

        // Calculate and display net profit
        double netProfit = transactions.stream()
                .filter(t -> t.getProfit() != null)
                .mapToDouble(Transaction::getProfit)
                .sum();

        netProfitSummaryLabel.setText(String.format("Net Profit: Rs.%.2f", netProfit));
        transactionsTable.refresh();
    }

    /**
     * Shows an alert dialog with the given title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}