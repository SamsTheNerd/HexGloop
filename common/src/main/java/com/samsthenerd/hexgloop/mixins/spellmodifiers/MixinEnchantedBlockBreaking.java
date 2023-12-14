package com.samsthenerd.hexgloop.mixins.spellmodifiers;

import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinEnchantedBlockBreaking {
    @ModifyExpressionValue(
        method="breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;I)Z",
        at=@At(value="FIELD", target="net/minecraft/item/ItemStack.EMPTY : Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack modifyBreakingBlock(ItemStack original, BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth){
        if(breakingEntity instanceof ServerPlayerEntity sPlayer){
            Set<CastingHarness> harnesses = ((ICADHarnessStorage)(Object)sPlayer).getHarnesses();
            if(!harnesses.iterator().hasNext()) return original;
            CastingHarness harness = harnesses.iterator().next();
            ItemStack maybeStack = ((IContextHelper)(Object)(harness.getCtx())).getCastingItem();
            if(maybeStack != null){
                return maybeStack;
            }
        }

        return original;
    }
}
