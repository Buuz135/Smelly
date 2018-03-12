package com.buuz135.smelly.task;

import com.buuz135.smelly.config.SmellyConfig;
import com.buuz135.smelly.storage.EntityData;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityAITempInventory extends EntityAIBase {

    private EntityPlayer temptingPlayer;
    private EntityCreature temptedEntity;
    private EntityData data;
    private int delayTemptCounter;
    private int veryCloseTime;
    private int runAwayTime;

    public EntityAITempInventory(EntityCreature temptedEntityIn, EntityData data) {
        this.temptedEntity = temptedEntityIn;
        this.data = data;
        this.delayTemptCounter = 0;
        this.veryCloseTime = 0;
        this.runAwayTime = 0;
    }

    @Override
    public boolean shouldExecute() {
        --this.runAwayTime;
        if (this.runAwayTime > 0 && this.temptedEntity.getNavigator().noPath()) {
            this.temptedEntity.getNavigator().clearPath();
            Vec3d randomPos = RandomPositionGenerator.getLandPos(this.temptedEntity, 5, 4);
            if (randomPos != null)
                this.temptedEntity.getNavigator().tryMoveToXYZ(randomPos.x, randomPos.y, randomPos.z, this.data.getSpeed() * 2); //Make this more random
            return true;
        }
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
            if (this.temptingPlayer == null) {
                this.delayTemptCounter = 2 * 20; //Stopping the AI for a while from working
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
        if (this.runAwayTime > 0) return;
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
                        this.delayTemptCounter = 2 * 20;
                        if (SmellyConfig.sexyMode && this.temptedEntity instanceof EntityAnimal) {
                            ((EntityAnimal) this.temptedEntity).setInLove(null);
                        } else {
                            this.runAwayTime = 2 * 20;
                        }
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

}
