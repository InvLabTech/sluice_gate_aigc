package com.invlab.sluicegate.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;  // 添加这行
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;

public class SluiceGateEntity extends Entity {
    private static final EntityDataAccessor<Integer> GATE_ID = SynchedEntityData.defineId(SluiceGateEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_OPEN = SynchedEntityData.defineId(SluiceGateEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_MOVING = SynchedEntityData.defineId(SluiceGateEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Direction> MOVING_DIRECTION =
            SynchedEntityData.defineId(SluiceGateEntity.class, EntityDataSerializers.DIRECTION);

    private Set<BlockPos> gateBlocks = new HashSet<>();
    private AABB originalBounds;
    private float animationProgress = 0f;
    private int width, height, depth;

    public SluiceGateEntity(EntityType<? extends SluiceGateEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(GATE_ID, 0);
        this.entityData.define(IS_OPEN, false);
        this.entityData.define(IS_MOVING, false);
        this.entityData.define(MOVING_DIRECTION, Direction.NORTH);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (IS_OPEN.equals(key) || IS_MOVING.equals(key) || MOVING_DIRECTION.equals(key)) {
            // 重置动画状态以匹配新数据
            this.animationProgress = this.entityData.get(IS_OPEN) ? 0f : 1f;
        }
    }

    public void initializeGate(int gateId, Set<BlockPos> blocks, BlockPos origin, int width, int height, int depth, Direction direction) {
        if (gateId <= 0) {
            throw new IllegalArgumentException("Invalid gate ID");
        }
        if (blocks == null || blocks.isEmpty()) {
            throw new IllegalArgumentException("Gate blocks cannot be null or empty");
        }
        if (origin == null) {
            throw new IllegalArgumentException("Origin position cannot be null");
        }
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        this.entityData.set(GATE_ID, gateId);
        this.gateBlocks = new HashSet<>(blocks);
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.entityData.set(MOVING_DIRECTION, direction);

        this.originalBounds = new AABB(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + width, origin.getY() + height, origin.getZ() + depth
        );

        this.setPos(origin.getX() + width/2.0, origin.getY() + height/2.0, origin.getZ() + depth/2.0);
        this.setBoundingBox(this.originalBounds);
        this.entityData.set(MOVING_DIRECTION, direction);
    }

    public void toggleGate() {
        if (this.entityData.get(IS_MOVING)) return;

        this.entityData.set(IS_MOVING, true);
        this.entityData.set(IS_OPEN, !this.entityData.get(IS_OPEN));
        this.animationProgress = 0f;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entityData.get(IS_MOVING)) {
            animationProgress += 0.05f;

            if (animationProgress >= 1f) {
                animationProgress = 1f;
                this.entityData.set(IS_MOVING, false);
            }

            updatePositionAndBounds();
        }
    }

    private void updatePositionAndBounds() {
        float progress = this.entityData.get(IS_OPEN) ? animationProgress : 1f - animationProgress;
        double offset = progress * 2.0;
        Direction direction = this.entityData.get(MOVING_DIRECTION);

        Vec3 movement = new Vec3(
                direction.getStepX() * offset,
                direction.getStepY() * offset,
                direction.getStepZ() * offset
        );

        AABB newBounds = this.originalBounds.move(movement);
        this.setBoundingBox(newBounds);
        this.setPos(newBounds.getCenter().x, newBounds.getCenter().y, newBounds.getCenter().z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(GATE_ID, tag.getInt("GateId"));
        this.entityData.set(IS_OPEN, tag.getBoolean("IsOpen"));
        this.entityData.set(IS_MOVING, tag.getBoolean("IsMoving"));

        this.animationProgress = tag.getFloat("AnimationProgress");
        this.width = tag.getInt("Width");
        this.height = tag.getInt("Height");
        this.depth = tag.getInt("Depth");

        // 使用完全限定的Tag类型
        net.minecraft.nbt.ListTag blocksTag = tag.getList("GateBlocks", Tag.TAG_COMPOUND);
        this.gateBlocks = new HashSet<>();
        for (net.minecraft.nbt.Tag blockTag : blocksTag) {
            CompoundTag posTag = (CompoundTag) blockTag;
            this.gateBlocks.add(new BlockPos(
                    posTag.getInt("X"),
                    posTag.getInt("Y"),
                    posTag.getInt("Z")
            ));
        }

        if (tag.contains("OriginalBounds")) {
            CompoundTag boundsTag = tag.getCompound("OriginalBounds");
            this.originalBounds = new AABB(
                    boundsTag.getDouble("MinX"),
                    boundsTag.getDouble("MinY"),
                    boundsTag.getDouble("MinZ"),
                    boundsTag.getDouble("MaxX"),
                    boundsTag.getDouble("MaxY"),
                    boundsTag.getDouble("MaxZ")
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("GateId", this.entityData.get(GATE_ID));
        tag.putBoolean("IsOpen", this.entityData.get(IS_OPEN));
        tag.putBoolean("IsMoving", this.entityData.get(IS_MOVING));

        tag.putFloat("AnimationProgress", this.animationProgress);
        tag.putInt("Width", this.width);
        tag.putInt("Height", this.height);
        tag.putInt("Depth", this.depth);

        // 使用完全限定的ListTag类型
        net.minecraft.nbt.ListTag blocksTag = new net.minecraft.nbt.ListTag();
        for (BlockPos pos : this.gateBlocks) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            blocksTag.add(posTag);
        }
        tag.put("GateBlocks", blocksTag);

        if (this.originalBounds != null) {
            CompoundTag boundsTag = new CompoundTag();
            boundsTag.putDouble("MinX", this.originalBounds.minX);
            boundsTag.putDouble("MinY", this.originalBounds.minY);
            boundsTag.putDouble("MinZ", this.originalBounds.minZ);
            boundsTag.putDouble("MaxX", this.originalBounds.maxX);
            boundsTag.putDouble("MaxY", this.originalBounds.maxY);
            boundsTag.putDouble("MaxZ", this.originalBounds.maxZ);
            tag.put("OriginalBounds", boundsTag);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
}