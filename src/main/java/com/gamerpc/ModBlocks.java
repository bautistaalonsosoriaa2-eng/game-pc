package com.gamerpc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block PC_TOWER = reg("pc_tower", new PCTowerBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .strength(2.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .luminance(PCTowerBlock::light)));

    public static final Block MONITOR = reg("monitor", new MonitorBlock(
            deviceSettings().luminance(MonitorBlock::light)));

    public static final Block ROUTER = reg("router", new GamerDeviceBlock(deviceSettings(),
            Block.createCuboidShape(2, 0, 3, 14, 10, 13),
            Block.createCuboidShape(3, 0, 2, 13, 10, 14),
            "§bRouter WiFi 6: da Internet a 8 bloques a la redonda"));

    public static final Block KEYBOARD = reg("keyboard", new GamerDeviceBlock(deviceSettings(),
            Block.createCuboidShape(1, 0, 5, 15, 2, 11),
            Block.createCuboidShape(5, 0, 1, 11, 2, 15),
            "§aTeclado mecánico gamer RGB"));

    public static final Block MOUSE = reg("mouse", new GamerDeviceBlock(deviceSettings(),
            Block.createCuboidShape(6, 0, 5, 10, 2, 11),
            Block.createCuboidShape(5, 0, 6, 11, 2, 10),
            "§eMouse gamer 16000 DPI"));

    private static AbstractBlock.Settings deviceSettings() {
        return AbstractBlock.Settings.create()
                .mapColor(MapColor.BLACK)
                .strength(1.0f)
                .sounds(BlockSoundGroup.METAL)
                .nonOpaque();
    }

    private static Block reg(String name, Block block) {
        Identifier id = new Identifier(GamerPC.MOD_ID, name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void register() {
    }
}
