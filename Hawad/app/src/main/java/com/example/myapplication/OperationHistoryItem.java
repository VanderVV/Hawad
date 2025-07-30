package com.example.myapplication;
public class OperationHistoryItem {
    private String operation;
    private int oldStatus;
    private int newStatus;
    private String timestamp;

    public OperationHistoryItem(String operation, int oldStatus, int newStatus, String timestamp) {
        this.operation = operation;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    public String getOperation() {
        return operation.substring(0, 1).toUpperCase() + operation.substring(1);
    }

    public String getStatusChange() {
        return (oldStatus == 1 ? "ON" : "OFF") + " → " + (newStatus == 1 ? "ON" : "OFF");
    }

    public int  getOldStatus() {
        return oldStatus;
    }


    public String getTimestamp() {
        return timestamp;
    }
}