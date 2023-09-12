package com.samsthenerd.hexgloop.mixins.truenameclassactionmixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.casting.truenameclassaction.ILockedIota;
import com.samsthenerd.hexgloop.misc.worldData.TruenameLockState;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;

@Mixin(EntityIota.class)
public class MixinSetupLockableEntityIota implements ILockedIota{
    private UUID keyUuid = null;

    // i guess maybe don't even need this as a part of the interface but whatever, good to have it external
    public UUID getUUIDKey(){
        return keyUuid;
    }

    public void setUUIDKey(UUID newKey){
        this.keyUuid = newKey;
    }

    @Inject(method = "serialize()Lnet/minecraft/nbt/NbtElement;", at=@At("RETURN"))
    public void addLockToEntityIotaSerialize(CallbackInfoReturnable<NbtElement> cir){
        NbtCompound nbt = (NbtCompound) cir.getReturnValue();
        // HexGloop.logPrint("in ent iota serialization");
        if(this.keyUuid != null){
            // HexGloop.logPrint("adding lockUUID to nbt: " + keyUuid.toString());
            nbt.putUuid("keyUUID", keyUuid);
        }
    }

    @Inject(method="<init>(Lnet/minecraft/entity/Entity;)V", at=@At("TAIL"))
    public void attachCurrentLockAtCreation(Entity ent, CallbackInfo ci){
        // HexGloop.logPrint("in ent iota creation");
        if(ent instanceof PlayerEntity && ent.getWorld() instanceof ServerWorld sWorld){
            this.keyUuid = TruenameLockState.getServerState(sWorld.getServer()).getLockUUID(ent.getUuid());
            // HexGloop.logPrint("putting lockUUID into ent iota: " + this.keyUuid);
        }
    }
}
