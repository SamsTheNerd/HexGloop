package com.samsthenerd.hexgloop.mixins.recipes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.item.ItemStack;

@Mixin(targets="at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell$Spell")
public class MixinSplitCastingCost {

    @Shadow
    @Final
    private ItemStack stack;


    @WrapOperation(
        method="cast(Lat/petrak/hexcasting/api/spell/casting/CastingContext;)V",
        at=@At(value="INVOKE", target="at/petrak/hexcasting/api/utils/MediaHelper.extractMedia$default (Lnet/minecraft/item/ItemStack;IZZILjava/lang/Object;)I")
    )
    private int splitMediaCost(ItemStack mediaStack, int cost, boolean drainForBatteries, 
        boolean godKnowsWhatIHateKotlin, int noIdeaSomeInt, Object yeahSureHaveAnObjectWhyNot,
        Operation<Integer> original){
        int mediaCost = original.call(mediaStack, cost, drainForBatteries, godKnowsWhatIHateKotlin, noIdeaSomeInt, yeahSureHaveAnObjectWhyNot);
        int count = stack.getCount();
        return mediaCost / count;
    }
}
