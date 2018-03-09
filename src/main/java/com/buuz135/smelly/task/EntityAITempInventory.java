package com.buuz135.smelly.task;

import com.buuz135.smelly.storage.EntityData;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAITempInventory extends EntityAIBase {

    private EntityPlayer temptingPlayer;
    private EntityCreature temptedEntity;
    private EntityData data;
    private int delayTemptCounter;

    public EntityAITempInventory(EntityCreature temptedEntityIn, EntityData data) {
        this.temptedEntity = temptedEntityIn;
        this.data = data;
    }

    @Override
    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
            if (this.temptingPlayer == null) {
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
        } else {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.data.getSpeed());
        }
    }

    private boolean isTempting() {
        return data.doesPlayerHaveItem(temptedEntity, temptingPlayer);
    }
}
