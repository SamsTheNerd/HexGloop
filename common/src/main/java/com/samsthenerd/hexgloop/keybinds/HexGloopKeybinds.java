package com.samsthenerd.hexgloop.keybinds;

import org.lwjgl.glfw.GLFW;

import com.samsthenerd.hexgloop.items.ItemFidget;
import com.samsthenerd.hexgloop.items.ItemMultiFocus;
import com.samsthenerd.hexgloop.misc.CastingRingHelperClient;
import com.samsthenerd.hexgloop.mixins.wnboi.MixinIsScrollableInvoker;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.utils.KeybindUtils.KeybindHandler;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HexGloopKeybinds {
    public static final KeyBinding IOTA_WHEEL_KEYBIND = new KeyBinding("key.hexgloop.open_iota_wheel",
			InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.hexgloop");
    // public static final KeyBinding FIDGET_WHEEL_KEYBIND = new KeyBinding("key.hexgloop.open_fidget_wheel",
	// 		InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.hexgloop");
    public static final KeyBinding CASTING_RING_KEY_BINDING = new KeyBinding("key.hexgloop.casting_ring",
    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.hexgloop");

    public static final KeyBinding HEX_SCROLL_UP = new KeyBinding("key.hexgloop.scroll_up", 
        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.hexgloop");
    public static final KeyBinding HEX_SCROLL_DOWN = new KeyBinding("key.hexgloop.scroll_down",
        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "key.categories.hexgloop");

    public static void registerKeybinds(){
        registerKeybind(IOTA_WHEEL_KEYBIND, (keyBinding, client) -> handleIotaWheelItems(keyBinding, client));
        registerKeybind(CASTING_RING_KEY_BINDING, CastingRingHelperClient::handleCastingRingKeypress);
        registerKeybind(HEX_SCROLL_UP, (keyBinding, client) -> handleScrollKey(keyBinding, client, true));
        registerKeybind(HEX_SCROLL_DOWN, (keyBinding, client) -> handleScrollKey(keyBinding, client, false));
    }

    // do it architectury instead of relying on wnboi since forge load order is mean
    public static void registerKeybind(KeyBinding keybinding, KeybindHandler handler){
        KeyMappingRegistry.register(keybinding);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (keybinding.wasPressed()) {
                handler.run(keybinding, minecraft);
            }
        });
    }

    // not the best named anymore but it's just used here. technically catches fidgets too
    private static boolean isIotaWheelItem(Item item){
        return item instanceof ItemSpellbook || item instanceof ItemMultiFocus || item instanceof ItemFidget;
    }

    public static void handleIotaWheelItems(KeyBinding keyBinding, MinecraftClient client){
        handleIotaWheelItems(keyBinding, client, keyBinding.isPressed());
    }

    public static void handleScrollKey(KeyBinding keyBinding, MinecraftClient client, boolean goUp){
        handleScrollKey(keyBinding, client, goUp, keyBinding.wasPressed());
    }

    public static void handleScrollKey(KeyBinding keyBinding, MinecraftClient client, boolean goUp, boolean isPressed){
        if(!isPressed) return;
        boolean mainHand = false;
        if(MixinIsScrollableInvoker.InvokeIsScrollableItem(client.player.getMainHandStack().getItem())){
            mainHand = true;
        } else if(!MixinIsScrollableInvoker.InvokeIsScrollableItem(client.player.getOffHandStack().getItem())){
            return;
        }
        int dir = goUp ? -1 : 1;
        IClientXplatAbstractions.INSTANCE.sendPacketToServer(
                    new MsgShiftScrollSyn(mainHand ? dir : 0, !mainHand ? dir : 0, Screen.hasControlDown(),
                        false, false));
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
