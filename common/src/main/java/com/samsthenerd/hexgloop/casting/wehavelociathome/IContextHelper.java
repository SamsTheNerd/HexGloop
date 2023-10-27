package com.samsthenerd.hexgloop.casting.wehavelociathome;

import java.util.List;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IContextHelper {
    public List<BlockPos> getChestRefs();

    public void addChestRef(BlockPos pos);

    // stuff for kitty casting:

    public default boolean isKitty(){
        return getKitty() != null;
    }

    public void setKittyContext(KittyContext kCtx);

    public KittyContext getKittyContext();

    public ItemStack getKitty();

    public void setKitty(ItemStack kitty);

    public void setCursorRef(StackReference cursorRef);

    public StackReference getCursorRef();
}
