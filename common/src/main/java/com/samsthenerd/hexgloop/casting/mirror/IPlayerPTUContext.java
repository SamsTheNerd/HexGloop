package com.samsthenerd.hexgloop.casting.mirror;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough.PassThroughUseContext;

public interface IPlayerPTUContext {
    @Nullable
    public PassThroughUseContext<?,?> getPTUContext();

    public void setPTUContext(@Nullable PassThroughUseContext<?,?> context);

    default public void clearPTUContext(){
        setPTUContext(null);
    }

    default public boolean hasPTUContext(){
        return getPTUContext() != null;
    }
}
