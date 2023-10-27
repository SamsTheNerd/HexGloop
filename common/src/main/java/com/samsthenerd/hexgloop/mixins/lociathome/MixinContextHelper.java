package com.samsthenerd.hexgloop.mixins.lociathome;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;
import com.samsthenerd.hexgloop.casting.wehavelociathome.IContextHelper;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(CastingContext.class)
public class MixinContextHelper implements IContextHelper{
    private List<BlockPos> chestRefs = new ArrayList<BlockPos>();

    public List<BlockPos> getChestRefs(){
        return new ArrayList<BlockPos>(chestRefs);
    }

    public void addChestRef(BlockPos pos){
        chestRefs.add(pos);
    }

    @Shadow
    @Final
    private ServerPlayerEntity caster;

    // might use for more stuff in the future but it's fine for now
    @Inject(method="isVecInRange(Lnet/minecraft/util/math/Vec3d;)Z",
    at=@At("RETURN"), cancellable=true)
    public void sentinelIsCozy(Vec3d pos, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue() && caster != null){
            BlockState state = caster.getWorld().getBlockState(new BlockPos(pos));
            if(state.getBlock() == HexGloopBlocks.SENTINEL_BED_BLOCK.get())
                cir.setReturnValue(true);
        }
    }

    private ItemStack kittyStack = null;

    public void setKitty(ItemStack kitty){
        kittyStack = kitty;
    }

    public ItemStack getKitty(){
        return kittyStack;
    }

    private StackReference cursorRef = null;

    public void setCursorRef(StackReference cursorRef){
        this.cursorRef = cursorRef;
    }

    public StackReference getCursorRef(){
        return cursorRef;
    }

    private KittyContext kittyContext = null;

    public void setKittyContext(KittyContext kCtx){
        kittyContext = kCtx;
    }

    public KittyContext getKittyContext(){
        return kittyContext;
    }
}
