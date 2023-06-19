package com.samsthenerd.hexgloop.mixins.lightning;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.blockentities.BlockEntityGloopEnergizer;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.blocks.BlockGloopEnergizer;

import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(LightningEntity.class)
public class MixinLightningMakeGloop {
    @Shadow
    private BlockPos getAffectedBlockPos(){
        throw new AssertionError();
    }

    @Inject(at = @At(value="INVOKE", target="net/minecraft/entity/LightningEntity.powerLightningRod ()V"), method = "tick")
    public void triggerGloopEnergizer(CallbackInfo ci){
        World thisWorld = ((LightningEntity)(Object) this).world;
        BlockPos affectedPos = getAffectedBlockPos();
        BlockPos energizerPos = BlockGloopEnergizer.getNearestEnergizer(thisWorld, affectedPos);
        if(energizerPos == null){
            return;
        }
        BlockEntityGloopEnergizer energizer = thisWorld.getBlockEntity(energizerPos, HexGloopBEs.GLOOP_ENERGIZER_BE.get()).orElse(null);
        if(energizer == null) return;
        energizer.makeGloopSoup(affectedPos);
    }
}
