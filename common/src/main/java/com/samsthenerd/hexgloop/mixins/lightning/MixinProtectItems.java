package com.samsthenerd.hexgloop.mixins.lightning;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(Entity.class)
public class MixinProtectItems {
    
    // maybe not the *best* way to do this but uh, oh well i guess
    @Inject(at = @At("HEAD"), method="onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V", cancellable = true)
    public void protectItems(ServerWorld world, LightningEntity lightning, CallbackInfo ci){
        if(((Object) this) instanceof ItemEntity itemEnt && itemEnt.isTouchingWater()){
            itemEnt.setInvulnerable(true);
        }
    }
}
