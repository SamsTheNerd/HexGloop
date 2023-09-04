package com.samsthenerd.hexgloop.mixins.truenameclassactionmixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.misc.worldData.TruenameLockState;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Pseudo
@Mixin(targets="ram.talia.hexal.common.entities.BaseCastingWisp")
public class MixinSorryChloeNoWispNonsense {
    private UUID keyUuid = null;
    private static final String KEY_UUID_KEY = "keyUuid";

    @Inject(method="getCaster()Lnet/minecraft/entity/player/PlayerEntity;", at=@At("RETURN"), cancellable = true)
    public void NullifyCasterIfLockChanged(CallbackInfoReturnable<PlayerEntity> cir) {
        PlayerEntity player = cir.getReturnValue();
        if(player instanceof ServerPlayerEntity sPlayer){
            ServerWorld world = sPlayer.getWorld();
            if(world == null) return;
            MinecraftServer server = world.getServer();
            if(server == null) return;
            UUID lockUUID = TruenameLockState.getServerState(server).getLockUUID(player.getUuid());
            if (lockUUID == null) return;
            if (lockUUID != keyUuid) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method="setCaster(Lnet/minecraft/entity/player/PlayerEntity;)V", at=@At("TAIL"))
    public void SetDefaultUUIDKey(PlayerEntity player, CallbackInfo ci){
        if(keyUuid != null) return;
        if(player instanceof ServerPlayerEntity sPlayer){
            ServerWorld world = sPlayer.getWorld();
            if(world == null) return;
            MinecraftServer server = world.getServer();
            if(server == null) return;
            this.keyUuid = TruenameLockState.getServerState(server).getLockUUID(player.getUuid());
        }
    }

    @Inject(method="readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at=@At("TAIL"))
    public void ReadUUIDKeyFromNBT(NbtCompound nbt, CallbackInfo ci){
        if(nbt.containsUuid(KEY_UUID_KEY)){
            keyUuid = nbt.getUuid(KEY_UUID_KEY);
        }
    }

    @Inject(method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at=@At("TAIL"))
    public void WriteUUIDKeyToNBT(NbtCompound nbt, CallbackInfo ci){
        if(keyUuid != null){
            nbt.putUuid(KEY_UUID_KEY, keyUuid);
        }
    }
}
