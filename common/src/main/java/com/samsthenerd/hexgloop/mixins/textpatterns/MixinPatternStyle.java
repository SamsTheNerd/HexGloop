package com.samsthenerd.hexgloop.mixins.textpatterns;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.client.RenderLib;
import at.petrak.hexcasting.common.lib.HexItems;
import kotlin.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec2f;


// heavy influence/mild copying from https://github.com/Snownee/TextAnimator/blob/1.19.2-fabric/src/main/java/snownee/textanimator/mixin/StyleMixin.java
// note that all of style's style.withProperty methods won't preserve the pattern
@Mixin(Style.class)
public class MixinPatternStyle implements PatternStyle{

    private HexPattern pattern = null;
    private List<Vec2f> zappyPoints = null;
    private List<Vec2f> pathfinderDots = null;
    private float patScale; // maybe want to have this exposed somewhere?

    private static final String PATTERN_KEY = "hexPatternStyle";
    private static final String PATTERN_START_DIR_KEY = "startDir";
    private static final String PATTERN_ANGLE_SIG_KEY = "angleSig";
    private static final float RENDER_SIZE = 128f;

    @Override
    public HexPattern getPattern() {
        return pattern;
    }

    @Override
    public Style setPattern(HexPattern pattern) {
        // yoinked from PatternTooltipComponent
        this.pattern = pattern;
        Pair<Float, List<Vec2f> > pair = RenderLib.getCenteredPattern(pattern, RENDER_SIZE, RENDER_SIZE, 16f);
        this.patScale = pair.getFirst();
        List<Vec2f> dots = pair.getSecond();
        this.zappyPoints = RenderLib.makeZappy(
            dots, RenderLib.findDupIndices(pattern.positions()),
            10, 0.8f, 0f, 0f, RenderLib.DEFAULT_READABILITY_OFFSET, RenderLib.DEFAULT_LAST_SEGMENT_LEN_PROP,
            0.0);
        this.pathfinderDots = dots;
        return (Style)(Object)this;
    }

    @Override
    public Style withPattern(HexPattern pattern, boolean withPatternHoverEvent, boolean withPatternClickEvent) {
        Style style = (Style)(Object)this;
        if (withPatternHoverEvent) {
            StringBuilder bob = new StringBuilder();
            bob.append(pattern.getStartDir());

            var sig = pattern.anglesSignature();
            if (!sig.isEmpty()) {
                bob.append(" ");
                bob.append(sig);
            }
            Text hoverText = Text.translatable("hexcasting.tooltip.pattern_iota",
                Text.literal(bob.toString()).formatted(Formatting.WHITE));
            ItemStack scrollStack = new ItemStack(HexItems.SCROLL_LARGE);
            scrollStack.setCustomName(hoverText);
            HexItems.SCROLL_LARGE.writeDatum(scrollStack, new PatternIota(pattern));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(scrollStack)));
        }
        return style.withParent(PatternStyle.fromPattern(pattern));
    }

    @Override
    public List<Vec2f> getZappyPoints(){
        return zappyPoints;
    }

    @Override
    public List<Vec2f> getPathfinderDots(){
        return pathfinderDots;
    }

    @Inject(at=@At("TAIL"), method="<init>(Lnet/minecraft/text/TextColor;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/text/ClickEvent;Lnet/minecraft/text/HoverEvent;Ljava/lang/String;Lnet/minecraft/util/Identifier;)V")
    private void HexPatDefaultStyleConstructor(@Nullable TextColor color, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String insertion, @Nullable Identifier font, CallbackInfo cinfo){
        this.pattern = null;
        this.zappyPoints = null;
        this.pathfinderDots = null;
        this.patScale = 1f;
    }

    @Inject(method = "withParent", at = @At("RETURN"), cancellable = true)
	private void HexPatStyWithParent(Style parent, CallbackInfoReturnable<Style> cir) {
        Style rstyle = cir.getReturnValue();
        if(this.getPattern() != null){
            ((PatternStyle) rstyle).setPattern(this.getPattern());
        } else { // no pattern on this style, try falling back to inherit parent
            HexPattern parentPattern = ((PatternStyle) parent).getPattern();
            if(parentPattern != null){
                ((PatternStyle) rstyle).setPattern(parentPattern);
            }
        }
		cir.setReturnValue(rstyle);
	}

	@Inject(method = "equals", at = @At("HEAD"), cancellable = true)
	private void HexPatStyEquals(Object obj, CallbackInfoReturnable<Boolean> cir) {
		if (this != obj && (obj instanceof PatternStyle style)) {
			if (!Objects.equals(this.getPattern(), style.getPattern())) {
				cir.setReturnValue(false);
			}
		}
	}

	@Mixin(Style.Serializer.class)
	public static class MixinPatternStyleSerializer {
		@Inject(method = "deserialize", at = @At("RETURN"))
		private void HexPatStyDeserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Style> cir) {
			if (!jsonElement.isJsonObject() || cir.getReturnValue() == null) {
				return;
			}
			JsonObject json = jsonElement.getAsJsonObject();
			if (!json.has("hexPatternStyle")) {
				return;
			}
            JsonObject patternObj = JsonHelper.getObject(json, PATTERN_KEY);
            String startDirString = JsonHelper.hasString(patternObj, PATTERN_START_DIR_KEY) ? JsonHelper.getString(patternObj, PATTERN_START_DIR_KEY) : null;
            String angleSigString = JsonHelper.hasString(patternObj, PATTERN_ANGLE_SIG_KEY) ? JsonHelper.getString(patternObj, PATTERN_ANGLE_SIG_KEY) : null;
            
            if(startDirString == null || angleSigString == null) return;

            HexDir startDir = HexDir.fromString(startDirString);
            HexPattern pattern = HexPattern.fromAngles(angleSigString, startDir);
            ((PatternStyle) cir.getReturnValue()).setPattern(pattern);
		}

		@Inject(method = "serialize", at = @At("RETURN"))
		private void HexPatStySerialize(Style style, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> cir) {
			JsonElement jsonElement = cir.getReturnValue();

			PatternStyle pStyle = (PatternStyle) style;
			if (jsonElement == null || !jsonElement.isJsonObject() || pStyle.getPattern() == null) {
				return;
			}
			JsonObject json = jsonElement.getAsJsonObject();
            JsonObject patternObj = new JsonObject();
            patternObj.addProperty(PATTERN_START_DIR_KEY, pStyle.getPattern().getStartDir().toString());
            patternObj.addProperty(PATTERN_ANGLE_SIG_KEY, pStyle.getPattern().anglesSignature());
			json.add(PATTERN_KEY, patternObj);
		}
	}
}
