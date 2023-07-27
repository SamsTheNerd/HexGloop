package com.samsthenerd.hexgloop.misc.wnboi;

public interface LabelProvider {
    public int getCount(); // total number of iotas

    // so spellbook will have 8 per page
    public int perPage();

    public int currentSlot();

    public LabelMaker getLabelMaker();

    // moves you through the slots
    public void toSlot(int index);
}
