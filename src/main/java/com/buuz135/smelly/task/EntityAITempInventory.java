package com.buuz135.smelly.task;

import com.buuz135.smelly.config.SmellyConfig;
import com.buuz135.smelly.storage.EntityData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EntityAITempInventory extends EntityAIBase {

    private EntityPlayer temptingPlayer;
    private EntityCreature temptedEntity;
    private EntityData data;
    private int delayTemptCounter;
    private int veryCloseTime;

    public EntityAITempInventory(EntityCreature temptedEntityIn, EntityData data) {
        this.temptedEntity = temptedEntityIn;
        this.data = data;
        this.delayTemptCounter = 0;
        this.veryCloseTime = 0;
    }

    @Override
    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
            if (this.temptingPlayer == null) {
                this.delayTemptCounter = 5 * 20; //Stopping the AI for a while from working
                return false;
            } else {
                return this.isTempting();
            }
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    public void updateTask() {
        this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, (float) (this.temptedEntity.getHorizontalFaceSpeed() + 20), (float) this.temptedEntity.getVerticalFaceSpeed());
        if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 6.25D) {
            this.temptedEntity.getNavigator().clearPath();
            if (SmellyConfig.allowMobsToStealFromPlayers) ++this.veryCloseTime;
            if (this.veryCloseTime > SmellyConfig.stealingTime) {
                List<ItemStack> shuffled = new ArrayList<>(this.data.getPossibleItems());
                Collections.shuffle(shuffled);
                if (shuffled.isEmpty()) shuffled = temptingPlayer.inventory.mainInventory;
                for (ItemStack stack : shuffled) {
                    if (this.temptingPlayer.inventory.hasItemStack(stack)) {
                        int slot = this.temptingPlayer.inventory.getSlotFor(stack);
                        this.temptingPlayer.inventory.getStackInSlot(slot).shrink(1);
                        this.temptingPlayer.attackEntityFrom(DamageSource.GENERIC, 0.1f);
                        this.veryCloseTime = 0;
                        this.delayTemptCounter = 5 * 20;
                        Vec3d randomPos = RandomPositionGenerator.getLandPos(this.temptedEntity, 10, 4);/*getRandPos(this.temptedEntity.world, this.temptedEntity, 10, 4)*/;
                        if (randomPos != null) this.temptedEntity.getNavigator().tryMoveToXYZ(randomPos.x, randomPos.y, randomPos.z, this.data.getSpeed()*2);
                        break;
                    }
                }
            }
        } else {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.data.getSpeed());
            veryCloseTime = 0;
        }
    }

    private boolean isTempting() {
        return data.doesPlayerHaveItem(temptedEntity, temptingPlayer);
    }


    @Nullable
    private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
        BlockPos blockpos = new BlockPos(entityIn);
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        float f = (float) (horizontalRange * horizontalRange * verticalRange * 2);
        BlockPos blockpos1 = null;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int l = i - horizontalRange; l <= i + horizontalRange; ++l) {
            for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1) {
                for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1) {
                    blockpos$mutableblockpos.setPos(l, i1, j1);
                    IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);

                    if (iblockstate.getMaterial() == Material.WATER) {
                        float f1 = (float) ((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));

                        if (f1 < f) {
                            f = f1;
                            blockpos1 = new BlockPos(blockpos$mutableblockpos);
                        }
                    }
                }
            }
        }

        return blockpos1;
    }
}
