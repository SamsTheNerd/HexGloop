package com.samsthenerd.hexgloop.mixins.truenameclassactionmixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.casting.truenameclassaction.ILockedIota;
import com.samsthenerd.hexgloop.misc.worldData.TruenameLockState;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;

@Mixin(targets = "at.petrak.hexcasting.api.spell.iota.EntityIota$1")
public class MixinTruenameVPN {
    // make the entity iota deserialization return null if the lockUUID is not the same as the keyUUID
    @Inject(method="deserialize(Lnet/minecraft/nbt/NbtElement;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/iota/EntityIota;"
    , at=@At("RETURN"), cancellable = true)
    public void checkUUIDLockOnEntityIotaDeser(NbtElement tag, ServerWorld sWorld, CallbackInfoReturnable<EntityIota> cir){
        // HexGloop.logPrint("in checkUUIDLockOnEntityIotaDeser");
        if(cir.getReturnValue() == null) return;
        // HexGloop.logPrint("nonnull return value");
        Entity ent = cir.getReturnValue().getEntity();
        if(ent == null) return;
        // HexGloop.logPrint("nonnull entity");
        if(ent instanceof PlayerEntity){
            UUID lockUUID = TruenameLockState.getServerState(sWorld.getServer()).getLockUUID(ent.getUuid());
            // HexGloop.logPrint("lockUUID: " + (lockUUID == null ? "null" : lockUUID.toString()));
            NbtCompound nbt = HexUtils.downcast(tag, NbtCompound.TYPE);
            UUID keyUUID = null;
            if(nbt.containsUuid("keyUUID")){
                keyUUID = nbt.getUuid("keyUUID");
            }
            ((ILockedIota)(cir.getReturnValue())).setUUIDKey(keyUUID);
            // HexGloop.logPrint("keyUUID: " + (keyUUID == null ? "null" : keyUUID.toString()));
            if(lockUUID != null && !lockUUID.equals(keyUUID)){
                // HexGloop.logPrint("lockUUID != keyUUID");
                cir.setReturnValue(null);
            }
        }
    }
}
