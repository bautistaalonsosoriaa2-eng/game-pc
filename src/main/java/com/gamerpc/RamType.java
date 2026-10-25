package com.gamerpc;

import net.minecraft.util.StringIdentifiable;

public enum RamType implements StringIdentifiable {
    NONE("none", "ninguna"),
    RAM_32("ram_32", "32 GB"),
    RAM_100("ram_100", "100 GB");

    private final String id;
    public final String label;

    RamType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String asString() {
        return id;
    }
}
