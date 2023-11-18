package com.samsthenerd.hexgloop.casting;

import java.util.List;

import org.jetbrains.annotations.NotNull;

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
import kotlin.Triple;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class OpConjureTastyTreat implements SpellAction {

    public OpConjureTastyTreat(){
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
        // position to summon snacks
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        context.assertVecInRange(pos);
        ItemStack snackStack = HexGloopItems.HEX_SNACK.get().getDefaultStack();
        snackStack.setCount(1);
        ItemEntity snack = new ItemEntity(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), snackStack);
        
        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(new Spell(snack), MediaConstants.DUST_UNIT, List.of());
        // return List.of(new EntityIota(snack));
    }

    public class Spell implements RenderedSpell{
        private ItemEntity snack;
        public Spell(ItemEntity snack){
            this.snack = snack;
        }

        public void cast(CastingContext ctx){
            ctx.getWorld().spawnEntity(snack);
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}
