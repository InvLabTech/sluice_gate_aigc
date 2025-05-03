package com.invlab.sluicegate;

import com.invlab.sluicegate.entity.ModEntity;
import com.invlab.sluicegate.entity.SluiceGateEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SluiceGateManager {
    private static final ConcurrentHashMap<Integer, SluiceGateEntity> gates = new ConcurrentHashMap<>();
    private static final AtomicInteger nextGateId = new AtomicInteger(1);

    public static int createNewGate(Level level, Set<BlockPos> blocks, BlockPos origin, int width, int height, int depth,Direction direction) {
        if (level.isClientSide) return -1;

        ServerLevel serverLevel = (ServerLevel) level;
        SluiceGateEntity gateEntity = new SluiceGateEntity(ModEntity.SLUICE_GATE.get(), serverLevel);
        if (gateEntity == null) return -1;

        int gateId = nextGateId.getAndIncrement();
        gateEntity.initializeGate(gateId, blocks, origin, width, height, depth, direction);

        serverLevel.addFreshEntity(gateEntity);
        gates.put(gateId, gateEntity);

        return gateId;
    }

    public static boolean toggleGate(int gateId) {
        SluiceGateEntity gate = gates.get(gateId);
        if (gate != null && !gate.level().isClientSide) {
            gate.toggleGate();
            return true;
        }
        return false;
    }

    public static SluiceGateEntity getGate(int gateId) {
        return gates.get(gateId);
    }

    public static void removeGate(int gateId) {
        SluiceGateEntity gate = gates.remove(gateId);
        if (gate != null && !gate.isRemoved() && !gate.level().isClientSide) {
            gate.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public static void clearAllGates() {
        gates.values().removeIf(gate -> {
            if (!gate.isRemoved() && !gate.level().isClientSide) {
                gate.remove(Entity.RemovalReason.DISCARDED);
                return true;
            }
            return false;
        });
    }
}