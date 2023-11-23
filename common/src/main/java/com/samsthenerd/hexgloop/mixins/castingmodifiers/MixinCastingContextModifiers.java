package com.samsthenerd.hexgloop.mixins.castingmodifiers;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.samsthenerd.hexgloop.casting.ContextModificationHandlers;
import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.data.client.BlockStateVariantMap.TriFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

@Mixin(CastingContext.class)
public class MixinCastingContextModifiers {
    @Shadow
    @Final
    private ServerPlayerEntity caster;

    @ModifyReturnValue(method="isVecInRange(Lnet/minecraft/util/math/Vec3d;)Z",
    at=@At("RETURN"))
    public boolean doAmbitModification(boolean original, Vec3d pos){
        boolean current = original;
        for(Pair<TriFunction<CastingContext, Vec3d, Boolean, Modification>, Integer> pair : ContextModificationHandlers.AMBIT_MODIFIERS){
            TriFunction<CastingContext, Vec3d, Boolean, Modification> modifier = pair.getLeft();
            Modification mod = modifier.apply((CastingContext)(Object)this, pos, current);
            if(mod != Modification.NONE){
                current = (mod == Modification.ENABLE);
            }
        }
        return current;
    }

    @ModifyReturnValue(method="isCasterEnlightened()Z", at=@At("RETURN"), remap = false)
    public boolean doEnlightenmentModification(boolean original){
        boolean current = original;
        for(Pair<BiFunction<CastingContext, Boolean, Modification>, Integer> pair : ContextModificationHandlers.ENLIGHTENMENT_MODIFIERS){
            BiFunction<CastingContext, Boolean, Modification> modifier = pair.getLeft();
            Modification mod = modifier.apply((CastingContext)(Object)this, current);
            if(mod != Modification.NONE){
                current = (mod == Modification.ENABLE);
            }
        }
        return current;
    }

    @ModifyReturnValue(method="getCanOvercast()Z", at=@At("RETURN"), remap = false)
    public boolean doOvercastModification(boolean original){
        boolean current = original;
        for(Pair<BiFunction<CastingContext, Boolean, Modification>, Integer> pair : ContextModificationHandlers.OVERCAST_MODIFIERS){
            BiFunction<CastingContext, Boolean, Modification> modifier = pair.getLeft();
            Modification mod = modifier.apply((CastingContext)(Object)this, current);
            if(mod != Modification.NONE){
                current = (mod == Modification.ENABLE);
            }
        }
        return current;
    }


}
