package com.sergio.bodegainfante.models.enums;

public enum ItemType {
    PRODUCT("Product"),
    PACKAGE("Package");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static ItemType fromString(String type) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.name().equalsIgnoreCase(type)) {
                return itemType;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + type);
    }
}
