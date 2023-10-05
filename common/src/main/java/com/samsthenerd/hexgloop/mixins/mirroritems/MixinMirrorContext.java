package com.samsthenerd.hexgloop.mixins.mirroritems;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.samsthenerd.hexgloop.casting.mirror.BoundMirror;
import com.samsthenerd.hexgloop.casting.mirror.IMirrorBinder;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CastingContext.class)
public class MixinMirrorContext implements IMirrorBinder{
    BoundMirror tempMirror = null;

    @Shadow
    @Final
    private ServerPlayerEntity caster;

    public void bindTo(@Nullable BoundMirror mirror, boolean temp){
        if(temp){
            tempMirror = mirror;
        } else {
            if(((Object)caster) instanceof IMirrorBinder casterBinder){
                casterBinder.bindTo(mirror);
            }
        }
    }

    @Nullable
    public BoundMirror getBoundMirror(){
        if(tempMirror != null) return tempMirror;
        if(((Object)caster) instanceof IMirrorBinder casterBinder){
            return casterBinder.getBoundMirror();
        }
        return null;
    }

    public ItemStack getTrackedStack(){
        if(((Object)caster) instanceof IMirrorBinder casterBinder){
            return casterBinder.getTrackedStack();
        }
        return ItemStack.EMPTY;
    }
}
