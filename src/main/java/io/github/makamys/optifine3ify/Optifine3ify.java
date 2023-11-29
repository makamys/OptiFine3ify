package io.github.makamys.optifine3ify;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Optifine3ify.MODID, version = Optifine3ify.VERSION)
public class Optifine3ify
{
    public static final String MODID = "optifine3ify";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
}
