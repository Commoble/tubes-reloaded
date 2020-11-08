package commoble.tubesreloaded.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import commoble.tubesreloaded.MixinCallbacks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkManager;

@Mixin(ChunkManager.class)
public class ChunkManagerMixin
{
	@Inject(
		at = @At(value = "RETURN"),
		method = "sendChunkData")
	public void afterSendChunkData(ServerPlayerEntity player, IPacket<?>[] packetCache, Chunk chunkIn, CallbackInfo ci)
	{
		// sync redwire post positions to clients when a chunk needs to be loaded on the client
		MixinCallbacks.afterSendChunkData(player, chunkIn);
	}
}