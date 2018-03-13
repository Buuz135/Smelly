package com.buuz135.smelly;

import com.buuz135.smelly.config.SmellyConfig;
import com.buuz135.smelly.storage.EntityData;
import com.buuz135.smelly.task.EntityAITempInventory;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
    public static final String VERSION = "1.0.1";

    @Mod.Instance(MOD_ID)
    public static Smelly INSTANCE;

    private static List<EntityCreature> created = new ArrayList<>();

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        SmellyConfig.loadEntityInfo();
    }

    @SubscribeEvent
    public void onCreate(EntityEvent.EntityConstructing event) {
        ResourceLocation name = EntityList.getKey(event.getEntity());
        if (name == null) return;
        if (event.getEntity() instanceof EntityCreature && !EntityData.getEntityDataFromModID(name.toString()).isEmpty()) {
            created.add((EntityCreature) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent tickEvent) {
        List<EntityCreature> used = new ArrayList<>();
        for (EntityCreature animal : created) {
            if (!animal.isDead) {
                for (EntityData data : EntityData.getEntityDataFromModID(EntityList.getKey(animal).toString()))
                    animal.tasks.addTask(4, new EntityAITempInventory(animal, data));
            }
            used.add(animal);
        }
        created.removeAll(used);
    }
}
