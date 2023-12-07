package com.samsthenerd.hexgloop.mixins.misc;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.utils.ClientUtils;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.item.ColorizerItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

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

    private static String FULL_CHAR = "|";
    // private static String FULL_CHAR = "█";

    @Inject(method="getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;",
    at=@At("RETURN"))
    public void addPigmentDisplayTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir){
        ItemStack stack = (ItemStack)(Object)this;
        if(stack.getItem() instanceof ColorizerItem pigment){
            // if(pigment instanceof ItemDyeColorizer dyePigment){
            //     dyePigment.getDyeColor();
            // }
            UUID uuid = new UUID(0,0);
            float time = 0;
            if(player != null){
                uuid = player.getUuid();
            }
            if(Platform.getEnvironment() == Env.CLIENT){
                time = ClientUtils.getClientTime();
            }
            MutableText colorBar = Text.literal("");
            int steps = 16;
            for(int i = 0; i < steps; i++){
                float progress = (float)i / (float)steps;
                int color = pigment.color(stack, uuid, time, new Vec3d(0, -4 + progress*8, 0));
                colorBar.append(Text.literal(FULL_CHAR).styled(style -> style.withColor(color)));
            }
            MutableText pigmentText = Text.translatable("hexgloop.tooltip.pigment", colorBar);
            pigmentText.styled(style -> style.withColor(Formatting.GRAY));
            cir.getReturnValue().add(pigmentText);
        }
    }
}
