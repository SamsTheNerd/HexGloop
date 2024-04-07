package com.samsthenerd.hexgloop.casting.canvas;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemSlateCanvas;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import kotlin.collections.CollectionsKt;
import net.minecraft.block.MapColor;
import net.minecraft.block.MapColor.Brightness;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class OpPutColor implements Action{

    public OpPutColor(){
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
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext context){
        // potentially get an entity first -- deal with that later
        ItemStack mapStack = context.getHeldItemToOperateOn(itemstack -> itemstack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())).component1();

        HexGloop.logPrint("args: " + stack.toString());

        int brushsize = 1;
        try {
            brushsize = OperatorUtils.getIntBetween(List.of(stack.get(stack.size()-1)), 0, 1, 128, 1);
            stack.remove(stack.size()-1);
        } catch (Throwable mishap) {
            // do nothing
            HexGloop.logPrint("mishap: " + mishap);
        }

        List<Iota> args = CollectionsKt.takeLast(stack, 3);
        
        Integer x = null;
        if(!(args.get(0) instanceof NullIota)){
            x = OperatorUtils.getIntBetween(args, 0, 0, 127, 3);   
        }
        Integer y = null;
        if(!(args.get(1) instanceof NullIota)){
            y = OperatorUtils.getIntBetween(args, 1, 0, 127, 3);
        }
        byte closestColor = MapColor.CLEAR.getRenderColorByte(Brightness.NORMAL);
        if(!(args.get(2) instanceof NullIota)){
            Vec3d colorVec = OperatorUtils.getVec3(args, 2, 3);
            closestColor = SlateCanvasUtils.getClosestMapColor(new Vec3i(colorVec.x, colorVec.y, colorVec.z), context.getCaster());
        }
        
        for(int i = 0; i < 3; i++){
            stack.remove(stack.size()-1);
        }

        Integer mapId = FilledMapItem.getMapId(mapStack);
        if(mapId == null){
            HexGloop.logPrint("creating a new map");
            Pair<MapState, Integer> newMapData = ItemSlateCanvas.createMapState(context.getWorld());
            mapId = newMapData.getRight();
            ItemSlateCanvas.setCanvasMapId(mapStack, mapId);
        }
        HexGloop.logPrint("map id: " + mapId);
        MapState mapState = FilledMapItem.getMapState(mapId, context.getWorld());
        if(x == null && y == null){ // paint whole thing
            for(int i = 0; i < 128; i++){
                for(int j = 0; j < 128; j++){
                    mapState.setColor(i, j, closestColor);
                }
            }
        } else {
            int startX = 0, startY = 0;
            int endX = 128, endY = 128;

            if(x != null){
                startX = (int)(x - Math.floor((brushsize-1)/2.0));
                endX = (int)(x + Math.ceil((brushsize+1)/2.0));
            }

            if(y != null){
                startY = (int)(y - Math.floor((brushsize-1)/2.0));
                endY = (int)(y + Math.ceil((brushsize+1)/2.0));
            }

            // clamp all the values down to actual sizes
            startX = Math.min(Math.max(0, startX), 128);
            startY = Math.min(Math.max(0, startY), 128);
            endX = Math.min(Math.max(0, endX), 128);
            endY = Math.min(Math.max(0, endY), 128);

            for(int i = startX; i < endX; i++){
                for(int j = startY; j < endY; j++){
                    mapState.setColor(i, j, closestColor);
                }
            }
        }
        return new OperationResult(continuation, stack, ravenmind, new ArrayList<OperatorSideEffect>());
    }
    
}