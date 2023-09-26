package com.samsthenerd.hexgloop.casting.orchard;

import at.petrak.hexcasting.api.spell.math.HexPattern;

// this system should mostly be non-persistent, so don't expect it to work across disconnects
public interface IOrchardKeybind {
    public void setAssociation(HexPattern pattern, boolean isActivated);

    public boolean getAssociation(HexPattern pattern);
}
