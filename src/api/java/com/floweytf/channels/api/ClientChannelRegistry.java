package com.floweytf.channels.api;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public abstract class ClientChannelRegistry {
    public abstract void register(ResourceLocation rl, ClientChannelHandler cch);
    public abstract void unregister(ResourceLocation rl, ClientChannelHandler inst);
    public abstract void registerAndEnable(ResourceLocation rl, ClientChannelHandler cch);
    public abstract void addListener(ClientCustomPacketEvent e);
    public abstract void removeListener(ClientCustomPacketEvent e);
    public abstract void sendEnablePacket(ResourceLocation id);
    public abstract void sendEnablePacket(Collection<ResourceLocation> id);
    public abstract void sendPacketToServer(ResourceLocation location, PacketBuffer buffer);

    private static ClientChannelRegistry INSTANCE = null;
    private static final String IMPL_LOCATION = "com.floweytf.channels.api.ClientChannelRegistry";

    public static ClientChannelRegistry getInstance() {
        if(INSTANCE == null) {
            try {
                Class<?> clazz = Class.forName(IMPL_LOCATION);
                INSTANCE = (ClientChannelRegistry) clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Cannot load class: " + IMPL_LOCATION);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot create class: " + IMPL_LOCATION);
            }
        }

        return INSTANCE;
    }
}
