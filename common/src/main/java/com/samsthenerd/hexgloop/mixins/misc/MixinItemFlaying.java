package com.samsthenerd.hexgloop.mixins.misc;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.samsthenerd.hexgloop.blocks.IDynamicFlayTarget;
import com.samsthenerd.hexgloop.casting.BrainsweepSpellInvoker;
import com.samsthenerd.hexgloop.items.IFlayableItem;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.spell.ParticleSpray;
import at.petrak.hexcasting.api.spell.RenderedSpell;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.casting.operators.spells.great.OpBrainsweep;
import at.petrak.hexcasting.common.misc.Brainsweeping;
import kotlin.Triple;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(OpBrainsweep.class)
public class MixinItemFlaying {

    @WrapOperation(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;",
    at=@At(value="INVOKE", target="at/petrak/hexcasting/api/spell/OperatorUtils.getVillager (Ljava/util/List;II)Lnet/minecraft/entity/passive/VillagerEntity;"))
    public VillagerEntity findDifferentSacrifice(List<? extends Iota> args, int index, int argc, Operation<VillagerEntity> original, 
        List<? extends Iota> methodArgsIgnore, CastingContext ctx, @Share("itement") LocalRef<ItemEntity> itemEntRef){
        // i mean it should always be zero but still
        itemEntRef.set(null); // just reset it in case it still has anything from the last call
        if(index >= 0 && index < args.size()){
            Iota sacrificeIota = args.get(index);
            if(sacrificeIota instanceof EntityIota eIota && eIota.getEntity() instanceof ItemEntity itemEnt){
                ItemStack stack = itemEnt.getStack();
                if(stack.getItem() instanceof IFlayableItem flayable){
                    VillagerEntity villager = flayable.getFlayableVillager(stack, itemEnt, ctx);
                    if(villager != null){
                        itemEntRef.set(itemEnt);
                        return villager;
                    }
                }
            }
        }
        return original.call(args, index, argc);
    }

    // @Inject(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;",
    // at=@At("RETURN"))
    // public void brainsweepItem(@NotNull List<? extends Iota> args, @NotNull CastingContext ctx, 
    //     CallbackInfo ci, @Share("itement") LocalRef<ItemEntity> itemEntRef){
    //     ItemEntity itemEnt = itemEntRef.get();
    //     if(itemEnt == null) return;
    //     ItemStack stack = itemEnt.getStack();
    //     // it should be, but still just to safely cast
    //     if(stack.getItem() instanceof IFlayableItem flayable){
    //         flayable.handleBrainsweep(stack, itemEnt, ctx);
    //     }
    // }

    // @WrapOperation(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;",
    // at=@At(value="INVOKE", target="kotlin/Triple.<init> (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"), remap=false)
    // public void addSacrificeProviderToSpell(Triple<Object, Object, Object> originalTriple, Object spell, Object cost, Object particleSprays, 
    //     Operation<Void> original, @Share("itement") LocalRef<ItemEntity> itemEntRef){

    //     ItemEntity itemEnt = itemEntRef.get();
    //     if(spell instanceof BrainsweepSpellInvoker spellMixin && itemEnt != null){
    //         spellMixin.setSacrificeProvider(itemEnt);
    //     }
    //     original.call(originalTriple, spell, cost, particleSprays);
    // }

    @ModifyArg(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;",
    at=@At(value="INVOKE", target="kotlin/Triple.<init> (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"), remap=false, index=0)
    public Object addSacrificeProviderToSpell(Object spell, @Share("itement") LocalRef<ItemEntity> itemEntRef){
        ItemEntity itemEnt = itemEntRef.get();
        if(spell instanceof BrainsweepSpellInvoker spellMixin && itemEnt != null){
            spellMixin.setSacrificeProvider(itemEnt);
        }
        return spell;
    }


