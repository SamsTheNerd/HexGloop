package com.samsthenerd.hexgloop.casting;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

public interface IContextHelper {
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
