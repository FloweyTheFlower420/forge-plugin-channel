package com.floweytf.channels;

import com.floweytf.channels.api.ClientChannelRegistry;
import com.floweytf.channels.api.ClientCustomPacketEvent;
import com.floweytf.channels.commands.Commands;
import com.floweytf.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class EventHandler implements ClientCustomPacketEvent {
    private Modes dumpMode = Modes.none;
    private final CommandDispatcher<String> dispatcher = new CommandDispatcher<>();

    public EventHandler() {
        ClientChannelRegistry.getInstance().addListener(this);
        dispatcher.register(Commands.literal("#pluginchannels")
            .then(Commands.literal("dump")
                .then(Commands.argument("format", EnumArgument.enumArgument(Modes.class))
                    .executes((ctx) -> {
                        dumpMode = ctx.getArgument("format", Modes.class);
                        sendMessageToPlayer("[Plugin channel API]: Dump mode set to: " + dumpMode.name());
                        return 0;
                    })
                )
            )
            .then(Commands.literal("help")
                .executes((ctx) -> {
                    sendMessageToPlayer(
                        "[Plugin channel API]: ---- help ----\n" +
                        "All commands are prefixed with #pluginchannels\n" +
                        "dump <none|base64|stdstreamstr> - Sets the dump mode\n" +
                        "    none - do not dump\n" +
                        "    base64 - dump bytes encoded as base64\n" +
                        "    base64 - dump bytes as a stdstream string\n" +
                        "send <channel> <stdstreams> <buf> - Sends a packet to the server, encoded as specified \n" +
                        "mcpkt register <channel> - Registers a plugin channel"
                    );

                    return 0;
                })
            )
            .then(Commands.literal("send")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                    .then(Commands.literal("stdstreams")
                        .then(Commands.argument("buf", StringArgumentType.greedyString())
                            .executes((ctx) -> {
                                ResourceLocation rl = ctx.getArgument("id", ResourceLocation.class);
                                String data = ctx.getArgument("buf", String.class);
                                PacketBufByteWriter w = PacketBufByteWriter.getWriter();
                                Utils.rethrow(() -> w.write(data));
                                ClientChannelRegistry.sendPacketToServer(rl, w.getBacking());
                                sendMessageToPlayer("[Plugin channel API]: sent to " + rl + " buf: " + data);
                                return 0;
                            })
                        )
                    )
                )
            )
            .then(Commands.literal("mcpkt")
                .then(Commands.literal("register")
                    .then(Commands.argument("channel", ResourceLocationArgument.id())
                        .executes((ctx) -> {
                            ResourceLocation id = ctx.getArgument("channel", ResourceLocation.class);
                            sendMessageToPlayer("[Plugin channel API]: registered " + id);
                            ClientChannelRegistry.sendEnablePacket(id);
                            return 0;
                        })
                    )
                )
            )
        );
    }

    @SubscribeEvent
    public void onClientConnect(ClientPlayerNetworkEvent.LoggedInEvent e) {
        ClientChannelRegistry.getInstance().sendEnablePackets();
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        try {
            dispatcher.execute(event.getMessage(), "");
            Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
            event.setCanceled(true);
        }
        catch (CommandSyntaxException e) {
            System.out.println(e);
        }
    }

    private static void sendMessageToPlayer(String str)   {
        Objects.requireNonNull(Minecraft.getInstance().player)
            .sendMessage(new StringTextComponent(str), UUID.randomUUID());
    }

    private static String getMessage(ResourceLocation rl, String data) {
        return ("[Plugin channel API] [" + rl + "]: " + data);
    }

    @Override
    public void onPacket(PacketBuffer buffer, ResourceLocation name) {
        PlayerEntity p = Objects.requireNonNull(Minecraft.getInstance().player);
        switch (dumpMode) {
            case base64:
                byte[] bytes = new byte[buffer.readableBytes()];
                buffer.readBytes(bytes);
                sendMessageToPlayer(getMessage(name, new String(Base64.getEncoder().encode(bytes))));
                break;
            case stdstreamstr:
                try {
                    PacketBufByteReader reader = new PacketBufByteReader(buffer);
                    sendMessageToPlayer(getMessage(name, reader.readString()));
                } catch (Exception e) {
                    Objects.requireNonNull(Minecraft.getInstance().player).sendMessage(
                        new StringTextComponent("Received data not valid stdstreams string"),
                        UUID.randomUUID()
                    );
                }
            case none:
            default:
                break;
        }
    }
}
