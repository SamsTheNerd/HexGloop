package com.samsthenerd.hexgloop.mixins.misc;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;

@Mixin(ClientAdvancementManager.class)
public interface MixinAccessClientAdvancementProgress {
    @Accessor("advancementProgresses")
    public Map<Advancement, AdvancementProgress> getAdvancementProgresses();
}
