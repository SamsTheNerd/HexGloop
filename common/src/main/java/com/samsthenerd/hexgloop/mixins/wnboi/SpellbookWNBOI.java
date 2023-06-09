package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.misc.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.SpellbookScreenInterface;
import com.samsthenerd.hexgloop.misc.wnboi.SpellbookIotaProvider;
import com.samsthenerd.hexgloop.screens.IotaWheelScreen;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

@Mixin(ItemSpellbook.class)
public class SpellbookWNBOI implements KeyboundItem, SpellbookScreenInterface{
    @Override
    public KeyBinding getKeyBinding(){
        return HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
    }

    public IotaWheelScreen screen = null;

    @Override
    public void clearScreen(){
        screen = null;
    }

    ItemStack spellbook = null;

    @Override
    public AbstractContextWheelScreen getScreen(){
        Pair<ItemStack, Boolean> spellbookResult = getSpellbook();
        spellbook = spellbookResult.getLeft();
        Screen oldScreen = MinecraftClient.getInstance().currentScreen;
        if(screen == null){
            screen = new IotaWheelScreen(new SpellbookIotaProvider(spellbook), oldScreen);
        }
        ((SpellbookIotaProvider) screen.iotaProvider).updateItemStack(spellbook);
        ((SpellbookIotaProvider) screen.iotaProvider).mainHand = spellbookResult.getRight();
        screen.onPage = (screen.iotaProvider.currentSlot()-1) / screen.iotaProvider.perPage();
        return screen;
    }

    public Pair<ItemStack, Boolean> getSpellbook(){
        ItemStack mainStack = MinecraftClient.getInstance().player.getMainHandStack();
        if(mainStack.getItem() instanceof ItemSpellbook){
            return new Pair<ItemStack, Boolean>(mainStack, true);
        }
        ItemStack offStack = MinecraftClient.getInstance().player.getOffHandStack();
        if(offStack.getItem() instanceof ItemSpellbook){
            return new Pair<ItemStack, Boolean>(offStack, false);
        }
        return null;
    }
}
