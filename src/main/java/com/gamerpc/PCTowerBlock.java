package com.gamerpc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Torre de PC que se arma con clic derecho:
 *  1) Tarjeta madre  2) RAM (32 GB o 100 GB)  3) SSD (500 GB o 1 TB)
 * Shift + clic derecho con la mano vacía quita la última pieza.
 */
public class PCTowerBlock extends HorizontalFacingBlock {
    public static final BooleanProperty MOTHERBOARD = BooleanProperty.of("motherboard");
    public static final EnumProperty<RamType> RAM = EnumProperty.of("ram", RamType.class);
    public static final EnumProperty<StorageType> STORAGE = EnumProperty.of("storage", StorageType.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

    public PCTowerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(MOTHERBOARD, false)
                .with(RAM, RamType.NONE)
                .with(STORAGE, StorageType.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MOTHERBOARD, RAM, STORAGE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public static boolean isComplete(BlockState s) {
        return s.get(MOTHERBOARD) && s.get(RAM) != RamType.NONE && s.get(STORAGE) != StorageType.NONE;
    }

    /** Una PC completa brilla (luz nivel 6). */
    public static int light(BlockState s) {
        return isComplete(s) ? 6 : 0;
    }

    private static Item ramItem(RamType t) {
        return t == RamType.RAM_32 ? ModItems.RAM_32GB : ModItems.RAM_100GB;
    }

    private static Item storageItem(StorageType t) {
        return t == StorageType.SSD_500 ? ModItems.SSD_500GB : ModItems.SSD_1TB;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        Item item = held.getItem();

        // --- Tarjeta madre ---
        if (item == ModItems.MOTHERBOARD) {
            if (state.get(MOTHERBOARD)) return fail(world, player, "§cYa tiene tarjeta madre.");
            return install(world, pos, player, held, state.with(MOTHERBOARD, true),
                    "§aTarjeta madre instalada");
        }

        // --- RAM ---
        if (item == ModItems.RAM_32GB || item == ModItems.RAM_100GB) {
            if (!state.get(MOTHERBOARD)) return fail(world, player, "§cPrimero instala la tarjeta madre.");
            if (state.get(RAM) != RamType.NONE) return fail(world, player, "§cYa tiene RAM instalada.");
            RamType type = item == ModItems.RAM_32GB ? RamType.RAM_32 : RamType.RAM_100;
            return install(world, pos, player, held, state.with(RAM, type),
                    "§aRAM de " + type.label + " instalada");
        }

        // --- Almacenamiento ---
        if (item == ModItems.SSD_500GB || item == ModItems.SSD_1TB) {
            if (!state.get(MOTHERBOARD)) return fail(world, player, "§cPrimero instala la tarjeta madre.");
            if (state.get(STORAGE) != StorageType.NONE) return fail(world, player, "§cYa tiene almacenamiento.");
            StorageType type = item == ModItems.SSD_500GB ? StorageType.SSD_500 : StorageType.SSD_1TB;
            return install(world, pos, player, held, state.with(STORAGE, type),
                    "§aSSD de " + type.label + " instalado");
        }

        // --- Mano vacía ---
        if (held.isEmpty()) {
            if (player.isSneaking()) return removeLast(world, pos, player, state);
            if (!world.isClient) player.sendMessage(Text.literal(status(state)), true);
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }

    private String status(BlockState s) {
        if (isComplete(s)) {
            return "§b§l¡PC GAMER lista! §r§7RAM: §f" + s.get(RAM).label + " §7| SSD: §f" + s.get(STORAGE).label;
        }
        StringBuilder sb = new StringBuilder("§eFalta: ");
        if (!s.get(MOTHERBOARD)) sb.append("tarjeta madre, ");
        if (s.get(RAM) == RamType.NONE) sb.append("RAM, ");
        if (s.get(STORAGE) == StorageType.NONE) sb.append("almacenamiento, ");
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    private ActionResult fail(World world, PlayerEntity player, String msg) {
        if (!world.isClient) player.sendMessage(Text.literal(msg), true);
        return ActionResult.success(world.isClient);
    }

    private ActionResult install(World world, BlockPos pos, PlayerEntity player, ItemStack held,
                                 BlockState newState, String msg) {
        if (!world.isClient) {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            if (!player.isCreative()) held.decrement(1);
            if (isComplete(newState)) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1.5f);
                msg = status(newState);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1.2f);
            }
            player.sendMessage(Text.literal(msg), true);
        }
        return ActionResult.success(world.isClient);
    }

    private ActionResult removeLast(World world, BlockPos pos, PlayerEntity player, BlockState state) {
        if (!world.isClient) {
            ItemStack give;
            BlockState next;
            if (state.get(STORAGE) != StorageType.NONE) {
                give = new ItemStack(storageItem(state.get(STORAGE)));
                next = state.with(STORAGE, StorageType.NONE);
            } else if (state.get(RAM) != RamType.NONE) {
                give = new ItemStack(ramItem(state.get(RAM)));
                next = state.with(RAM, RamType.NONE);
            } else if (state.get(MOTHERBOARD)) {
                give = new ItemStack(ModItems.MOTHERBOARD);
                next = state.with(MOTHERBOARD, false);
            } else {
                player.sendMessage(Text.literal("§7La torre está vacía."), true);
                return ActionResult.success(false);
            }
            world.setBlockState(pos, next, Block.NOTIFY_ALL);
            player.getInventory().offerOrDrop(give);
            world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1f, 1.3f);
        }
        return ActionResult.success(world.isClient);
    }

    /** Al romper la torre se sueltan las piezas instaladas. */
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && !player.isCreative()) {
            if (state.get(MOTHERBOARD)) dropStack(world, pos, new ItemStack(ModItems.MOTHERBOARD));
            if (state.get(RAM) != RamType.NONE) dropStack(world, pos, new ItemStack(ramItem(state.get(RAM))));
            if (state.get(STORAGE) != StorageType.NONE) dropStack(world, pos, new ItemStack(storageItem(state.get(STORAGE))));
        }
        super.onBreak(world, pos, state, player);
    }
}
