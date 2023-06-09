package com.samsthenerd.hexgloop.mixins.ring;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.common.network.MsgNewSpellPatternSyn;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.TagKey;

// it wants to check for a staff in hand. don't do that if the ring is equipped
@Mixin(MsgNewSpellPatternSyn.class)
public class MixinRingSendPatterns {
    @Redirect(method="lambda$handle$0(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
        at=@At(target="Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/tag/TagKey;)Z", value="INVOKE"))
    public boolean overrideHeldCheck(ItemStack stack, TagKey<Item> tag, ServerPlayerEntity sender){
        return stack.isIn(tag) || HexGloop.TRINKETY_INSTANCE.isCastingRingEquipped(sender);
    }
}
