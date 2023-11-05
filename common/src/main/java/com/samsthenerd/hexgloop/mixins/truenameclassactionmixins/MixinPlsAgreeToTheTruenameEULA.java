package com.samsthenerd.hexgloop.mixins.truenameclassactionmixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.misc.worldData.AgreeTruenameEULAState;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.common.casting.operators.rw.OpWrite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(OpWrite.class)
public class MixinPlsAgreeToTheTruenameEULA {
    @WrapOperation(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;",
    at=@At(value="INVOKE", target="at/petrak/hexcasting/api/spell/mishaps/MishapOthersName$Companion.getTrueNameFromDatum (Lat/petrak/hexcasting/api/spell/iota/Iota;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/player/PlayerEntity;"))
    public PlayerEntity wrapTruenameCheckerForEULA(MishapOthersName.Companion staticCompanion, Iota iota, PlayerEntity thisPlayer, Operation<PlayerEntity> original){
        // probably want to add a check for if the player has agreed already or not
        if(iota instanceof EntityIota entIota){
            if(entIota.getEntity() == thisPlayer && thisPlayer instanceof ServerPlayerEntity sPlayer){
                // do our mishap or whatever
                boolean hasAgreed = AgreeTruenameEULAState.getServerState(sPlayer.getServer()).checkAgreement(sPlayer.getUuid());
                if(!hasAgreed){
                    // should probably add name of the spell to cast and its symbol as arguments here
                    sPlayer.sendMessageToClient(Text.translatable("hexgloop.truename_eula"), false);
                }
            }
        }
        return original.call(staticCompanion, iota, thisPlayer);
    }
}
