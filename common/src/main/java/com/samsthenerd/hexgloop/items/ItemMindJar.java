package com.samsthenerd.hexgloop.items;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

public class ItemMindJar extends Item implements IFlayableItem{

    public ItemMindJar(Settings settings) {
        super(settings);
    }

    @Override
    public VillagerEntity getFlayableVillager(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx){
        NbtCompound entNbt = stack.getSubNbt(IMindTargetItem.STORED_MIND_TAG);
        Optional<Entity> maybeEnt = EntityType.getEntityFromNbt(entNbt, ctx.getWorld());
        if(maybeEnt.orElse(null) instanceof VillagerEntity villager){
            VillagerEntity clone = new VillagerEntity(EntityType.VILLAGER, ctx.getWorld());
            clone.setVillagerData(villager.getVillagerData());
            if(itemEnt != null){
                clone.setPos(itemEnt.getX(), itemEnt.getY(), itemEnt.getZ());
            } else {
                clone.setPos(ctx.getPosition().getX(), ctx.getPosition().getY(), ctx.getPosition().getZ());
            }
            return clone;
        }
        return null;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            // just don't put it in there for now !
            // stacks.add(new ItemStack(this));
        }
    }

    @Override
    public void handleBrainsweep(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx, Consumer<ItemStack> resultConsumer){
        stack.decrement(1);
        resultConsumer.accept(new ItemStack(HexGloopItems.EMPTY_JAR_ITEM.get()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(world != null && stack.hasNbt() && stack.getNbt().contains(IMindTargetItem.STORED_MIND_TAG)){
            NbtCompound entNbt = stack.getSubNbt(IMindTargetItem.STORED_MIND_TAG);
            Optional<Entity> maybeEnt = EntityType.getEntityFromNbt(entNbt, world);
            if(maybeEnt.orElse(null) instanceof VillagerEntity villager){
                VillagerData vData = villager.getVillagerData();
                if(vData.getProfession() == VillagerProfession.NONE){
                    tooltip.add(Text.translatable("item.hexgloop.mind_jar.tooltip.nojob"));
                } else {
                    Text jobLevel = Text.translatable("merchant.level." + vData.getLevel());
                    Text job = Text.translatable("entity.minecraft.villager." + vData.getProfession());
                    Text full = Text.translatable("item.hexgloop.mind_jar.tooltip", jobLevel, job);
                    tooltip.add(full);
                }
                return;
            }
        }
        tooltip.add(Text.translatable("item.hexgloop.mind_jar.tooltip.empty"));
    }
}
