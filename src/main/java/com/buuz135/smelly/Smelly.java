package com.buuz135.smelly;

import com.buuz135.smelly.task.EntityAITempInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = Smelly.MOD_ID,
        name = Smelly.MOD_NAME,
        version = Smelly.VERSION
)
public class Smelly {

    public static final String MOD_ID = "smelly";
    public static final String MOD_NAME = "Smelly";
    public static final String VERSION = "1.0-SNAPSHOT";

    @Mod.Instance(MOD_ID)
    public static Smelly INSTANCE;

    //Allow mobs to following you depending of breeding/item/collection/oreDict
    //Allow mobs to steal those items
    //Allow mobs to enter breeding mood if they steal those items
    //Allow player to stop mobs from stealing food by wearing pants
    //Allow mobs to run with panic when they steal something if possible
    //Allow mobs to run away if they started getting close when the player starts moving
    //Allow to scare mobs depending of an item/collection/oreDict

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    private static List<EntityAnimal> created = new ArrayList<>();

    @SubscribeEvent
    public void onSpawn(EntityEvent.EntityConstructing event){
        if (event.getEntity() instanceof EntityAnimal){
            System.out.println("Spawning");
            created.add((EntityAnimal) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent tickEvent){
        List<EntityAnimal> used = new ArrayList<>();
        for (EntityAnimal animal : created){
            if (!animal.isDead){
                animal.tasks.addTask(3, new EntityAITempInventory(animal, 1.0D, Items.WHEAT, false));
            }
            used.add(animal);
        }
        created.removeAll(used);
    }
}
