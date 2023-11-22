package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.SpellbookScreenInterface;
import com.samsthenerd.hexgloop.misc.wnboi.SpellbookIotaProvider;
import com.samsthenerd.hexgloop.screens.IotaWheelScreen;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

@Mixin(ItemSpellbook.class)
public class SpellbookWNBOI implements KeyboundItem, SpellbookScreenInterface{
    @Override
    @Environment(EnvType.CLIENT)
    public KeyBinding getKeyBinding(){
        return HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
    }

    @Override
    public void clearScreen(){
        IotaWheelScreen.CURRENT = null;
    }

    ItemStack spellbook = null;

    @Environment(EnvType.CLIENT)
    @Override
    public AbstractContextWheelScreen getScreen(){
        Pair<ItemStack, Boolean> spellbookResult = getSpellbook();
        spellbook = spellbookResult.getLeft();
        Screen oldScreen = MinecraftClient.getInstance().currentScreen;
        if(IotaWheelScreen.CURRENT == null || !(IotaWheelScreen.CURRENT.iotaProvider instanceof SpellbookIotaProvider)){
            IotaWheelScreen.CURRENT = new IotaWheelScreen(new SpellbookIotaProvider(spellbook), oldScreen);
        }
        ((SpellbookIotaProvider) IotaWheelScreen.CURRENT.iotaProvider).updateItemStack(spellbook);
        ((SpellbookIotaProvider) IotaWheelScreen.CURRENT.iotaProvider).mainHand = spellbookResult.getRight();
        IotaWheelScreen.CURRENT.onPage = (IotaWheelScreen.CURRENT.iotaProvider.currentSlot()-1) / IotaWheelScreen.CURRENT.iotaProvider.perPage();
        
        return IotaWheelScreen.CURRENT;
    }

    @Environment(EnvType.CLIENT)
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
