package com.floweytf.channels.mixins;

import com.floweytf.channels.api.ClientChannelRegistry;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleCustomPayload", at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lnet/minecraft/network/play/server/SCustomPayloadPlayPacket;getIdentifier()Lnet/minecraft/util/ResourceLocation;"
    ), cancellable = true)
    void handleCustomPayload(SCustomPayloadPlayPacket packet, CallbackInfo ci) {
        if(ClientChannelRegistry.getInstance().dispatchIfPresent(packet))
            ci.cancel();
    }
}