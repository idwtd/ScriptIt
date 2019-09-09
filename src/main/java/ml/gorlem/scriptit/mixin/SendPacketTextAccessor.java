package ml.gorlem.scriptit.mixin;

import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatMessageC2SPacket.class)
public interface SendPacketTextAccessor {
    @Accessor
    void setMessage(String message);
}
