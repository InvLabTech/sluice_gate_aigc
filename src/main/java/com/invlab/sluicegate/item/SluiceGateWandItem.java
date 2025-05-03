// SluiceGateWandItem.java
package com.invlab.sluicegate.item;

import com.invlab.sluicegate.SluiceGateManager;
import com.invlab.sluicegate.block.SluiceGateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class SluiceGateWandItem extends Item {
    private static final Set<BlockPos> selectedBlocks = new HashSet<>();
    private static BlockPos firstCorner = null;
    
    public SluiceGateWandItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        
        if (!(state.getBlock() instanceof SluiceGateBlock)) {
            return InteractionResult.FAIL;
        }
        
        if (context.getPlayer() == null || context.getPlayer().isShiftKeyDown()) {
            // Shift-click to confirm gate creation
            if (selectedBlocks.size() >= 2) {
                createGateFromSelection(level, context);
                selectedBlocks.clear();
                firstCorner = null;
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        
        // First click - select first corner
        if (firstCorner == null) {
            firstCorner = pos;
            selectedBlocks.add(pos);
            return InteractionResult.SUCCESS;
        }
        
        // Second click - select opposite corner and add all blocks in between
        BlockPos secondCorner = pos;
        int minX = Math.min(firstCorner.getX(), secondCorner.getX());
        int minY = Math.min(firstCorner.getY(), secondCorner.getY());
        int minZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        int maxX = Math.max(firstCorner.getX(), secondCorner.getX());
        int maxY = Math.max(firstCorner.getY(), secondCorner.getY());
        int maxZ = Math.max(firstCorner.getZ(), secondCorner.getZ());
        
        selectedBlocks.clear();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos current = new BlockPos(x, y, z);
                    if (level.getBlockState(current).getBlock() instanceof SluiceGateBlock) {
                        selectedBlocks.add(current);
                    }
                }
            }
        }
        
        return InteractionResult.SUCCESS;
    }
    
    private void createGateFromSelection(Level level, UseOnContext context) {
        if (selectedBlocks.size() < 2) return;
        
        // Calculate gate dimensions
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        
        for (BlockPos pos : selectedBlocks) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int depth = maxZ - minZ + 1;
        BlockPos origin = new BlockPos(minX, minY, minZ);

        // 获取玩家朝向作为闸门方向
        Direction facing = context.getHorizontalDirection();
        SluiceGateManager.createNewGate(level, new HashSet<>(selectedBlocks), origin, width, height, depth, facing);
    }
}