package com.demo.folder.transaction;

public class TransactionIdHolder {

    private static final ThreadLocal<String> transactionIdHolder = new ThreadLocal<>();

    public static void setTransactionId(String transactionId) {
        transactionIdHolder.set(transactionId);
    }

    public static String getTransactionId() {
        return transactionIdHolder.get();
    }

    public static void clear() {
        transactionIdHolder.remove();
    }
}
