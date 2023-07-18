package com.samsthenerd.hexgloop.mixins.textpatterns;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.samsthenerd.hexgloop.screens.PatternStyle;
import com.samsthenerd.hexgloop.utils.StringsToDirMap;

import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

@Mixin(TextVisitFactory.class)
public class MixinParsePatternFormatting {

    // want to mixin to start of the loop in visitFormatted
    
    @WrapOperation(method="visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
    at=@At(value="INVOKE", target="net/minecraft/client/font/TextVisitFactory.visitRegularCharacter (Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;IC)Z"))
    private static boolean parsePatternFormatting(Style style, CharacterVisitor visitor, int index, char c, Operation<Boolean> operation, @Local(ordinal=2) LocalIntRef jref, @Local(ordinal=0) String text){
        int startishIndex = jref.get(); // where we entered the loop
        int j = jref.get();
        if(j < text.length() && text.charAt(j) == '<'){ 
            if(j > 0 && text.charAt(j-1) == '\\'){ // escaped
                jref.set(startishIndex);
                return operation.call(style, visitor, index, c);
            }
            j++; // skip <
            // want to do pattern matching
            while(j < text.length() && Character.isWhitespace(text.charAt(j))){ // skip whitespace
                j++;
            }
            int startDirIndex = j;
            // try to find direction:
            while(j < text.length() && text.charAt(j) != '>' && text.charAt(j) != ','){
                j++;
            }
            if(j == text.length() || text.charAt(j) == '>'){ // no direction
                // HexGloop.logPrint("no direction");
                jref.set(startishIndex);
                return operation.call(style, visitor, index, c);
            }
            String dirString = text.substring(startDirIndex, j).toLowerCase().strip().replace("_", "");
            HexDir dir = StringsToDirMap.dirMap.get(dirString);
            if(dir == null){
                // HexGloop.logPrint("invalid direction: " + dirString);
                jref.set(startishIndex);
                return operation.call(style, visitor, index, c);
            }
            // have direction now
            j++; // skip comma
            int startPatternIndex = j;
            char ch = '\0';
            while(j < text.length() && (text.charAt(j) != '>')){ // run to end
                ch = text.charAt(j);
                if(ch != 'a' && ch != 'A' && ch != 'q' && ch != 'Q' && ch != 'w' && ch != 'W'
                && ch != 'e' && ch != 'E' && ch != 's' && ch != 'S' && ch != 'd' && ch != 'D' && ch != ' '){
                    // HexGloop.logPrint("found invalid char: " + ch);
                    jref.set(startishIndex);
                    return operation.call(style, visitor, index, c);
                }
                j++;
            }
            if(j == text.length() && ch != '>'){ // no closing bracket
                // HexGloop.logPrint("no closing bracket");
                jref.set(startishIndex);
                return operation.call(style, visitor, index, c);
            }
            // have pattern now
            String angleSigs = text.substring(startPatternIndex, j).toLowerCase().strip().replace(" ", "");
            HexPattern pattern = parsePattern(angleSigs, dir);
            if(pattern == null) return operation.call(style, visitor, index, c);
            // poggers we have everything
            visitor.accept(startishIndex, ((PatternStyle)style).withPattern(pattern), '!');
            jref.set(j);
            return j < text.length(); // if there's more or not
        }
        return operation.call(style, visitor, index, c);
    }

    @Nullable
    private static HexPattern parsePattern(String angleSigs, HexDir dir){
        if(angleSigs == null || dir == null){
            return null;
        }
        HexPattern pattern = null;
        try{
            pattern = HexPattern.fromAngles(angleSigs, dir);
        } catch (IllegalStateException e) {
        }
        return pattern;
    }
}
