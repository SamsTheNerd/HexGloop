package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

@Mixin(BlockEntity.class)
public abstract class MixinSyncLecternData {

    @Shadow
    protected void writeNbt(NbtCompound nbt){
        throw new AssertionError();
    }

    @ModifyReturnValue(
        method="toInitialChunkDataNbt()Lnet/minecraft/nbt/NbtCompound;",
        at=@At("RETURN")
    )
    public NbtCompound lecternInitialNbt(NbtCompound nbt){
        if(((Object)this) instanceof LecternBlockEntity lecternBE){
            writeNbt(nbt);
            return nbt;
        }
        return nbt;
    }

    @ModifyReturnValue(
        method="toUpdatePacket()Lnet/minecraft/network/Packet;",
        at=@At("RETURN")
    )
    public Packet<ClientPlayPacketListener> lecternUpdatePacket(Packet<ClientPlayPacketListener> packet){
        if(((Object)this) instanceof LecternBlockEntity lecternBE){
            if(packet == null) // this feature really isn't that important, don't want to break other mods over it. TODO: figure out how to merge my stuff into here
            return BlockEntityUpdateS2CPacket.create(lecternBE);
        }
        return packet;
    }
}
