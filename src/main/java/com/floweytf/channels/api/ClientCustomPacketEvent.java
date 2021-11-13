package com.floweytf.channels.api;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface ClientCustomPacketEvent {
    void onPacket(PacketBuffer buffer, ResourceLocation name);
}
