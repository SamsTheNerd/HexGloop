package com.samsthenerd.hexgloop.casting.mirror;

import javax.annotation.Nullable;

// meant for player and casting context, but left ambiguous because it's gonna have to be injected onto there anyways
// also meant for serverside
public interface IMirrorBinder extends IShallowMirrorBinder{

    // temp really only makes sense for when it's implemented by casting context
    public void bindTo(@Nullable BoundMirror mirror, boolean temp);

    default public void bindTo(@Nullable BoundMirror mirror){
        bindTo(mirror, false);
    }

    @Nullable
    public BoundMirror getBoundMirror();
}
