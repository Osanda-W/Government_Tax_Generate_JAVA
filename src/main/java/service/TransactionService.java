package service;

import org.iit.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TransactionService {

    public List<Transaction> importTransactionsFromFile(String filePath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    try {
                        String billNumber = parts[0].trim();
                        String itemCode = parts[1].trim();
                        double internalPrice = Double.parseDouble(parts[2].trim());
                        double salePrice = Double.parseDouble(parts[3].trim());
                        int quantity = Integer.parseInt(parts[4].trim());
                        double discount = Double.parseDouble(parts[5].trim());
                        int checksum = Integer.parseInt(parts[6].trim());

                        Transaction transaction = new Transaction(
                                billNumber, itemCode, internalPrice, salePrice,
                                quantity, discount, checksum);
                        transactions.add(transaction);
                    } catch (NumberFormatException e) {
                        // Skip malformed lines
                    }
                }
            }
        }
        return transactions;
    }

    public void validateTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            boolean isValid = true;
            List<String> errors = new ArrayList<>();

            // Check checksum
            int calculatedChecksum = calculateChecksum(transaction);
            if (calculatedChecksum != transaction.getChecksum()) {
                errors.add("Checksum mismatch (expected " + transaction.getChecksum() +
                        ", got " + calculatedChecksum + ")");
                isValid = false;
            }

            // Check for special characters in item code
            if (!transaction.getItemCode().matches("[a-zA-Z0-9]+")) {
                errors.add("Item code contains special characters");
                isValid = false;
            }

            // Check for negative prices
            if (transaction.getInternalPrice() < 0 || transaction.getSalePrice() < 0) {
                errors.add("Negative price value");
                isValid = false;
            }

            if (isValid) {
                transaction.setStatus("Valid");
            } else {
                transaction.setStatus("Invalid: " + String.join(", ", errors));
            }
        }
    }

    public int calculateChecksum(Transaction transaction) {
        String lineData = String.format("Item: %s, Internal Price: %.2f, Sale Price: %.2f, Quantity: %d, Discount: %.2f",
                transaction.getItemCode(), transaction.getInternalPrice(),
                transaction.getSalePrice(), transaction.getQuantity(),
                transaction.getDiscount());

        int capitalCount = 0;
        int simpleCount = 0;
        int numberCount = 0;

        for (char c : lineData.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitalCount++;
            } else if (Character.isLowerCase(c)) {
                simpleCount++;
            } else if (Character.isDigit(c) || c == '.') {
                numberCount++;
            }
        }

        return capitalCount + simpleCount + numberCount;
    }

    public void calculateProfits(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            double discountedRevenue = transaction.getSalePrice() *
                    transaction.getQuantity() *
                    (1 - transaction.getDiscount()/100);
            double cost = transaction.getInternalPrice() * transaction.getQuantity();
            double profit = discountedRevenue - cost;
            transaction.setProfit(profit);
        }
    }

    public void removeZeroProfitTransactions(List<Transaction> transactions) {
        transactions.removeIf(transaction ->
                transaction.getProfit() != null && transaction.getProfit() == 0);
    }

    public TaxCalculationResult calculateTax(List<Transaction> transactions, double taxRate) {
        double totalProfit = 0;
        double totalLoss = 0;

        for (Transaction transaction : transactions) {
            if ("Valid".equals(transaction.getStatus())) {
                if (transaction.getProfit() > 0) {
                    totalProfit += transaction.getProfit();
                } else {
                    totalLoss += Math.abs(transaction.getProfit());
                }
            }
        }

        double netProfit = totalProfit - totalLoss;
        double tax = netProfit * taxRate / 100;

        return new TaxCalculationResult(totalProfit, totalLoss, netProfit, tax);
    }

    public static class TaxCalculationResult {
        private final double totalProfit;
        private final double totalLoss;
        private final double netProfit;
        private final double tax;

        public TaxCalculationResult(double totalProfit, double totalLoss,
                                    double netProfit, double tax) {
            this.totalProfit = totalProfit;
            this.totalLoss = totalLoss;
            this.netProfit = netProfit;
            this.tax = tax;
        }

        // Getters
        public double getTotalProfit() { return totalProfit; }
        public double getTotalLoss() { return totalLoss; }
        public double getNetProfit() { return netProfit; }
        public double getTax() { return tax; }
    }
}