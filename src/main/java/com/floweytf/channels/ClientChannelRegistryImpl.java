package com.floweytf.channels;

import com.floweytf.channels.api.ClientChannelHandler;
import com.floweytf.channels.api.ClientChannelRegistry;
import com.floweytf.channels.api.ClientCustomPacketEvent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;

import java.util.*;

// vanilla channels, client side
public class ClientChannelRegistryImpl extends ClientChannelRegistry {
    private static final ClientChannelRegistryImpl INSTANCE = new ClientChannelRegistryImpl();
    private final Map<ResourceLocation, List<ClientChannelHandler>> handlers = new HashMap<>();
    private final List<ClientCustomPacketEvent> eventHandlers = new ArrayList<>();
    private final Set<ResourceLocation> toRegisterToServer = new HashSet<>();

    public void register(ResourceLocation rl, ClientChannelHandler cch) {
        handlers.computeIfAbsent(rl, (v) -> new ArrayList<>());
        handlers.get(rl).add(cch);
    }

    public void registerAndEnable(ResourceLocation rl, ClientChannelHandler cch) {
        // if currently connected
        sendEnablePacket(rl);
        register(rl, cch);
        toRegisterToServer.add(rl);
    }

    public void unregister(ResourceLocation rl, ClientChannelHandler inst) {
        handlers.get(rl).remove(inst);
    }

    public void addListener(ClientCustomPacketEvent e) {
        eventHandlers.add(e);
    }

    public void removeListener(ClientCustomPacketEvent e) {
        eventHandlers.remove(e);
    }

    public void sendEnablePacket(ResourceLocation id) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeBytes(id.toString().getBytes()).writeChar(0);
        sendPacketToServer(new ResourceLocation("register"), buf);
    }

    public void sendEnablePacket(Collection<ResourceLocation> id) {
        if(id.isEmpty())
            return;

        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        for(ResourceLocation i : id)
            buf.writeBytes(i.toString().getBytes()).writeChar(0);
        sendPacketToServer(new ResourceLocation("register"), buf);
    }

    public void sendPacketToServer(ResourceLocation location, PacketBuffer buffer) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection())
            .send(new CCustomPayloadPacket(location, buffer));
    }

    // Not API:
    public boolean dispatchIfPresent(SCustomPayloadPlayPacket packet) {
        try {
            for (ClientCustomPacketEvent eh : eventHandlers) {
                eh.onPacket(packet.getData(), packet.getIdentifier());
            }

            if (handlers.containsKey(packet.getIdentifier())) {
                for (ClientChannelHandler h : handlers.get(packet.getIdentifier())) {
                    h.onPacket(packet.getData());
                }
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendEnablePackets() {
        sendEnablePacket(toRegisterToServer);
    }
}