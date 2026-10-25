package com.gamerpc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GamerPC implements ModInitializer {
    public static final String MOD_ID = "gamerpc";

    @Override
    public void onInitialize() {
        ModItems.register();
        ModBlocks.register();

        ItemGroup group = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModBlocks.PC_TOWER))
                .displayName(Text.translatable("itemGroup.gamerpc.main"))
                .entries((ctx, entries) -> {
                    entries.add(ModBlocks.PC_TOWER);
                    entries.add(ModItems.MOTHERBOARD);
                    entries.add(ModItems.RAM_32GB);
                    entries.add(ModItems.RAM_100GB);
                    entries.add(ModItems.SSD_500GB);
                    entries.add(ModItems.SSD_1TB);
                    entries.add(ModBlocks.MONITOR);
                    entries.add(ModBlocks.ROUTER);
                    entries.add(ModBlocks.KEYBOARD);
                    entries.add(ModBlocks.MOUSE);
                })
                .build();
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "main"), group);
    }
}
