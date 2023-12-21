package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.client.book.template.component.ComponentCustom;

@Mixin(ComponentCustom.class)
public interface MixinClientGetCustomComponent {
    @Accessor("callbacks")
    ICustomComponent getCallbacks();
}
