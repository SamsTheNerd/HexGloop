package com.samsthenerd.hexgloop.misc.wnboi;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.LabelType;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel.PatternOptions;
import com.samsthenerd.hexgloop.utils.GloopyRenderUtils;
import com.samsthenerd.wnboi.utils.RenderUtils;

import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LabelTypes {
    public static LabelType<TextLabel> TEXT_LABEL_TYPE = LabelMaker.registerLabelType(
        new LabelType<TextLabel>(new Identifier("hexgloop", "text_label"), nbt -> {
            if(nbt == null || !nbt.contains("type", NbtElement.STRING_TYPE) || !nbt.getString("type").equals("hexgloop:text_label")) return null;
            if(!nbt.contains("text", NbtElement.STRING_TYPE)) return null;
            Text text = Text.Serializer.fromJson(nbt.getString("text"));
            return new TextLabel(text);
        })
    );

    public static LabelType<ItemLabel> ITEM_LABEL_TYPE = LabelMaker.registerLabelType(
        new LabelType<ItemLabel>(new Identifier("hexgloop", "item_label"), nbt -> {
            if(nbt == null || !nbt.contains("type", NbtElement.STRING_TYPE) || !nbt.getString("type").equals("hexgloop:item_label")) return null;
            if(!nbt.contains("item", NbtElement.COMPOUND_TYPE)) return null;
            ItemStack stack = ItemStack.fromNbt(nbt.getCompound("item"));
            if(stack == null) return null;
            return new ItemLabel(stack);
        })
    );

    public static LabelType<EntityLabel> ENTITY_LABEL_TYPE = LabelMaker.registerLabelType(
        new LabelType<EntityLabel>(new Identifier("hexgloop", "entity_label"), nbt -> {
            if(nbt == null || !nbt.contains("type", NbtElement.STRING_TYPE) || !nbt.getString("type").equals("hexgloop:entity_label")) return null;
            if(!nbt.contains("entity", NbtElement.COMPOUND_TYPE)) return null;
            Entity entity;
            if(nbt.getCompound("entity").contains("player", NbtElement.STRING_TYPE)){
                UUID playerUUID = UUID.fromString(nbt.getCompound("entity").getString("player"));
                entity = new OtherClientPlayerEntity(MinecraftClient.getInstance().world, 
                    new GameProfile(playerUUID, null), null);
            } else {
                EntityType<?> entType = EntityType.fromNbt(nbt.getCompound("entity")).orElse(null);
                if(entType == null) return null;
                entity = EntityType.getEntityFromNbt(nbt.getCompound("entity"), MinecraftClient.getInstance().world).orElse(null);
            }
            if(entity == null) return null;
            entity.setBodyYaw(215);
            entity.setHeadYaw(215);
            return new EntityLabel(entity);
        })
    );

    public static LabelType<PatternLabel> PATTERN_LABEL_TYPE = LabelMaker.registerLabelType(
        new LabelType<PatternLabel>(new Identifier("hexgloop", "pattern_label"), nbt -> {
            if(nbt == null || !nbt.contains("type", NbtElement.STRING_TYPE) || !nbt.getString("type").equals("hexgloop:pattern_label")) return null;
            if(!nbt.contains("pattern", NbtElement.COMPOUND_TYPE)) return null;
            HexPattern pattern = HexPattern.fromNBT(nbt.getCompound("pattern"));
            if(pattern == null) return null;
            if(nbt.contains("options", NbtElement.COMPOUND_TYPE)){
                return new PatternLabel(pattern, PatternOptions.fromNbt(nbt.getCompound("options")));
            }
            return new PatternLabel(pattern);
        })
    );
    

    public static class TextLabel implements Label{
        public Text text;

        public TextLabel(Text text){
            this.text = text;
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height){
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> lines = textRenderer.wrapLines(text, width);
            int totalHeight = textRenderer.fontHeight * lines.size();
            int yOffset = (height - totalHeight) / 2;
            int maxWidth = 0;
            for(OrderedText line: lines){
                maxWidth = Math.max(textRenderer.getWidth(line), maxWidth);
            }
            int xOffset = (width - maxWidth) / 2;
            for(OrderedText line : lines){
                textRenderer.draw(matrices, line, x + xOffset - width/2, y + yOffset - height /2 + textRenderer.fontHeight/2, 0xFFFFFF);
                yOffset += textRenderer.fontHeight;
            }
        }

        @Override
        public LabelType<?> getType(){
            return TEXT_LABEL_TYPE;
        }

        @Override
        public NbtCompound toNbt(){
            NbtCompound nbt = Label.super.toNbt();
            nbt.putString("text", Text.Serializer.toJson(text));
            return nbt;
        }
    }

    public static class ItemLabel implements Label{
        public ItemStack stack;

        public ItemLabel(ItemStack stack){
            this.stack = stack;
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height){
            int size = Math.min(width, height);
            GloopyRenderUtils.renderGuiItemIcon(stack, x-size/2, y-size/2, size);
        }

        @Override
        public LabelType<?> getType(){
            return ITEM_LABEL_TYPE;
        }

        @Override
        public NbtCompound toNbt(){
            NbtCompound nbt = Label.super.toNbt();
            nbt.put("item", stack.writeNbt(new NbtCompound()));
            return nbt;
        }
    }

    public static class EntityLabel implements Label{
        public Entity entity;

        public EntityLabel(Entity entity){
            this.entity = entity;
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height){
            int size = (int)Math.min(width/entity.getWidth(), height/entity.getHeight());
            RenderUtils.drawEntity(x, y+height/2, size, entity);
        }

        @Override
        public LabelType<?> getType(){
            return ENTITY_LABEL_TYPE;
        }

        @Override
        public NbtCompound toNbt(){
            NbtCompound nbt = Label.super.toNbt();
            NbtCompound entNbt = new NbtCompound();
            if(entity instanceof PlayerEntity player){
                entNbt.putString("player", player.getUuidAsString());
            } else {
                entity.saveSelfNbt(entNbt);
            }
            nbt.put("entity", entNbt);
            return nbt;
        }
    }

    public static class PatternLabel implements Label{
        public HexPattern pattern;
        public PatternOptions options;

        public PatternLabel(HexPattern pattern, PatternOptions options){
            this.pattern = pattern;
            this.options = options;
        }

        public PatternLabel(HexPattern pattern){
            this(pattern, new PatternOptions());
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height){
            GloopyRenderUtils.drawPattern(matrices, pattern, width, height, x, y, 
                options.outerColor == null ? 0xFF_FFFFFF : options.outerColor.intValue(),
                options.innerColorMain == null ? 0xFF_FFFFFF : options.innerColorMain.intValue(),
                options.innerColorAccent == null ? 0xFF_FFFFFF : options.innerColorAccent.intValue(),
                options.dotColor == null ? 0xFF_FFFFFF : options.dotColor.intValue(),
                options.speed == null ? 0 : options.speed.floatValue(),
                options.variance == null ? 0 : options.variance.floatValue(),
                options.hasStartingPoint == null ? false : options.hasStartingPoint.booleanValue()
            );
        }

        @Override
        public LabelType<?> getType(){
            return PATTERN_LABEL_TYPE;
        }

        @Override
        public NbtCompound toNbt(){
            NbtCompound nbt = Label.super.toNbt();
            nbt.put("pattern", pattern.serializeToNBT());
            nbt.put("options", options.toNbt());
            return nbt;
        }

        // lots of options ! most prob won't be accessible in game but i might use them for fidgets
        public static class PatternOptions{
            public Integer outerColor; public Integer innerColorMain; public Integer innerColorAccent;
            public Float speed; public Float variance; public Boolean hasStartingPoint; public Integer dotColor;

            public PatternOptions(Integer outerColor, Integer innerColorMain, Integer innerColorAccent, Integer dotColor, Float speed, Float variance, Boolean hasStartingPoint){
                this.outerColor = outerColor;
                this.innerColorMain = innerColorMain;
                this.innerColorAccent = innerColorAccent;
                this.speed = speed;
                this.variance = variance;
                this.hasStartingPoint = hasStartingPoint;
                this.dotColor = dotColor;
            }

            public PatternOptions(){
                this(null, null, null, null, null, null, null);
            }

            public static PatternOptions fromNbt(NbtCompound nbt){
                PatternOptions options = new PatternOptions();
                if(nbt.contains("outerColor", NbtElement.INT_TYPE)) options.outerColor = nbt.getInt("outerColor");
                if(nbt.contains("innerColorMain", NbtElement.INT_TYPE)) options.innerColorMain = nbt.getInt("innerColorMain");
                if(nbt.contains("innerColorAccent", NbtElement.INT_TYPE)) options.innerColorAccent = nbt.getInt("innerColorAccent");
                if(nbt.contains("dotColor", NbtElement.INT_TYPE)) options.dotColor = nbt.getInt("dotColor");
                if(nbt.contains("speed", NbtElement.FLOAT_TYPE)) options.speed = nbt.getFloat("speed");
                if(nbt.contains("variance", NbtElement.FLOAT_TYPE)) options.variance = nbt.getFloat("variance");
                if(nbt.contains("hasStartingPoint", NbtElement.BYTE_TYPE)) options.hasStartingPoint = nbt.getBoolean("hasStartingPoint");
                return options;
            }

            public NbtCompound toNbt(){
                NbtCompound nbt = new NbtCompound();
                if(outerColor != null) nbt.putInt("outerColor", outerColor);
                if(innerColorMain != null) nbt.putInt("innerColorMain", innerColorMain);
                if(innerColorAccent != null) nbt.putInt("innerColorAccent", innerColorAccent);
                if(dotColor != null) nbt.putInt("dotColor", dotColor);
                if(speed != null) nbt.putFloat("speed", speed);
                if(variance != null) nbt.putFloat("variance", variance);
                if(hasStartingPoint != null) nbt.putBoolean("hasStartingPoint", hasStartingPoint);
                return nbt;
            }
        }
    }

    // now the iota transforming functions

    public static void registerIotaLabelFunctions(){
        LabelMaker.registerIotaLabelFunction(DoubleIota.TYPE, LabelTypes::doubleToTextLabel);
        LabelMaker.registerIotaLabelFunction(PatternIota.TYPE, LabelTypes::patternToLabel);
        LabelMaker.registerIotaLabelFunction(EntityIota.TYPE, LabelTypes::entityToLabel);
    }

    @Nullable
    public static TextLabel doubleToTextLabel(Iota iota){
        if(!(iota instanceof DoubleIota)) return null;
        DoubleIota di = (DoubleIota) iota;
        Text text = Text.literal(Double.toString(di.getDouble()));
        return new TextLabel(text);
    }

    @Nullable
    public static PatternLabel patternToLabel(Iota iota){
        if(!(iota instanceof PatternIota)) return null;
        PatternIota patIota = (PatternIota) iota;
        HexPattern pattern = patIota.getPattern();
        if(pattern == null) return null;
        return new PatternLabel(pattern);
    }

    @Nullable
    public static Label entityToLabel(Iota iota){
        if(!(iota instanceof EntityIota)) return null;
        EntityIota entityIota = (EntityIota) iota;
        Entity entity = entityIota.getEntity();
        if(entity == null) return null;
        if(entity instanceof ItemEntity itemEnt){
            ItemStack stack = itemEnt.getStack();
            return new ItemLabel(stack);
        } else {
            return new EntityLabel(entity);
        }
    }
}
