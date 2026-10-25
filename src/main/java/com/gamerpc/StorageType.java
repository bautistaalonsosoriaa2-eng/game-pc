package com.gamerpc;

import net.minecraft.util.StringIdentifiable;

public enum StorageType implements StringIdentifiable {
    NONE("none", "ninguno"),
    SSD_500("ssd_500", "500 GB"),
    SSD_1TB("ssd_1tb", "1 TB");

    private final String id;
    public final String label;

    StorageType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String asString() {
        return id;
    }
}
