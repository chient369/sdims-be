package com.company.internalmgmt.modules.contract.model.enums;

import lombok.Getter;

/**
 * Enum representing different status of contract payments
 */
@Getter
public enum PaymentStatus {
    UNPAID("unpaid"),
    PARTIAL("partial"),
    PAID("paid"),
    OVERDUE("overdue"),
    CANCELLED("cancelled");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentStatus value: " + value);
    }
} 