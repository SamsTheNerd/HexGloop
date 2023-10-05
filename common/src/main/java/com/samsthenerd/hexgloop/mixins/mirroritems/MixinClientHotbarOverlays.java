package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.casting.mirror.IShallowMirrorBinder;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;
import com.samsthenerd.hexgloop.items.ItemHandMirror;
import com.samsthenerd.hexgloop.renderers.HUDOverlay;
import com.samsthenerd.hexgloop.utils.ClientUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtDouble;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinClientHotbarOverlays {

    // seems like the seed that gets passed to renderHotbarItem is 1-9 for hotbar slots and 10 for the offhand ?
    private boolean isOffhand(int slot){
        return slot == 10; 
    }

    private boolean isSelected(int slot, PlayerEntity player){
        return player.getInventory().selectedSlot == slot-1;
    }

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method="renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
    at=@At("RETURN"))
    private void renderHotbarOverlay(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed,
    CallbackInfo ci){
        // diagonstics:
        if(stack.getItem() instanceof ItemHandMirror mirrorItem){
            if(!mirrorItem.isMirrorActivated(stack) || !ClientUtils.shouldShowReflected()) return;
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(-3.0, -3.0, 0);
            RenderSystem.applyModelViewMatrix();
            if(isSelected(seed, player)){ // selected
                HUDOverlay.SELECTED_HAND_MIRROR_INDICATOR.render(x, y, 22, 22, tickDelta);
            } else { // not selected
                HUDOverlay.HAND_MIRROR_INDICATOR.render(x, y, 22, 22, tickDelta);
            }
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        } else if(stack.isEmpty() // only render if theres nothing in the hand
            && (isOffhand(seed) // render in offhand fine
            || (isSelected(seed, player) && !player.getOffHandStack().isEmpty())) // render in mainhand if offhand has an item
            && ((Object)player) instanceof IShallowMirrorBinder shallowBinder) // safe cast to get the item
        { 
            ItemStack trackedStack = shallowBinder.getTrackedStack();
            if(trackedStack == null || trackedStack.isEmpty()) return; // don't render nothing
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(-3.0, -3.0, 0);
            RenderSystem.applyModelViewMatrix();
            if(isOffhand(seed)){
                HUDOverlay.BOUND_MIRROR_INDICATOR.render(x, y, 22, 22, tickDelta);
            } else {
                HUDOverlay.SELECTED_BOUND_MIRROR_INDICATOR.render(x, y, 22, 22, tickDelta);
            }
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            // probably want to render item while we're here too ? - just yoinked from original but without the bobbing stuff because wtf is that
            trackedStack = trackedStack.copy(); // so we don't accidentally change the nbt of the actual item
            trackedStack.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(1));
            itemRenderer.renderInGuiWithOverrides(player, trackedStack, x, y, seed);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            itemRenderer.renderGuiItemOverlay(client.textRenderer, trackedStack, x, y);
        }
    }

    // so it renders the offhand slot even if it's technically empty
    @WrapOperation(method="renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V",
    at=@At(value="INVOKE", target = "net/minecraft/item/ItemStack.isEmpty ()Z"))
    private boolean plsRenderOffhand(ItemStack stack, Operation<Boolean> original){
        if(ClientUtils.shouldShowReflected() && ((Object)client.player) instanceof IShallowMirrorBinder shallowBinder){
            ItemStack trackedStack = shallowBinder.getTrackedStack();
            if(trackedStack != null && !trackedStack.isEmpty()){
                return false;
            }
        }
        return original.call(stack);
    }


    @WrapOperation(method="renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/item/ItemRenderer.renderInGuiWithOverrides (Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    private void renderReflectedItem(ItemRenderer thisItemRenderer, LivingEntity entity, ItemStack stack, int x, int y, int seed, Operation<Void> original){
        ItemStack itemToRender = stack;
        if(ClientUtils.shouldShowReflected() && !stack.isEmpty() && stack.getItem() instanceof ItemHandMirror mirrorItem){
            if(mirrorItem.isMirrorActivated(stack)){
                ItemStack maybeItemToRender = mirrorItem.getMirroredItemStack(stack);
                if(maybeItemToRender != null && !maybeItemToRender.isEmpty()){
                    itemToRender = maybeItemToRender;
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(1));
                }
            }
        }
        // need to handle empty hand reflection somewhere else since this won't even be called if it's empty
        original.call(thisItemRenderer, entity, itemToRender, x, y, seed);
    }

    @WrapOperation(method="renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/item/ItemRenderer.renderGuiItemOverlay (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"))
    private void renderReflectedItemOverlay(ItemRenderer thisItemRenderer, TextRenderer thisTextRenderer, ItemStack stack, int x, int y, Operation<Void> original){
        ItemStack itemToRender = stack;
        if(ClientUtils.shouldShowReflected() && !stack.isEmpty() && stack.getItem() instanceof ItemHandMirror mirrorItem){
            if(mirrorItem.isMirrorActivated(stack)){
                ItemStack maybeItemToRender = mirrorItem.getMirroredItemStack(stack);
                if(maybeItemToRender != null && !maybeItemToRender.isEmpty()){
                    itemToRender = maybeItemToRender;
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(1));
                }
            }
        }
        original.call(thisItemRenderer, thisTextRenderer, itemToRender, x, y);
    }

}
