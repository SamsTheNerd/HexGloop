package com.samsthenerd.hexgloop.items;

import java.util.List;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.wnboi.IotaProvider;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;
import com.samsthenerd.hexgloop.screens.IotaWheelScreen;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

// it's functionally just a restricted spellbook (that's not end locked)
public class ItemMultiFocus extends Item implements KeyboundItem, IotaHolderItem, LabelyItem{
    public final static int MAX_FOCI_SLOTS = 6;

    @Environment(EnvType.CLIENT)
    public IotaWheelScreen screen;

    ItemStack multifocus = null;

    @Override
    @Environment(EnvType.CLIENT)
    public KeyBinding getKeyBinding(){
        return HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
    }

    public ItemMultiFocus(Settings settings){
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AbstractContextWheelScreen getScreen(){
        Pair<ItemStack, Boolean> handItemResult = getHandItem();
        multifocus = handItemResult.getLeft();
        Screen oldScreen = MinecraftClient.getInstance().currentScreen;
        if(screen == null){
            HexGloop.logPrint("multifocus is" + (multifocus == null ? "null" : multifocus.getName().toString()));
            screen = new IotaWheelScreen(new MultiFocusIotaProvider(multifocus), oldScreen);
        }
        ((MultiFocusIotaProvider) screen.iotaProvider).updateItemStack(multifocus);
        ((MultiFocusIotaProvider) screen.iotaProvider).mainHand = handItemResult.getRight();
        screen.onPage = (screen.iotaProvider.currentSlot()-1) / screen.iotaProvider.perPage();
        return screen;
    }

    @Environment(EnvType.CLIENT)
    public Pair<ItemStack, Boolean> getHandItem(){
        ItemStack mainStack = MinecraftClient.getInstance().player.getMainHandStack();
        if(mainStack.getItem() instanceof ItemMultiFocus){
            return new Pair<ItemStack, Boolean>(mainStack, true);
        }
        ItemStack offStack = MinecraftClient.getInstance().player.getOffHandStack();
        if(offStack.getItem() instanceof ItemMultiFocus){
            return new Pair<ItemStack, Boolean>(offStack, false);
        }
        return null;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        HexItems.SPELLBOOK.writeDatum(stack, iota);
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return iota == null || !ItemSpellbook.isSealed(stack);
    }

    @Override
    @Nullable
    public NbtCompound readIotaTag(ItemStack stack){
        return HexItems.SPELLBOOK.readIotaTag(stack);
    }

    public NbtCompound readSlotIotaTag(ItemStack stack, int index){
        String key = String.valueOf(index);
        NbtCompound tag = NBTHelper.getCompound(stack, ItemSpellbook.TAG_PAGES);
        if (tag != null && tag.contains(key, NbtElement.COMPOUND_TYPE)) {
            return tag.getCompound(key);
        } else {
            return null;
        }
    }

    public static int rotatePageIdx(ItemStack stack, boolean increase) {
        int idx = ItemSpellbook.getPage(stack, 0);
        if (idx != 0) {
            idx += increase ? 1 : -1;
            if(idx <= 0) idx = MAX_FOCI_SLOTS;
            if(idx > MAX_FOCI_SLOTS) idx = 1;
            idx = Math.max(1, idx);
        }
        idx = MathHelper.clamp(idx, 0, MAX_FOCI_SLOTS);
        NBTHelper.putInt(stack, ItemSpellbook.TAG_SELECTED_PAGE, idx);

        NbtCompound names = NBTHelper.getCompound(stack, ItemSpellbook.TAG_PAGE_NAMES);
        int shiftedIdx = Math.max(1, idx);
        String nameKey = String.valueOf(shiftedIdx);
        String name = NBTHelper.getString(names, nameKey);
        if (name != null) {
            stack.setCustomName(Text.Serializer.fromJson(name));
        } else {
            stack.removeCustomName();
        }

        return idx;
    }

    @Override
    public void inventoryTick(ItemStack stack, World pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        int index = ItemSpellbook.getPage(stack, 0);
        NBTHelper.putInt(stack, ItemSpellbook.TAG_SELECTED_PAGE, index);

        int shiftedIdx = Math.max(1, index);
        String nameKey = String.valueOf(shiftedIdx);
        NbtCompound names = NBTHelper.getOrCreateCompound(stack, ItemSpellbook.TAG_PAGE_NAMES);
        if (stack.hasCustomName()) {
            names.putString(nameKey, Text.Serializer.toJson(stack.getName()));
        } else {
            names.remove(nameKey);
        }
    }

    // render tooltips the same way spellbook does
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World level, List<Text> tooltip,
                                TooltipContext isAdvanced) {
        HexItems.SPELLBOOK.appendTooltip(stack, level, tooltip, isAdvanced);
    }

    @Nullable
    public boolean putLabel(ItemStack stack, NbtCompound labelNbt){
        int index = ItemSpellbook.getPage(stack, 0);
        return putLabel(stack, index, labelNbt);
    }

    public class MultiFocusIotaProvider implements IotaProvider{
        protected ItemStack multifocus = null;
        protected LabelMaker labelMaker = null;
        public boolean mainHand = true;
        public static final Random RANDOM = Random.create();

        // be careful to only pass this a spellbook
        public MultiFocusIotaProvider(ItemStack _multifocus){
            super();
            if(_multifocus != null && _multifocus.getItem() instanceof ItemMultiFocus){
                multifocus = _multifocus;
                labelMaker = new LabelMaker(multifocus);
            }
        }

        public void updateItemStack(ItemStack newMulitFocus){
            multifocus = newMulitFocus;
            labelMaker = new LabelMaker(multifocus);
        }

        @Override
        public int getCount(){
            return MAX_FOCI_SLOTS;
        }

        @Override
        public int perPage(){
            return Math.min(MAX_FOCI_SLOTS, 8);
        }

        // -1 if empty
        @Override
        public int currentSlot(){
            return ItemSpellbook.getPage(multifocus, -1);
        }

        @Override
        public LabelMaker getLabelMaker(){
            return labelMaker;
        }

        @Override
        public NbtCompound getIotaNBT(int index){
            int idx = index + 1;
            String key = String.valueOf(idx);
            NbtCompound tag = NBTHelper.getCompound(multifocus, ItemSpellbook.TAG_PAGES);
            if (tag != null && tag.contains(key, NbtElement.COMPOUND_TYPE)) {
                return tag.getCompound(key);
            } else {
                return null;
            }
        }

        @Override
        public void toSlot(int index){
            int current = currentSlot()-1;
            HexGloop.logPrint("going to slot " + index + " from slot " + current);
            int dist = Math.abs(index - current);
            int invert = (dist == (index - current)) ? -1 : 1;
            for(int i = 0; i < dist; i++){
                IClientXplatAbstractions.INSTANCE.sendPacketToServer(
                        new MsgShiftScrollSyn(mainHand ? invert*dist : 0, !mainHand ? invert*dist : 0, true,
                            false, false));
            }
        }

        @Override
        public Random getRNG(){
            return RANDOM;
        }

        @Override
        public Text getName(int index){
            int idx = index + 1;
            String key = String.valueOf(idx);
            NbtCompound tag = NBTHelper.getCompound(multifocus, ItemSpellbook.TAG_PAGE_NAMES);
            if (tag != null && tag.contains(key, NbtElement.STRING_TYPE)) {
                return Text.Serializer.fromJson(tag.getString(key));
            } else {
                return null;
            }
        }
    }
}
