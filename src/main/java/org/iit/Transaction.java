package org.iit;

import javafx.beans.property.*;

public class Transaction {
    private final StringProperty billNumber;
    private final StringProperty itemCode;
    private final DoubleProperty internalPrice;
    private final DoubleProperty salePrice;
    private final IntegerProperty quantity;
    private final DoubleProperty discount;
    private final IntegerProperty checksum;
    private final DoubleProperty profit;
    private final StringProperty status;

    public Transaction(String billNumber, String itemCode, double internalPrice, double salePrice, int quantity, double discount, int checksum) {
        this.billNumber = new SimpleStringProperty(billNumber);
        this.itemCode = new SimpleStringProperty(itemCode);
        this.internalPrice = new SimpleDoubleProperty(internalPrice);
        this.salePrice = new SimpleDoubleProperty(salePrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.discount = new SimpleDoubleProperty(discount);
        this.checksum = new SimpleIntegerProperty(checksum);
        this.profit = new SimpleDoubleProperty();
        this.status = new SimpleStringProperty("Not validated");
    }

    // Getters and setters
    public String getBillNumber() { return billNumber.get(); }
    public void setBillNumber(String value) { billNumber.set(value); }
    public StringProperty billNumberProperty() { return billNumber; }

    public String getItemCode() { return itemCode.get(); }
    public void setItemCode(String value) { itemCode.set(value); }
    public StringProperty itemCodeProperty() { return itemCode; }

    public double getInternalPrice() { return internalPrice.get(); }
    public void setInternalPrice(double value) { internalPrice.set(value); }
    public DoubleProperty internalPriceProperty() { return internalPrice; }

    public double getSalePrice() { return salePrice.get(); }
    public void setSalePrice(double value) { salePrice.set(value); }
    public DoubleProperty salePriceProperty() { return salePrice; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getDiscount() { return discount.get(); }
    public void setDiscount(double value) { discount.set(value); }
    public DoubleProperty discountProperty() { return discount; }

    public int getChecksum() { return checksum.get(); }
    public void setChecksum(int value) { checksum.set(value); }
    public IntegerProperty checksumProperty() { return checksum; }

    public Double getProfit() { return profit.get(); }
    public void setProfit(double value) { profit.set(value); }
    public DoubleProperty profitProperty() { return profit; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
}