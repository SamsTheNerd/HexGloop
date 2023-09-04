package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.UUID;

import javax.annotation.Nullable;

/*
 * interface to be injected onto EntityIota to allow for truename locking
 */
public interface ILockedIota{
    // gets the key that *this* iota has
    @Nullable
    public UUID getUUIDKey();

    public void setUUIDKey(UUID newKey);
}