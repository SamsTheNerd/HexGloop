package com.samsthenerd.hexgloop.mixins.vibrations;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.misc.HexGloopGameEvents;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.world.event.GameEvent;

@Mixin(CastingHarness.class)
public class MixinCustomCastingEvent {
    @Redirect(method = "executeIotas(Ljava/util/List;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/casting/ControllerInfo;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/event/GameEvent;ITEM_INTERACT_FINISH:Lnet/minecraft/world/event/GameEvent;", opcode = Opcodes.GETSTATIC))
private GameEvent injectCustomCastingEvent() {
        return HexGloopGameEvents.CASTING_EVENT.get();
    }
}
