package com.samsthenerd.hexgloop.mixins.textpatterns;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

@Mixin(TextVisitFactory.class)
public class MixinParsePatternFormatting {

    private static final Map<String, HexDir> dirMap = new HashMap<String, HexDir>();

    {
        dirMap.put("northwest", HexDir.NORTH_WEST);
        dirMap.put("west", HexDir.WEST);
        dirMap.put("southwest", HexDir.SOUTH_WEST);
        dirMap.put("southeast", HexDir.SOUTH_EAST);
        dirMap.put("east", HexDir.EAST);
        dirMap.put("northeast", HexDir.NORTH_EAST);
        dirMap.put("nw", HexDir.NORTH_WEST);

        dirMap.put("w", HexDir.WEST);
        dirMap.put("sw", HexDir.SOUTH_WEST);
        dirMap.put("se", HexDir.SOUTH_EAST);
        dirMap.put("e", HexDir.EAST);
        dirMap.put("ne", HexDir.NORTH_EAST);
    }

    public static boolean skipBrace = false;

    /** ideas before sleep
     * use something static to store the style maybe?
     * maybe same thing for skipping '>' 
     * mixin and make charAt(outOfBounds) return null and then null check everything else to prevent out of bounds issues
     */

    // want to mixin to start of the loop in visitFormatted
    @ModifyVariable(method="visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
    at=@At(value="INVOKE", target="java/lang/String.charAt (I)C", ordinal = 0), 
    name="j")
    public static int parsePatternFormatting(int j, String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor){
        skipBrace = false; // reset skipBrace
        int startishIndex = j; // where we entered the loop
        if(j < text.length() && text.charAt(j) == '<'){ 
            if(j > 0 && text.charAt(j-1) == '\\'){ // escaped
                return startishIndex;
            }
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
                return startishIndex;
            }
            String dirString = text.substring(startDirIndex, j).toLowerCase().strip().replace("_", "");
            HexDir dir = dirMap.get(dirString);
            if(dir == null){
                return startishIndex;
            }
            // have direction now
            j++; // skip comma
            int startPatternIndex = j;
            while(j < text.length() && (text.charAt(j) != '>')){ // run to end
                char c = text.charAt(j);
                if(c != 'a' && c != 'A' && c != 'q' && c != 'Q' && c != 'w' && c != 'W'
                && c != 'e' && c != 'E' && c != 's' && c != 'S' && c != 'd' && c != 'D' && c != ' '){
                    return startishIndex;
                }
                j++;
            }
            if(j == text.length()){ // no closing bracket
                return startishIndex;
            }
            // have pattern now
            String angleSigs = text.substring(startPatternIndex, j).toLowerCase().strip().replace(" ", "");
            HexPattern pattern = HexPattern.fromAngles(angleSigs, dir);
            // poggers we have everything
            visitor.accept(j, ((PatternStyle)startingStyle).withPattern(pattern), '!');
            skipBrace = true;
        }
        return j;
    }

    // now do all our redirects down here

    @Redirect(method="visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
    at=@At(value="INVOKE", target="java/lang/String.charAt (I)C"))
    public char HexPatBoundsCheckCharAt(String text, int index){
        if(index >= text.length()){
            return 24; // just something that probably wouldn't otherwise be there?
        }
        return text.charAt(index);
    }
    
    @Redirect(method="visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
    at=@At(value="INVOKE", target="java/lang/Character.isHighSurrogate (C)Z"))
    public static boolean HexPatBoundsCheckIsHighSurrogate(char c){
        if(skipBrace || c == 24) return false;
        return Character.isHighSurrogate(c);
    }

    @Redirect(method="visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
    at=@At(value="INVOKE", target="net/minecraft/client/font/TextVisitFactory.visitRegularCharacter (Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;IC)Z"))
    public static boolean HexPatBoundsCheckVisitRegular(Style style, CharacterVisitor visitor, int index, char c){
        if(skipBrace) return true; // we want to keep going 
        if(c == 24) return false; // we want to not keep going
        return TextVisitFactory.visitRegularCharacter(style, visitor, index, c);
    }
}
