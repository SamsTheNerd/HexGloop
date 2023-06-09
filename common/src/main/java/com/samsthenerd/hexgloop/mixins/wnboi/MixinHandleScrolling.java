package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.items.ItemMultiFocus;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

@Mixin(MsgShiftScrollSyn.class)
public class MixinHandleScrolling {
    @Shadow
    protected boolean invertSpellbook;
    

    @Inject(at = @At("TAIL"), method = "handleForHand(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/Hand;D)V")
	private void handleForHand(ServerPlayerEntity sender, Hand hand, double delta, CallbackInfo info) {
        if(delta != 0){
            ItemStack stack = sender.getStackInHand(hand);
            if(stack.getItem() instanceof ItemMultiFocus){
                handleMultiFocusScroll(sender, hand, stack, delta);
            }
        }
	}

    // i love stealing hex code :D
    public void handleMultiFocusScroll(ServerPlayerEntity sender, Hand hand, ItemStack stack, double delta) {
        if (invertSpellbook) {
            delta = -delta;
        }
        
        var newIdx = ItemMultiFocus.rotatePageIdx(stack, delta < 0.0);

        var len = ItemSpellbook.highestPage(stack);

        var sealed = ItemSpellbook.isSealed(stack);

        MutableText component;
        if (hand == Hand.OFF_HAND && stack.hasCustomName()) {
            if (sealed) {
                component = Text.translatable("hexgloop.tooltip.multi_focus.page_with_name.sealed",
                    Text.literal(String.valueOf(newIdx)).formatted(Formatting.WHITE),
                    Text.literal(String.valueOf(len)).formatted(Formatting.WHITE),
                    Text.literal("").formatted(stack.getRarity().formatting, Formatting.ITALIC)
                        .append(stack.getName()),
                    Text.translatable("hexcasting.tooltip.spellbook.sealed").formatted(Formatting.GOLD));
            } else {
                component = Text.translatable("hexgloop.tooltip.multi_focus.page_with_name",
                    Text.literal(String.valueOf(newIdx)).formatted(Formatting.WHITE),
                    Text.literal(String.valueOf(len)).formatted(Formatting.WHITE),
                    Text.literal("").formatted(stack.getRarity().formatting, Formatting.ITALIC)
                        .append(stack.getName()));
            }
        } else {
            if (sealed) {
                component = Text.translatable("hexgloop.tooltip.multi_focus.page.sealed",
                    Text.literal(String.valueOf(newIdx)).formatted(Formatting.WHITE),
                    Text.literal(String.valueOf(len)).formatted(Formatting.WHITE),
                    Text.translatable("hexcasting.tooltip.spellbook.sealed").formatted(Formatting.GOLD));
            } else {
                component = Text.translatable("hexgloop.tooltip.multi_focus.page",
                    Text.literal(String.valueOf(newIdx)).formatted(Formatting.WHITE),
                    Text.literal(String.valueOf(len)).formatted(Formatting.WHITE));
            }
        }

        sender.sendMessage(component.formatted(Formatting.GRAY), true);
    }
}
