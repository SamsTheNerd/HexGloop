package com.samsthenerd.hexgloop.mixins.mirroritems;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.SyncedItemHandling;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.casting.operators.spells.OpColorize;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakeBattery;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell;
import at.petrak.hexcasting.common.casting.operators.spells.OpRecharge;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Pseudo
@Mixin(value={
    OpMakeBattery.class,
    OpMakePackagedSpell.class,
    OpRecharge.class,
    OpColorize.class
})
public class MixinEveryInlineGetHeldItemCall {
    @WrapOperation(method = {
        "execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Ljava/util/List;",
        "execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;"
    },
    at = @At(value = "INVOKE", target="net/minecraft/server/network/ServerPlayerEntity.getStackInHand (Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack getAlternateHandStack(ServerPlayerEntity player, Hand hand, Operation<ItemStack> original, List<Iota> args, CastingContext context){
        ItemStack altStack = SyncedItemHandling.getAlternateHandStack(player, hand, context);
        return altStack == null ? original.call(player, hand) : altStack;
    }
}
