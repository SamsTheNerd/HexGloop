package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.misc.HexGloopKeybinds;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class MixinWheelInCasting {
    
    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    public void checkForIotaWheelKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        if(((Object) this) instanceof GuiSpellcasting){
            HexGloop.logPrint("MixinWheelInCasting: Casting screen detected");
            if(HexGloopKeybinds.IOTA_WHEEL_KEYBIND.matchesKey(keyCode, scanCode)){
                HexGloop.logPrint("MixinWheelInCasting: Iota wheel key pressed");
                HexGloopKeybinds.handleIotaWheelItems(HexGloopKeybinds.IOTA_WHEEL_KEYBIND, MinecraftClient.getInstance(), true);
            }
        }
    }
}
