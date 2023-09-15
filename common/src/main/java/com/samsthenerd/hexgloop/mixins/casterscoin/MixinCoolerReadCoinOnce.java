package com.samsthenerd.hexgloop.mixins.casterscoin;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemCastersCoin;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.casting.operators.rw.OpTheCoolerRead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(OpTheCoolerRead.class)
public class MixinCoolerReadCoinOnce {
    @WrapOperation(method ="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Ljava/util/List;",
    at = @At(value="INVOKE", target="at/petrak/hexcasting/api/addldata/ADIotaHolder.readIota (Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/iota/Iota;", ordinal=0))
    private Iota readCoinOnce(ADIotaHolder holder, ServerWorld world, Operation<Iota> original, @NotNull List<? extends Iota> args, @NotNull CastingContext ctx){
        Iota iota = original.call(holder, world);
        Entity ent = OperatorUtils.getEntity(args, 0, ((OpTheCoolerRead)(Object)(this)).getArgc());
        if(ent instanceof ItemEntity itemEnt && itemEnt.getStack() != null && itemEnt.getStack().getItem() instanceof ItemCastersCoin readOnlyItem){
            if(holder != null){
                // if it's gonna mishap then don't do the read once stuff
                if(iota == null){
                    if(holder.emptyIota() == null){ 
                        return iota;
                    }
                }
                // shouldn't read mishap so do the read once stuff
                ItemStack stack = itemEnt.getStack();
                ItemStack newStack = readOnlyItem.useCoin(stack);
                ItemEntity newEnt = new ItemEntity(ctx.getWorld(), itemEnt.getX(), itemEnt.getY(), itemEnt.getZ(), newStack);
                ctx.getWorld().spawnEntity(newEnt);
            }
        }
        return iota;
    }
}
