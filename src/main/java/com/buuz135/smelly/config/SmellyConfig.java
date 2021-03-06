package com.buuz135.smelly.config;

import com.buuz135.smelly.Smelly;
import com.buuz135.smelly.storage.EntityData;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;


@Config(modid = Smelly.MOD_ID)
public class SmellyConfig {

    @Config.Name("Mob Information List")
    @Config.Comment("A list of strings contaning mob information about what items can they get or movement speed of the mob when chasing the player around. Format: mobid|items|speed. (mobID can be duplicate)\n" +
            "Items can be:\n" +
            " * The word breeding: That will use mobs breeding items\n" +
            " * An oredictionary entry: That will use all the items (Example: oreIron)\n" +
            " * A list of item ids separated by coma: modid:itemid:meta. Meta can be *, then it will ignore the metadata of the item (Example: minecraft:wool:7 or minecraft:stone)\n" +
            "Examples: \n" +
            " minecraft:cow|breeding|1.0\n" +
            " minecraft:pig|minecraft:carrot|1.0\n")
    public static String[] mobInformation = new String[]{
            "minecraft:chicken|breeding|1.0",
            "minecraft:mooshroom|breeding|1.0",
            "minecraft:cow|breeding|1.0",
            "minecraft:ocelot|breeding|1.0",
            "minecraft:pig|breeding|1.0",
            "minecraft:parrot|breeding|1.0",
            "minecraft:rabbit|breeding|1.0",
            "minecraft:sheep|breeding|1.0",
            "minecraft:wolf|breeding|1.0",
            "minecraft:villager|minecraft:emerald|0.75"
    };

    @Config.Comment("Allow mobs to steal desired items from players if they are very close to the player for a while")
    public static boolean allowMobsToStealFromPlayers = true;

    @Config.Comment("Amount of ticks a mob needs to wait so it can steal stuff from you")
    @Config.RangeInt(min = 0)
    public static int stealingTime = 20 * 2;

    @Config.Comment("Allow mobs to enter 'Breeding mode' if possible")
    public static boolean sexyMode = false;

    @Config.Comment("Allow players from mobs stealing stuff by wearing pants")
    public static boolean protectionPants = false;

    @Config.Comment("How many ticks a mob will forget about once it has completed tge AI task")
    @Config.RangeInt(min = 0)
    public static int playerChasingCooldown = 2 * 20;

    @Config.Comment("How many ticks a mob will run away after stealing from the players")
    @Config.RangeInt(min = 0)
    public static int runAwayTime = 2 * 20;

    public static void loadEntityInfo() {
        EntityData.entityDataList.clear();
        for (String information : mobInformation) {
            String[] splitInfo = information.split(Pattern.quote("|"));
            if (splitInfo.length == 3) {
                EntityData.entityDataList.add(new EntityData(splitInfo[0], splitInfo[1], Float.valueOf(splitInfo[2])));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Smelly.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Smelly.MOD_ID)) {
                ConfigManager.sync(Smelly.MOD_ID, Config.Type.INSTANCE);
                loadEntityInfo();
            }
        }
    }

}