    @Inject(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Lkotlin/Triple;", cancellable=true,
    at=@At(value="INVOKE", target="net/minecraft/server/world/ServerWorld.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    public void tryFlayIntoDynamicBlockTarget(@NotNull List<? extends Iota> args, @NotNull CastingContext ctx, 
        CallbackInfoReturnable< Triple<RenderedSpell, Integer, List<ParticleSpray>> > cir, 
        @Local(ordinal = 0) BlockPos pos, @Local(ordinal=0) VillagerEntity sacrifice, @Share("itement") LocalRef<ItemEntity> itemEntRef){
        
        BlockState state = ctx.getWorld().getBlockState(pos);
        if(!(state.getBlock() instanceof IDynamicFlayTarget target)) return;
        // maybe move this into a spell ?
        boolean accepted = target.canAcceptMind(sacrifice, pos, ctx);
        if(accepted){
            cir.setReturnValue(new Triple<>(
                new FlaySpell(sacrifice, pos, itemEntRef.get()),
                10 * MediaConstants.CRYSTAL_UNIT,
                List.of(ParticleSpray.cloud(sacrifice.getPos(), 1.0, 20), ParticleSpray.burst(Vec3d.ofCenter(pos), 0.3, 100))
            ));
        }
    }

    private class FlaySpell implements RenderedSpell{
        private VillagerEntity sacrifice;
        private BlockPos pos;
        private ItemEntity sacrificeProvider;

        public FlaySpell(VillagerEntity sacrifice, BlockPos pos, @Nullable ItemEntity sacrificeProvider){
            this.sacrifice = sacrifice;
            this.pos = pos;
            this.sacrificeProvider = sacrificeProvider;
        }
        public void cast(CastingContext ctx) {
            // ctx.getWorld().setBlockAndUpdate(pos, BrainsweepRecipe.copyProperties(state, recipe.result))
            BlockState state = ctx.getWorld().getBlockState(pos);
            if(!(state.getBlock() instanceof IDynamicFlayTarget target)) return;
            target.absorbVillagerMind(sacrifice, pos, ctx);

            // handle the actual sacrifice
            if(sacrificeProvider == null){
                Brainsweeping.brainsweep(sacrifice);
                if (HexConfig.server().doVillagersTakeOffenseAtMindMurder()) {
                    // sacrifice.notifyDeath(ctx.getCaster());
                }
                ctx.getWorld().playSound((PlayerEntity)null, sacrifice.getBlockPos(), SoundEvents.ENTITY_VILLAGER_DEATH, SoundCategory.AMBIENT, 0.8f, 1f);
            } else {
                ItemStack providerStack = sacrificeProvider.getStack();
                if(providerStack.getItem() instanceof IFlayableItem flayableItem){
                    Consumer<ItemStack> resultConsumer = (result) -> {
                        ItemEntity resultEnt = new ItemEntity(ctx.getWorld(), sacrificeProvider.getX(), sacrificeProvider.getY(), sacrificeProvider.getZ(), result, 0, 0, 0);
                        ctx.getWorld().spawnEntity(resultEnt);
                    };
                    flayableItem.handleBrainsweep(providerStack, sacrificeProvider, ctx, resultConsumer);
                }
            }

            ctx.getWorld().playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 0.5f, 0.8f);
        }
    }


    @Mixin(targets="at.petrak.hexcasting.common.casting.operators.spells.great.OpBrainsweep$Spell")
    public static class MixinBrainsweepSpell implements BrainsweepSpellInvoker{
        private ItemEntity sacrificeProvider = null;

        public void setSacrificeProvider(ItemEntity provider){
            sacrificeProvider = provider;
        }

        @WrapOperation(method="cast(Lat/petrak/hexcasting/api/spell/casting/CastingContext;)V", 
        at=@At(value="INVOKE", target="at/petrak/hexcasting/common/misc/Brainsweeping.brainsweep (Lnet/minecraft/entity/mob/MobEntity;)V"))
        public void wrapBrainsweep(MobEntity sacrifice, Operation<Void> original, CastingContext ctx){
            if(sacrificeProvider == null){
                original.call(sacrifice);
            } else {
                ItemStack providerStack = sacrificeProvider.getStack();
                if(providerStack.getItem() instanceof IFlayableItem flayableItem){
                    Consumer<ItemStack> resultConsumer = (result) -> {
                        ItemEntity resultEnt = new ItemEntity(ctx.getWorld(), sacrificeProvider.getX(), sacrificeProvider.getY(), sacrificeProvider.getZ(), result, 0, 0, 0);
                        ctx.getWorld().spawnEntity(resultEnt);
                    };
                    flayableItem.handleBrainsweep(providerStack, sacrificeProvider, ctx, resultConsumer);
                }
            }
        }

        @WrapOperation(method="cast(Lat/petrak/hexcasting/api/spell/casting/CastingContext;)V", 
        at=@At(value="INVOKE", target="at/petrak/hexcasting/ktxt/AccessorWrappers.tellWitnessesThatIWasMurdered (Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/entity/Entity;)V"))
        public void maybeCancelNotifyDeath(VillagerEntity sacrifice, Entity murderer, Operation<Void> original, CastingContext ctx){
            if(sacrificeProvider != null){
                ItemStack providerStack = sacrificeProvider.getStack();
                if(providerStack.getItem() instanceof IFlayableItem flayableItem){
                    if(!flayableItem.wasMurderous(providerStack, sacrificeProvider, ctx)) return;
                }
            }
            original.call(sacrifice);
        }


        @WrapOperation(method="cast(Lat/petrak/hexcasting/api/spell/casting/CastingContext;)V", 
        at=@At(value="INVOKE", target="net/minecraft/server/world/ServerWorld.playSoundFromEntity (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
        public void maybeDontMakeVillagerDeathNoise(ServerWorld world, PlayerEntity player, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float probablyVolume, float probablyPitch, Operation<Void> original, CastingContext ctx){
            if(sacrificeProvider != null && soundEvent.equals(SoundEvents.ENTITY_VILLAGER_DEATH)){
                ItemStack providerStack = sacrificeProvider.getStack();
                if(providerStack.getItem() instanceof IFlayableItem flayableItem){
                    if(!flayableItem.wasMurderous(providerStack, sacrificeProvider, ctx)) return;
                }
            }
            original.call(world, player, entity, soundEvent, soundCategory, probablyVolume, probablyPitch);
        }
    }
}
