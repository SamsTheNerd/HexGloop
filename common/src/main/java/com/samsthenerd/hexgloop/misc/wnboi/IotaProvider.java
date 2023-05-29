package com.samsthenerd.hexgloop.misc.wnboi;



import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

/*
 * An interface that gives Iotas for the IotaWheelScreen. 
 */
public interface IotaProvider{
    public int getCount(); // total number of iotas

    // so spellbook will have 8 per page
    public int perPage();

    public int currentSlot();

    public NbtCompound getIotaNBT(int index);

    // moves you through the slots
    public void toSlot(int index);

    public Random getRNG();

    public Text getName(int index);

    public default FrozenColorizer getColorizer(){
        return IXplatAbstractions.INSTANCE.getColorizer(MinecraftClient.getInstance().player);
    }

    // want label-based stuff here - need to setup that class first though - also need to make sure that we keep server/client sidedness safe
}