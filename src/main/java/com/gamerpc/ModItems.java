package com.gamerpc;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item MOTHERBOARD = reg("motherboard", new Item(new FabricItemSettings().maxCount(16)));
    public static final Item RAM_32GB    = reg("ram_32gb",    new Item(new FabricItemSettings().maxCount(16)));
    public static final Item RAM_100GB   = reg("ram_100gb",   new Item(new FabricItemSettings().maxCount(16)));
    public static final Item SSD_500GB   = reg("ssd_500gb",   new Item(new FabricItemSettings().maxCount(16)));
    public static final Item SSD_1TB     = reg("ssd_1tb",     new Item(new FabricItemSettings().maxCount(16)));

    private static Item reg(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(GamerPC.MOD_ID, name), item);
    }

    public static void register() {
        // fuerza la carga de la clase (los items se registran arriba)
    }
}
