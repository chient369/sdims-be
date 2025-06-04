package com.company.internalmgmt.modules.contract.model.enums;

import lombok.Getter;

/**
 * Enum representing different types of contracts
 */
@Getter
public enum ContractType {
    FIXED_PRICE("FixedPrice"),
    TIME_AND_MATERIAL("TimeAndMaterial"),
    RETAINER("Retainer"),
    MAINTENANCE("Maintenance"),
    OTHER("Other");

    private final String value;

    ContractType(String value) {
        this.value = value;
    }

    public static ContractType fromValue(String value) {
        for (ContractType type : ContractType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ContractType value: " + value);
    }
} 