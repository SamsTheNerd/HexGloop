package com.samsthenerd.hexgloop.casting.tags;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.samsthenerd.hexgloop.compat.hexal.HexalMaybeIotas;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import dev.architectury.platform.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class TagUtils {
    
    public static TagChecker getTagChecker(Iota iota, @Nullable CastingContext ctx){
        if(Platform.isModLoaded("hexal")){
            Either<Item, Block> blockOrItem = HexalMaybeIotas.getBlockOrItemFromIota(iota);
            if(blockOrItem != null){
                return new ItemOrBlockTagChecker(blockOrItem);
            }
        }
        if(iota instanceof EntityIota entityIota){
            return new EntityTagChecker(entityIota.getEntity());
        }
        if(iota instanceof Vec3Iota vecIota && ctx != null){
            return new ItemOrBlockTagChecker(Either.right(ctx.getWorld().getBlockState(new BlockPos(vecIota.getVec3())).getBlock()));
        }

        return new EmptyTagChecker();
    }

    public static interface TagChecker {
        public boolean hasTag(String tag);

        public Set<String> getTags();
    }

    public static class EmptyTagChecker implements TagChecker{
        public boolean hasTag(String tag){
            return false;
        }

        public Set<String> getTags(){
            return new HashSet<>();
        }
    }

    public static class ItemOrBlockTagChecker implements TagChecker{

        Either<Item, Block> blockOrItem;

        public ItemOrBlockTagChecker(Either<Item, Block> blockOrItem){
            this.blockOrItem = blockOrItem;
        }

        private Block asBlock(){
            if(blockOrItem.right().isPresent()){
                return blockOrItem.right().get();
            }
            if(blockOrItem.left().isPresent() && blockOrItem.left().get() instanceof BlockItem blockItem){
                return blockItem.getBlock();
            }
            return Blocks.AIR;
        }

        private Item asItem(){
            if(blockOrItem.left().isPresent()){
                return blockOrItem.left().get();
            }
            if(blockOrItem.right().isPresent()){
                return blockOrItem.right().get().asItem();
            }
            return Items.AIR;
        }

        public boolean hasTag(String tag){
            // Optional<RegistryEntry<Block>> blockEntry = Registry.BLOCK.getKey(asBlock()).map(key -> Registry.BLOCK.getEntry(key))
            if(asBlock().getRegistryEntry().isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier(tag)))){
                return true;
            }
            if(asItem().getRegistryEntry().isIn(TagKey.of(Registry.ITEM_KEY, new Identifier(tag)))){
                return true;
            }
            return false;
        }

        public Set<String> getTags(){
            Set<String> tags = new HashSet<>();
            asBlock().getRegistryEntry().streamTags().forEach(tag -> {
                tags.add(tag.id().toTranslationKey());
            });
            asItem().getRegistryEntry().streamTags().forEach(tag -> {
                tags.add(tag.id().toTranslationKey());
            });
            return tags;
        }
    }

    public static class EntityTagChecker implements TagChecker{

        Entity entity;

        public EntityTagChecker(Entity entity){
            this.entity = entity;
        }

        public ItemStack getItemStack(){
            if(entity instanceof ItemEntity itemEntity){
                return itemEntity.getStack();
            }
            if(entity instanceof ItemFrameEntity itemFrameEntity){
                return itemFrameEntity.getHeldItemStack();
            }
            return ItemStack.EMPTY;
        }

        public boolean hasTag(String tag){
            if(entity.getType().getRegistryEntry().isIn(TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(tag)))){
                return true;
            }
            ItemStack stack = getItemStack();
            if(!stack.isEmpty()){
                return new ItemOrBlockTagChecker(Either.left(stack.getItem())).hasTag(tag);
            }
            return false;
        }

        public Set<String> getTags(){
            Set<String> tags = new HashSet<>();
            entity.getType().getRegistryEntry().streamTags().forEach(tag -> {
                tags.add(tag.id().toTranslationKey());
            });
            ItemStack stack = getItemStack();
            if(!stack.isEmpty()){
                tags.addAll(new ItemOrBlockTagChecker(Either.left(stack.getItem())).getTags());
            }
            return tags;
        }
    }

}
