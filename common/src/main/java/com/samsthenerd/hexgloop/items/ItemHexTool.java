package com.samsthenerd.hexgloop.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

// come back to this
public abstract class ItemHexTool extends ItemPackagedHex implements IExtendedEnchantable {
    public static final Identifier TOOL_STATUS_PREDICATE = new Identifier(HexGloop.MOD_ID, "hex_tool_status");
    private final float attackDamage;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    // note that that's base stats, so like,, when broken
    public ItemHexTool(Settings pProperties, float attackDamage, float useSpeed) {
        super(pProperties);
        this.attackDamage = attackDamage;
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)useSpeed, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
    }

    public boolean hasMediaToUse(ItemStack stack){
        return getMedia(stack) > 0;
    }

    public boolean canDrawMediaFromInventory(ItemStack stack){
        return false; // i feel like no,, but the design is artifact-ish,, so idk
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    public boolean breakAfterDepletion(){
        return false;
    }

    public int getEnchantability() {
		return 25;
	}

    public boolean isEnchantable(ItemStack stack) {
		return true;
	}

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

    // for hexxycraft backport
    public int cooldown(){
        return 0;
    }
}
