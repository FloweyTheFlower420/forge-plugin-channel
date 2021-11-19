package com.floweytf.channels.api;

import net.minecraft.network.PacketBuffer;

public interface ClientChannelHandler {
    void onPacket(PacketBuffer data);
}
