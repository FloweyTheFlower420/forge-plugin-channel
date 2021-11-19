package com.floweytf.channels.commands;

import com.floweytf.channels.PacketBufByteWriter;
import com.floweytf.channels.api.ClientChannelRegistry;
import com.floweytf.channels.commands.Commands;
import com.floweytf.utils.Utils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.ResourceLocation;

public class ClientCommands {
    public static final LiteralArgumentBuilder<String> STD_STREAMS =
        Commands.literal("stdstreams")
            .then(Commands.argument("buf", StringArgumentType.greedyString())
                .executes((ctx) -> {
                    ResourceLocation rl = ctx.getArgument("id", ResourceLocation.class);
                    String data = ctx.getArgument("buf", String.class);
                    PacketBufByteWriter w = PacketBufByteWriter.getWriter();
                    Utils.rethrow(() -> w.write(data));
                    ClientChannelRegistry.sendPacketToServer(rl, w.getBacking());

                    return 0;
                }));
}
