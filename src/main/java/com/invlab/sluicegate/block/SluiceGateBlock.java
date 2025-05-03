// SluiceGateBlock.java
package com.invlab.sluicegate.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SluiceGateBlock extends Block {
    public static final BooleanProperty FRAME = BooleanProperty.create("frame");
    public static final BooleanProperty SERVO = BooleanProperty.create("servo");
    public SluiceGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FRAME, false)
                .setValue(SERVO, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FRAME, SERVO);
    }

    // 添加方块交互逻辑
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            // 这里可以添加闸门交互逻辑
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}