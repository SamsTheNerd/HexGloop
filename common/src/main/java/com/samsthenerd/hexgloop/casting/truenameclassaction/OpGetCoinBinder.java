package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.List;
import java.util.UUID;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.compat.moreIotas.MoreIotasMaybeIotas;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem;
import dev.architectury.platform.Platform;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

public class OpGetCoinBinder implements ConstMediaAction {
    boolean checkEquals;

    public OpGetCoinBinder(boolean checkEquals){
        this.checkEquals = checkEquals;
    }

    @Override
    public int getArgc(){ return checkEquals ? 1 : 0;}

    @Override
    public int getMediaCost(){
        return 0;
    }

    @Override
    public boolean isGreat(){ return false;}

    @Override
    public boolean getCausesBlindDiversion(){ return false;}

    @Override 
    public boolean getAlwaysProcessGreatSpell(){ return false;}

    @Override
    public Text getDisplayName(){ 
        return DefaultImpls.getDisplayName(this);
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingContext context){
        ServerPlayerEntity player = context.getCaster();
        Hand nonCastingHand = context.getOtherHand();
        ItemStack maybeCoinStack = player.getStackInHand(nonCastingHand);
        // check the other hand just in case
        if(maybeCoinStack.getItem() != HexGloopItems.CASTERS_COIN.get()){
            maybeCoinStack = player.getStackInHand(context.getCastingHand());
        }
        if(maybeCoinStack.getItem() != HexGloopItems.CASTERS_COIN.get()){
            // still mishap on offhand, since checking main hand isn't useful in most cases
            MishapThrowerWrapper.throwMishap(new MishapBadOffhandItem(maybeCoinStack, nonCastingHand, Text.translatable("hexcasting.mishap.bad_offhand_item.caster_bound")));
        }

        Pair<UUID, String> casterInfo = HexGloopItems.CASTERS_COIN.get().getBoundCaster(maybeCoinStack);

        if(checkEquals){
            Entity ent = OperatorUtils.getEntity(args, 0, getArgc());
            return List.of(new BooleanIota(casterInfo.getLeft() == ent.getUuid()));
        } else if(Platform.isModLoaded("moreiotas")){
            return List.of(MoreIotasMaybeIotas.makeStringIota(casterInfo.getRight()));
        } else {
            return List.of(new NullIota());
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}