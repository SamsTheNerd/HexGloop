package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.inventory.StackReference;
import net.minecraft.screen.ScreenHandler;

@Mixin(ScreenHandler.class)
public interface MixinExposeCursorStackRef {
    @Invoker("getCursorStackReference")
    public StackReference invokeGetCursorStackReference();
}
