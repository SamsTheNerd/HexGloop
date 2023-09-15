package com.samsthenerd.hexgloop.misc.wnboi;

import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class SpellbookIotaProvider implements IotaProvider{
    ItemStack spellbook = null;
    protected LabelMaker labelMaker = null;
    public boolean mainHand = true;
    public static final Random RANDOM = Random.create();

    // be careful to only pass this a spellbook
    public SpellbookIotaProvider(ItemStack _spellbook){
        super();
        if(_spellbook != null && _spellbook.getItem() instanceof ItemSpellbook){
            spellbook = _spellbook;
            labelMaker = new LabelMaker(spellbook);
        }
    }

    public void updateItemStack(ItemStack newSpellbook){
        spellbook = newSpellbook;
        labelMaker = new LabelMaker(spellbook);
    }

    @Override
    public int getCount(){
        return ItemSpellbook.MAX_PAGES;
    }

    @Override
    public int perPage(){
        return 8;
    }

    @Override
    public LabelMaker getLabelMaker(){
        return labelMaker;
    }

    // -1 if empty
    @Override
    public int currentSlot(){
        return ItemSpellbook.getPage(spellbook, -1);
    }

    @Override
    public NbtCompound getIotaNBT(int index){
        int idx = index + 1;
        String key = String.valueOf(idx);
        NbtCompound tag = NBTHelper.getCompound(spellbook, ItemSpellbook.TAG_PAGES);
        if (tag != null && tag.contains(key, NbtElement.COMPOUND_TYPE)) {
            return tag.getCompound(key);
        } else {
            return null;
        }
    }

    @Override
    public void toSlot(int index){
        int current = currentSlot()-1;
        // HexGloop.logPrint("going to slot " + index + " from slot " + current);
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
        NbtCompound tag = NBTHelper.getCompound(spellbook, ItemSpellbook.TAG_PAGE_NAMES);
        if (tag != null && tag.contains(key, NbtElement.STRING_TYPE)) {
            return Text.Serializer.fromJson(tag.getString(key));
        } else {
            return null;
        }
    }

}
