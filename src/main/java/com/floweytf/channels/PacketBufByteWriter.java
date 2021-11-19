package com.floweytf.channels;

import com.floweytf.utils.streams.stdstreams.BasicStandardWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

public class PacketBufByteWriter extends BasicStandardWriter<PacketBuffer> {
    public PacketBufByteWriter(PacketBuffer buf) {
        super(buf, ByteBuf::writeBytes);
    }

    public static PacketBufByteWriter getWriter() {
        return new PacketBufByteWriter(new PacketBuffer(Unpooled.buffer()));
    }
}
