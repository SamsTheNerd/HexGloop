package com.samsthenerd.hexgloop.misc;

import org.lwjgl.glfw.GLFW;

import com.samsthenerd.hexgloop.items.ItemMultiFocus;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.utils.KeybindUtils;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HexGloopKeybinds {
    public static final KeyBinding IOTA_WHEEL_KEYBIND = new KeyBinding("key.hexgloop.open_iota_wheel",
			InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.hexgloop");
    public static final KeyBinding CASTING_RING_KEY_BINDING = new KeyBinding("key.hexgloop.casting_ring",
    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.hexgloop");

    public static void registerKeybinds(){
        KeybindUtils.registerKeybind(IOTA_WHEEL_KEYBIND, (keyBinding, client) -> handleIotaWheelItems(keyBinding, client));
        KeybindUtils.registerKeybind(CASTING_RING_KEY_BINDING, CastingRingHelperClient::handleCastingRingKeypress);
    }

    private static boolean isIotaWheelItem(Item item){
        return item instanceof ItemSpellbook || item instanceof ItemMultiFocus;
    }

    public static void handleIotaWheelItems(KeyBinding keyBinding, MinecraftClient client){
        handleIotaWheelItems(keyBinding, client, keyBinding.isPressed());
    }

    // modified from default wnboi to support opening from casting context
    public static void handleIotaWheelItems(KeyBinding keyBinding, MinecraftClient client, boolean isPressed){
        if(!isPressed) return;
        // HexGloop.logPrint("thread check");
        PlayerEntity player = client.player;
        if(player == null){
            return;
        }
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        KeyboundItem mainItem = null;
        KeyboundItem offItem = null;

        if(mainHand.getItem() instanceof KeyboundItem && isIotaWheelItem(mainHand.getItem())){
            mainItem = (KeyboundItem)mainHand.getItem();
        }
        if(offHand.getItem() instanceof KeyboundItem && isIotaWheelItem(offHand.getItem())){
            offItem = (KeyboundItem)offHand.getItem();
        }

        if(client.currentScreen != null && !(client.currentScreen instanceof GuiSpellcasting)){
            // there's some other screen open, probably shouldn't open ours
            return;
        } else {
            // no screen open - check if we should open one
            if(mainItem != null && mainItem.getKeyBinding() == keyBinding){
                mainItem.openScreen();
                return;
            }
            if(offItem != null && offItem.getKeyBinding() == keyBinding){
                offItem.openScreen();
                return;
            }
        }
    }
}
