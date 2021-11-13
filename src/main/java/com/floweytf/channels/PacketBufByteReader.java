package com.floweytf.channels;

import com.floweytf.utils.streams.stdstreams.BasicStandardReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;

public class PacketBufByteReader extends BasicStandardReader<PacketBuffer> {
    public PacketBufByteReader(PacketBuffer buf) {
        super(buf, (inst, bytes) -> {
            inst.readBytes(bytes);
            return bytes.length;
        });
    }
}
