package com.samsthenerd.hexgloop.casting.mishapprotection;

import net.minecraft.text.Text;

public interface IMishapStorage {
    public Text getLastMishap();

    public void setLastMishap(Text mishap);
}
