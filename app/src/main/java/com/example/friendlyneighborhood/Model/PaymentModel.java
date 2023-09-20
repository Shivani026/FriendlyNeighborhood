package com.example.friendlyneighborhood.Model;

public class PaymentModel {
    private String paymentID , paymentAmount, paymentType, paidBy, paidAt;


    public PaymentModel(String paymentID, String paymentAmount, String paymentType, String paidBy, String paidAt) {
        this.paymentID = paymentID;
        this.paymentAmount = paymentAmount;
        this.paymentType = paymentType;
        this.paidBy = paidBy;
        this.paidAt = paidAt;
    }

    public PaymentModel() {
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }
}
