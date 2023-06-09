package com.samsthenerd.hexgloop.mixins.ring;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(GuiSpellcasting.class)
public class MixinKeepRingCastingOpen {
    // @Inject(at = @At(value = "INVOKE", 
    //     target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", 
    //     shift = At.Shift.AFTER), 
    //     method = "tick()V", cancellable = true)
    @Inject(at = @At("HEAD"), 
        method = "tick()V", cancellable = true)
    public void dontCloseWithRing(CallbackInfo callbackInfo){
        HexGloop.logPrint("MixinKeepRingCastingOpen: is casting screen");
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            HexGloop.logPrint("MixinKeepRingCastingOpen: has player");
            // prob want to add a check here that the casting menu was opened by the player
            if(HexGloop.TRINKETY_INSTANCE.isCastingRingEquipped(player)){
                HexGloop.logPrint("MixinKeepRingCastingOpen: Don't close bc casting ring equipped");
                callbackInfo.cancel();
            }
        }
    }
}
