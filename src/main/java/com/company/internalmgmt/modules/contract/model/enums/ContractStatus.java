package com.company.internalmgmt.modules.contract.model.enums;

import lombok.Getter;

/**
 * Enum representing different status of contracts
 */
@Getter
public enum ContractStatus {
    DRAFT("Draft"),
    IN_REVIEW("InReview"),
    APPROVED("Approved"),
    ACTIVE("Active"),
    IN_PROGRESS("InProgress"),
    ON_HOLD("OnHold"),
    COMPLETED("Completed"),
    TERMINATED("Terminated"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    private final String value;

    ContractStatus(String value) {
        this.value = value;
    }

    public static ContractStatus fromValue(String value) {
        for (ContractStatus status : ContractStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ContractStatus value: " + value);
    }
} 