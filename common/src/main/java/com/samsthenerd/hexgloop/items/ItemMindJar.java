package com.samsthenerd.hexgloop.items;

import java.util.Optional;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemMindJar extends Item implements IFlayableItem {

    private static String STORED_VILLAGER = "villager_mind";

    public ItemMindJar(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        for(VillagerEntity villager : world.getEntitiesByType(EntityType.VILLAGER, user.getBoundingBox().expand(5), (villager) -> {
            HexGloop.logPrint("found villager " + villager);
            return true;
        })){
            NbtCompound nbt = new NbtCompound();
            villager.saveSelfNbt(nbt);
            stack.getOrCreateNbt().put(STORED_VILLAGER, villager.writeNbt(nbt));
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, stack);
    }

    public VillagerEntity getFlayableVillager(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx){
        NbtCompound entNbt = stack.getSubNbt(STORED_VILLAGER);
        Optional<Entity> maybeEnt = EntityType.getEntityFromNbt(entNbt, ctx.getWorld());
        if(maybeEnt.orElse(null) instanceof VillagerEntity villager){
            return villager;
        }
        return null;
    }

    // do whatever, probably just decrement the stack count or clear some nbt
    public void handleBrainsweep(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx){
        HexGloop.logPrint("brainsweep !");
    }
}
