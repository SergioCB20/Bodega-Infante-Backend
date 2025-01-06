package com.sergio.bodegainfante.models.enums;

public enum Role {
    ADMIN("Admin"),
    CUSTOMER("Customer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static Role fromString(String type) {
        // Eliminar el prefijo "ROLE_" si está presente
        if (type.startsWith("ROLE_")) {
            type = type.substring(5);  // Eliminar "ROLE_"
        }
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(type)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + type);
    }

}
