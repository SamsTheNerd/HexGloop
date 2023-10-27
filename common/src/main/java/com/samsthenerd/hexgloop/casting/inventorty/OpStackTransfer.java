package com.samsthenerd.hexgloop.casting.inventorty;

import java.util.List;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.GrabbableStack;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class OpStackTransfer implements ConstMediaAction{

    public OpStackTransfer(){
    }

    @Override
    public int getArgc(){ return 3;}

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
        InventortyUtils.assertKittyCasting(context);
        GrabbableStack fromGrabbable = InventortyUtils.getStackFromGrabbable(args.get(0), context, 0, getArgc());
        ItemStack fromStack = fromGrabbable.getStack();
        int transferCount = Math.min(OperatorUtils.getInt(args, 2, getArgc()), fromStack.getCount());
        int amtLeft = transferCount;
        ItemEntity maybeNewEnt = null;
        if(args.get(1) instanceof NullIota){
            // want to make a new item entity
            ItemStack newStack = fromStack.split(transferCount);
            Vec3d playerPos = context.getCaster().getPos();
            ItemEntity newEnt = new ItemEntity(context.getWorld(), playerPos.x, playerPos.y, playerPos.z, newStack, 0,0,0);
            context.getWorld().spawnEntity(newEnt);
            amtLeft = fromStack.getCount();
            maybeNewEnt = newEnt;
        } else {
            GrabbableStack toGrabbable = InventortyUtils.getStackFromGrabbable(args.get(1), context, 1, getArgc());
            // HexGloop.logPrint("In OpStackTransfer:\n\ttoGrabbable: " + toGrabbable.getStack() + "\n\tfromGrabbable: " + fromGrabbable.getStack() +
            //     "\n\tcanTake: " + fromGrabbable.canTake(context.getCaster()) +
            //     "\n\tcanInsert: " + toGrabbable.canInsert(fromGrabbable.getStack())); 
            transferCount = Math.min(transferCount, 
                toGrabbable.getMaxCount() - (toGrabbable.getStack().isEmpty() ? 0 : toGrabbable.getStack().getCount()) 
            );
            
            if(ItemStack.canCombine(fromGrabbable.getStack(), toGrabbable.getStack()) || toGrabbable.getStack().isEmpty()){
                ItemStack takenStack = fromGrabbable.takeStack(transferCount, context.getCaster());
                // HexGloop.logPrint("transferCount: " + transferCount + "; takenStack: " + takenStack + "; fromGrabbable: " + fromGrabbable.getStack());
                toGrabbable.insertStack(takenStack);
                amtLeft = fromGrabbable.getStack().getCount();
            }
        }
        return maybeNewEnt == null ? List.of(new DoubleIota(amtLeft)) : List.of(new DoubleIota(amtLeft), new EntityIota(maybeNewEnt));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}



