package com.samsthenerd.hexgloop.mixins.misc;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public class MixinShowMediaWorth {
    
    private static DecimalFormat DUST_AMOUNT = new DecimalFormat("###,###.##");

    @Inject(method="getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;",
    at=@At("RETURN"))
    public void addMediaWorthTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir){
        ItemStack stack = (ItemStack)(Object)this;
        ADMediaHolder holder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        ADMediaHolder singleHolder = IXplatAbstractions.INSTANCE.findMediaHolder(singleStack);
        if(holder != null && holder.canConstructBattery()){
            int media = holder.getMedia();
            MutableText mediaAmount = Text.translatable("hexcasting.tooltip.media", Text.literal(DUST_AMOUNT.format(media / (float) MediaConstants.DUST_UNIT)));
            mediaAmount.styled(style -> style.withColor(ItemMediaHolder.HEX_COLOR));
            Text mediaText = Text.translatable("hexgloop.tooltip.media_worth", mediaAmount);
            cir.getReturnValue().add(mediaText);
        }
        if(stack.getCount() > 1 && singleHolder != null && singleHolder.canConstructBattery()){
            int media = singleHolder.getMedia();
            MutableText mediaAmount = Text.translatable("hexcasting.tooltip.media", Text.literal(DUST_AMOUNT.format(media / (float) MediaConstants.DUST_UNIT)));
            mediaAmount.styled(style -> style.withColor(ItemMediaHolder.HEX_COLOR));
            MutableText mediaText = Text.translatable("hexgloop.tooltip.media_worth_each", mediaAmount);
            mediaText.styled(style -> style.withItalic(true).withColor(Formatting.GRAY));
            cir.getReturnValue().add(mediaText);
        }
    }
}
