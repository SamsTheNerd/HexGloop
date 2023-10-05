package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.network.HexGloopNetwork;

import at.petrak.hexcasting.common.entities.EntityWallScroll;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

@Mixin(ItemModBook.class)
public class MixinCopyPatternToScrolls {
    @Inject(method="use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
    at=@At("HEAD"), cancellable = true)
    public void handleClickOnScroll(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        ItemStack stack = player.getStackInHand(hand);
		Book book = ItemModBook.getBook(stack);
        if(book == null){
            cir.setReturnValue(new TypedActionResult<>(ActionResult.FAIL, stack));
            return;
        }
        if(player instanceof ServerPlayerEntity sPlayer){
            if(book.id.toString().equals("hexcasting:thehexbook")){
                if(isLookingAtScroll(player)){
                    // HexGloop.logPrint("looking at scroll maybe ?");
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeBoolean(hand == Hand.MAIN_HAND);
                    NetworkManager.sendToPlayer(sPlayer, HexGloopNetwork.PROMPT_REPLACE_SCROLL_ID, buf);
                    cir.setReturnValue(new TypedActionResult<>(ActionResult.SUCCESS, stack));
                } else {
                    // HexGloop.logPrint("not looking at scroll");
                }
            }
        }
    }

    private boolean isLookingAtScroll(PlayerEntity player){
        Vec3d origin = player.getEyePos();
        Vec3d endPos = origin.add(player.getRotationVector().normalize().multiply(5));
        // just yoink this bit from hexcasting entity raycast
        EntityHitResult entResult = ProjectileUtil.raycast(player, origin, endPos, new Box(origin, endPos), (ent) -> {
            return ent instanceof EntityWallScroll;
        }, 1000000);
        if(entResult == null || entResult.getEntity() == null) return false;
        HitResult result = player.raycast(5, 0, false);
        // if(result.getType() == HitResult.Type.ENTITY){
        //     EntityHitResult entResult = (EntityHitResult) result;
        //     if(entResult.getEntity() instanceof EntityWallScroll){
        //         return true;
        //     }
        // }
        if(result.getType() == HitResult.Type.MISS || result.squaredDistanceTo(player) > entResult.squaredDistanceTo(player)){
            return true;
        }
        return false;
    }
}
