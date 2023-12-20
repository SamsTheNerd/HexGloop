package com.samsthenerd.hexgloop.mixins.booktweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

@Mixin(LecternBlockEntityRenderer.class)
public class MixinClientRetextureLecternBook {

    private static Map<Identifier, SpriteIdentifier> bookTextures = new HashMap<>();

    @ModifyExpressionValue(
        method="render(Lnet/minecraft/block/entity/LecternBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
        at=@At(value="FIELD", target="net/minecraft/client/render/block/entity/EnchantingTableBlockEntityRenderer.BOOK_TEXTURE : Lnet/minecraft/client/util/SpriteIdentifier;" )
    )    
    public SpriteIdentifier retextureLecternBook(SpriteIdentifier original, LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j){
        ItemStack lecternBook = lecternBlockEntity.getBook();
        HexGloop.logPrint("lectern has book: " + lecternBook);
        Book patchiBook = ItemStackUtil.getBookFromStack(lecternBook);
        if(patchiBook != null){
            HexGloop.logPrint("got a patchi book");
            if(bookTextures.containsKey(patchiBook.id)){
                return Objects.requireNonNullElse(bookTextures.get(patchiBook.id), original);
            }
            Identifier textureId = new Identifier(patchiBook.id.getNamespace(), "entity/lectern_" + patchiBook.id.getPath());
            Identifier resourceId = new Identifier(textureId.getNamespace(), "textures/" + textureId.getPath() + ".png");
            Optional<Resource> maybeTexture = MinecraftClient.getInstance().getResourceManager().getResource(resourceId);
            if(maybeTexture.isEmpty()){
                HexGloop.logPrint("couldn't find texture for " + patchiBook.id);
                bookTextures.put(patchiBook.id, null);
                return original;
            }
            HexGloop.logPrint("putting texture for " + patchiBook.id + " in cache");
            SpriteIdentifier foundSpriteID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, textureId);
            bookTextures.put(patchiBook.id, foundSpriteID);
            return foundSpriteID;
        }
        return original;
    }
}
