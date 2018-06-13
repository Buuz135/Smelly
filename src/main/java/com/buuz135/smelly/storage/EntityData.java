package com.buuz135.smelly.storage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class EntityData {

    public static List<EntityData> entityDataList = new ArrayList<>();
    private final String mobID;
    private final String items;
    private final float speed;
    private List<ItemStack> possibleItems;
    private boolean ignoresMeta;

    public EntityData(String mobID, String items, float speed) {
        this.mobID = mobID;
        this.items = items;
        this.speed = speed;
        this.possibleItems = new ArrayList<>();
        this.ignoresMeta = false;
        if (this.items.contains(":")) {
            for (String id : this.items.split(",")) {
                String[] name = id.split(":");
                if (name.length >= 2) {
                    Item item = Item.getByNameOrId(new ResourceLocation(name[0], name[1]).toString());
                    if (item != null) {
                        if (name.length > 2) {
                            if (name[2].equals("*") && item.getCreativeTab() != null) {
                                NonNullList<ItemStack> stacks = NonNullList.create();
                                item.getSubItems(item.getCreativeTab(), stacks);
                                possibleItems.addAll(stacks);
                                this.ignoresMeta = true;
                            } else {
                                try {
                                    possibleItems.add(new ItemStack(item, 1, Integer.parseInt(name[2])));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            possibleItems.add(new ItemStack(item));
                        }
                    }
                }
            }
        } else {
            possibleItems.addAll(OreDictionary.getOres(items));
        }
    }

    public static List<EntityData> getEntityDataFromModID(String mobID) {
        List<EntityData> datas = new ArrayList<>();
        for (EntityData data : entityDataList) {
            if (mobID.equalsIgnoreCase(data.getMobID())) datas.add(data);
        }
        return datas;
    }

    public boolean doesPlayerHaveItem(Entity self, EntityPlayer playerMP) {
        if (items.equalsIgnoreCase("breeding") && self instanceof EntityAnimal) {
            for (ItemStack stack : playerMP.inventory.mainInventory) {
                if (((EntityAnimal) self).isBreedingItem(stack)) return true;
            }
        }
        for (ItemStack itemStack : playerMP.inventory.mainInventory) {
            if (isStackValid(itemStack)) return true;
        }
        return false;
    }

    public boolean isStackValid(ItemStack itemStack) {
        for (ItemStack stack : possibleItems) {
            if (ignoresMeta && stack.isItemEqualIgnoreDurability(itemStack)) return true;
            else if (stack.isItemEqual(itemStack)) return true;
        }
        return false;
    }

    public boolean isBreedingItem(Entity self, ItemStack stack) {
        return items.equalsIgnoreCase("breeding") && self instanceof EntityAnimal && ((EntityAnimal) self).isBreedingItem(stack);
    }

    public String getMobID() {
        return mobID;
    }

    public String getItems() {
        return items;
    }

    public float getSpeed() {
        return speed;
    }

    public List<ItemStack> getPossibleItems() {
        return possibleItems;
    }
}
