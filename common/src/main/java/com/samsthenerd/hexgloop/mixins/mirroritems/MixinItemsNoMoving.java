package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.misc.INoMoving;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(Entity.class)
public class MixinItemsNoMoving implements INoMoving{
    private static final TrackedData<Boolean> NO_MOVING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Shadow
    private Vec3d velocity;

    @Shadow
    @Final
    protected DataTracker dataTracker;

    public void setNoMoving(boolean noMoving){
        dataTracker.set(NO_MOVING, noMoving);
    }

    public boolean getNoMoving(){
        return dataTracker.get(NO_MOVING);
    }

    @Inject(method="setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
    at=@At("HEAD"), cancellable = true)
    public void cancelMoving(Vec3d newVelocity, CallbackInfo ci){
        if(getNoMoving()){
            velocity = Vec3d.ZERO;
            ci.cancel();
        }
    }

    @Inject(method="<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
    at=@At("TAIL"))
    public void addNoMovingTracker(EntityType<?> type, World world, CallbackInfo ci){
        dataTracker.startTracking(NO_MOVING, false);
    }
}
