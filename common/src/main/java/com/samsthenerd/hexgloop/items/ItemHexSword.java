package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHexSword extends ItemHexTool implements IExtendedEnchantable.IWeaponEnchantable{

    public ItemHexSword(Settings pProperties) {
        super(pProperties, 2.0f, 3f);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(hasMediaToUse(stack)){
            withdrawMedia(stack, Math.min(1*MediaConstants.DUST_UNIT, getMedia(stack)), false);
            // should probably also do extra damage here ? - i don't think it'll stack with the other damage because of iframes or whatever ?
            // hm, might be neat if it scaled to do more damage for more media, but idk if that should be based on max media size or just the amount ?
            float enchantmentDamage = EnchantmentHelper.getAttackDamage(stack, target.getGroup());
            target.damage(DamageSource.MAGIC, 8.0F + enchantmentDamage);
            return true;
        }
        return false;
	}

	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if(hasMediaToUse(stack)){
            if (state.getHardness(world, pos) != 0.0F) {
                withdrawMedia(stack, Math.min(2*MediaConstants.DUST_UNIT, getMedia(stack)), false);
                // not sure if it needs a break sound or whatever ?
            }
            return true;
        }
        return false;
	}

	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}
}
