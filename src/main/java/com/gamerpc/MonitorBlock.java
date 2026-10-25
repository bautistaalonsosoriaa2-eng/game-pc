package com.gamerpc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
 * Monitor: se enciende solo si hay una torre COMPLETA pegada a su lado.
 * Clic derecho con la mano: si hay teclado y mouse cerca, abre la PC.
 * Internet: si hay un router a menos de 8 bloques del monitor y de la torre.
 */
public class MonitorBlock extends HorizontalFacingBlock {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    private static final VoxelShape NS = Block.createCuboidShape(1, 0, 5, 15, 15, 11);
    private static final VoxelShape EW = Block.createCuboidShape(5, 0, 1, 11, 15, 15);

    public MonitorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(ON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON);
    }

    public static int light(BlockState s) {
        return s.get(ON) ? 7 : 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(ON, findTower(ctx.getWorld(), ctx.getBlockPos()) != null);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.Z ? NS : EW;
    }

    /** Busca una torre completa pegada (4 lados horizontales). */
    public static BlockPos findTower(BlockView world, BlockPos pos) {
        for (Direction d : Direction.Type.HORIZONTAL) {
            BlockPos p = pos.offset(d);
            BlockState s = world.getBlockState(p);
            if (s.isOf(ModBlocks.PC_TOWER) && PCTowerBlock.isComplete(s)) return p;
        }
        return null;
    }

    /** ¿Hay un bloque de ese tipo a menos de r bloques? */
    public static boolean nearby(BlockView world, BlockPos center, Block block, int r) {
        for (BlockPos p : BlockPos.iterate(center.add(-r, -3, -r), center.add(r, 3, r))) {
            if (world.getBlockState(p).isOf(block)) return true;
        }
        return false;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
                               BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        boolean on = findTower(world, pos) != null;
        if (on != state.get(ON)) {
            world.setBlockState(pos, state.with(ON, on), Block.NOTIFY_ALL);
            world.playSound(null, pos, on ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE,
                    SoundCategory.BLOCKS, 0.8f, on ? 1.6f : 1.0f);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (!world.isClient) return ActionResult.SUCCESS;

        // --- lado cliente: validar y abrir la pantalla ---
        BlockPos tower = findTower(world, pos);
        if (!state.get(ON) || tower == null) {
            player.sendMessage(Text.literal("§cMonitor apagado. Pega al lado una torre de PC completa."), true);
            return ActionResult.SUCCESS;
        }
        boolean keyboard = nearby(world, pos, ModBlocks.KEYBOARD, 4);
        boolean mouse = nearby(world, pos, ModBlocks.MOUSE, 4);
        if (!keyboard || !mouse) {
            String falta = !keyboard && !mouse ? "teclado y mouse" : (!keyboard ? "teclado" : "mouse");
            player.sendMessage(Text.literal("§eFalta " + falta + " cerca del monitor (a 4 bloques)."), true);
            return ActionResult.SUCCESS;
        }
        boolean internet = nearby(world, pos, ModBlocks.ROUTER, 8) && nearby(world, tower, ModBlocks.ROUTER, 8);
        PCHooks.opener.accept(internet);
        return ActionResult.SUCCESS;
    }
}
