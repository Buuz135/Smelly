package com.buuz135.smelly.task;

import com.google.common.collect.Sets;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class EntityAITempInventory extends EntityAITempt {

    private final Set<Item> temptingItems;
    private EntityPlayer temptingPlayer;
    private EntityAnimal temptedEntity;

    public EntityAITempInventory(EntityAnimal temptedEntityIn, double speedIn, Item temptItemIn, boolean scaredByPlayerMovementIn) {
        this(temptedEntityIn, speedIn, scaredByPlayerMovementIn,  Sets.newHashSet(temptItemIn));
    }

    public EntityAITempInventory(EntityAnimal temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<Item> temptItemIn) {
        super(temptedEntityIn, speedIn, scaredByPlayerMovementIn, temptItemIn);
        this.temptingItems = temptItemIn;
        this.temptedEntity = temptedEntityIn;
    }

    @Override
    public boolean shouldExecute() {
        this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    protected boolean isTempting(ItemStack stack) {
        for (Item item : temptingItems){
            if (temptingPlayer.inventory.hasItemStack(new ItemStack(item))) return true;
        }
        return false;
    }
}
