package com.floweytf.channels;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModMain.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMain {
    public static final String MODID = "plugin-channel-api";
    public static final Logger LOGGER = LogManager.getLogger();

    public ModMain() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
