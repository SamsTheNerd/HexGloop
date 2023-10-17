package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.misc.INoMoving;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class MixinItemsNoMoving implements INoMoving{
    private boolean noMoving = false;

    @Shadow
    private Vec3d velocity;

    public void setNoMoving(boolean noMoving){
        this.noMoving = noMoving;
    }

    public boolean getNoMoving(){
        return this.noMoving;
    }

    @Inject(method="setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
    at=@At("HEAD"), cancellable = true)
    public void cancelMoving(Vec3d newVelocity, CallbackInfo ci){
        if(getNoMoving()){
            velocity = Vec3d.ZERO;
            ci.cancel();
        }
    }

    // just put it on a random call in the try block
    @Inject(method="readNbt(Lnet/minecraft/nbt/NbtCompound;)V",
    at=@At(value="INVOKE", target="net/minecraft/entity/Entity.setPos (DDD)V"))
    public void readNoMoving(NbtCompound nbt, CallbackInfo ci){
        if(nbt.contains("NoMoving")){
            setNoMoving(nbt.getBoolean("NoMoving"));
        }
    }

    @Inject(method="writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;",
    at=@At("RETURN"), cancellable = true)
    public void writeNoMoving(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir){
        if(getNoMoving()){
            NbtCompound newNbt = cir.getReturnValue();
            newNbt.putBoolean("NoMoving", true);
            cir.setReturnValue(newNbt);
        }
    }

    @Inject(method="canUsePortals()Z", at=@At("RETURN"), cancellable = true)
    public void noTeleporting(CallbackInfoReturnable<Boolean> cir){
        if(getNoMoving()){
            cir.setReturnValue(false);
        }
    }
}
