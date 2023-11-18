package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import at.petrak.hexcasting.interop.HexInterop;
import vazkii.patchouli.api.PatchouliAPI;

// yoinked from hexal
@Mixin(HexInterop.class)
public abstract class MixinHexInterop {
    @Inject(
            method = "initPatchouli",
            at = @At("RETURN"),
            remap = false
    )
    private static void forceInitForAddons(CallbackInfo ci) {
        PatchouliAPI.get().setConfigFlag(HexInterop.PATCHOULI_ANY_INTEROP_FLAG, true);
    }
}