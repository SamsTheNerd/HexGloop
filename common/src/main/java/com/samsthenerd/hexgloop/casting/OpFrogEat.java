package com.samsthenerd.hexgloop.casting;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.ParticleSpray;
import at.petrak.hexcasting.api.spell.RenderedSpell;
import at.petrak.hexcasting.api.spell.SpellAction;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity;
import kotlin.Triple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class OpFrogEat implements SpellAction {

    public OpFrogEat(){
    }

    @Override
    public int getArgc(){ return 1;}

    public boolean hasCastingSound(@NotNull CastingContext ctx){
        return false;
    }

    public boolean awardsCastingStat(@NotNull CastingContext ctx){
        return true;
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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingContext context){
        IContextHelper contextHelper = (IContextHelper)(Object)context;
        if(!contextHelper.isFrogCasting()){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List.of("frog_casting")));
            return null;
        }
        ItemStack frogStack = contextHelper.getFrog();
        LivingEntity target = OperatorUtils.getLivingEntityButNotArmorStand(args, 0, getArgc());
        
        if(!FrogEntity.isValidFrogFood(target)){
            MishapThrowerWrapper.throwMishap(MishapBadEntity.of(target, "not_frog_food"));
            return null;
        }

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(target, frogStack), 
            MediaConstants.SHARD_UNIT,
            List.of());
    }

    public class Spell implements RenderedSpell{
        private LivingEntity target = null;
        private ItemStack frogStack = null;

        public Spell(LivingEntity target, ItemStack frogStack){
            this.target = target;
            this.frogStack = frogStack;
        }

        public void cast(CastingContext ctx){
            if(target == null || frogStack == null) return;
            FrogEntity fakeFrog = EntityType.FROG.create(ctx.getWorld());
            fakeFrog.setVariant(HexGloopItems.CASTING_FROG_ITEM.get().getFrogVariant(frogStack));
            // not sure if this will target only slimes and whatnot 
            ctx.getWorld().playSoundFromEntity(null, fakeFrog, SoundEvents.ENTITY_FROG_EAT, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            fakeFrog.tryAttack(target);
            if (!target.isAlive()) {
                target.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}

