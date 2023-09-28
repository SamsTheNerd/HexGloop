package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.GuiBook;

@Mixin(Screen.class)
public class MixinWheelInCasting {

    private static GuiSpellcasting storedHexScreen;
    
    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    public void checkForIotaWheelKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        if(((Object) this) instanceof GuiSpellcasting castingScreen){
            // HexGloop.logPrint("MixinWheelInCasting: Casting screen detected");
            if(HexGloopKeybinds.IOTA_WHEEL_KEYBIND.matchesKey(keyCode, scanCode)){
                // HexGloop.logPrint("MixinWheelInCasting: Iota wheel key pressed");
                HexGloopKeybinds.handleIotaWheelItems(HexGloopKeybinds.IOTA_WHEEL_KEYBIND, MinecraftClient.getInstance(), true);
            }

            // gonna also deal with the scroll keybinds in here too
            if(HexGloopKeybinds.HEX_SCROLL_UP.matchesKey(keyCode, scanCode)){
                HexGloopKeybinds.handleScrollKey(HexGloopKeybinds.HEX_SCROLL_UP, MinecraftClient.getInstance(), true, true);
            }
            if(HexGloopKeybinds.HEX_SCROLL_DOWN.matchesKey(keyCode, scanCode)){
                HexGloopKeybinds.handleScrollKey(HexGloopKeybinds.HEX_SCROLL_UP, MinecraftClient.getInstance(), false, true);
            }

            // yeah why not throw in f keybind too
            MinecraftClient client = MinecraftClient.getInstance();
            if(client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
                if (!client.player.isSpectator())
                    client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            }

            // maybe should separate this out to a new mixin but whatever, we're gonna do the book in gui stuff here too
            if(HexGloopKeybinds.OPEN_HEX_BOOK.matchesKey(keyCode, scanCode)){
                storedHexScreen = castingScreen;
                PatchouliAPI.get().openBookGUI(new Identifier("hexcasting:thehexbook"));
            }
        }
    }

    @Inject(method="close", at=@At("HEAD"), cancellable = true)
    public void goBackToHexBook(CallbackInfo ci){
        if(((Object)this) instanceof GuiBook bookScreen && storedHexScreen != null){
            MinecraftClient.getInstance().setScreen(storedHexScreen);
            storedHexScreen = null;
            ci.cancel();
        }
    }
}
