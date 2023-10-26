package com.qronicle.enums;

import java.util.ArrayList;
import java.util.List;

public enum UserType {
    CASUAL("CASUAL"),
    COLLECTOR("COLLECTOR"),
    MERCHANT("MERCHANT");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
