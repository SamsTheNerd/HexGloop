package com.samsthenerd.hexgloop.casting.canvas;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.MapColor;
import net.minecraft.block.MapColor.Brightness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3i;

public class SlateCanvasUtils {
    private static Map<Vec3i, Byte> colorCache = new HashMap<Vec3i, Byte>();

    public static byte getClosestMapColor(Vec3i colorVec, @Nullable PlayerEntity player){
        if(colorCache.containsKey(colorVec)){
            return colorCache.get(colorVec);
        }
        byte closestColor = 0;
        double similarity = -1;
        for(int c = 0; c < 64; c++){
            for(int b = 0; b < 4; b++){
                MapColor mapColor = MapColor.get(c);
                Brightness brightness = Brightness.validateAndGet(b);
                int mapRgb = mapColor.getRenderColor(brightness);
                Vec3i mapColorVec = new Vec3i((mapRgb >> 16) & 0xFF, (mapRgb >> 8) & 0xFF, mapRgb & 0xFF);
                double newSimilarity = getSimpleSimilarity(colorVec, mapColorVec);
                if(similarity == -1 || newSimilarity < similarity){
                    similarity = newSimilarity;
                    closestColor = mapColor.getRenderColorByte(brightness);
                }
            }
        }
        // HexGloop.logPrint("closest color for: " + colorVec + " is " + getColorString(closestColor) + " (simple: " + getColorString(closestColorSimple) + ")");
        // int decimalColor = colorVec.getX() << 16 | colorVec.getY() << 8 | colorVec.getZ() + 0xFF000000;
        // MutableText sampleText = Text.literal("█").setStyle(Style.EMPTY.withColor(decimalColor));
        // sampleText.append(Text.literal("█").setStyle(Style.EMPTY.withColor(MapColor.getRenderColor(closestColor))));
        // sampleText.append(Text.literal("█").setStyle(Style.EMPTY.withColor(MapColor.getRenderColor(closestColorSimple))));
        // if(player != null){
        //     player.sendMessage(sampleText, false);
        // }
        colorCache.put(colorVec, closestColor);
        return closestColor;
    }

    private static String getColorString(byte colorByte){
        int colorId = (colorByte & 255) >>> 2;
        MapColor rawColor = MapColor.get(colorId);
        int brightnessId = colorByte & 0b11;
        Brightness brightness = Brightness.validateAndGet(brightnessId);
        int decimalColor = rawColor.getRenderColor(brightness);
        return String.format("[id: %d | brightness: %d | rgb: %d | hex: %x]", colorByte >>> 2, colorByte & 0b11, decimalColor, decimalColor);
    }

    public static double getSimilarity(Vec3i colorA, Vec3i colorB){
        double[] labA = xyzToLAB(rgbToXYZ(new double[]{colorA.getX(), colorA.getY(), colorA.getZ()}));
        double[] labB = xyzToLAB(rgbToXYZ(new double[]{colorB.getX(), colorB.getY(), colorB.getZ()}));
        return deltaE94(labA, labB);
    }

    public static double getSimpleSimilarity(Vec3i colorA, Vec3i colorB){
        double[] rgbA = new double[]{colorA.getX(), colorA.getY(), colorA.getZ()};
        double[] rgbB = new double[]{colorB.getX(), colorB.getY(), colorB.getZ()};
        return Math.pow(rgbA[0]-rgbB[0], 2) + Math.pow(rgbA[1]-rgbB[1], 2) + Math.pow(rgbA[2]-rgbB[2], 2);
    }

    // color functions implemented based on http://www.easyrgb.com/en/math.php
    public static double[] rgbToXYZ(double[] rgb){
        // rgb input range 0-255
        //X, Y and Z output refer to a D65/2° standard illuminant.

        double R = rgb[0] / 256;
        double G = rgb[1] / 256;
        double B = rgb[2] / 256;

        if ( R > 0.04045 ) R = Math.pow( ( R + 0.055 ) / 1.055, 2.4);
        else R = R / 12.92;

        if ( G > 0.04045 ) G = Math.pow( ( G + 0.055 ) / 1.055, 2.4);
        else G = G / 12.92;

        if ( B > 0.04045 ) B = Math.pow( ( B + 0.055 ) / 1.055, 2.4);
        else B = B / 12.92;

        R *= 100;
        G *= 100;
        B *= 100;

        double X = R * 0.4124 + G * 0.3576 + B * 0.1805;
        double Y = R * 0.2126 + G * 0.7152 + B * 0.0722;
        double Z = R * 0.0193 + G * 0.1192 + B * 0.9505;

        return new double[]{X, Y, Z};
    }

    private static final double[] REFERENCE_WHITE = rgbToXYZ(new double[]{1,1,1});

    // still yoinked from http://www.easyrgb.com/en/math.php
    public static double[] xyzToLAB(double[] xyz){
        //Reference-X, Y and Z refer to specific illuminants and observers.
        //Common reference values are available below in this same page.

        double X = xyz[0] / REFERENCE_WHITE[0];
        double Y = xyz[1] / REFERENCE_WHITE[1];
        double Z = xyz[2] / REFERENCE_WHITE[2];

        if ( X > 0.008856 ) X = Math.pow(X, 1.0/3.0 );
        else X = ( 7.787 * X ) + ( 16 / 116 );
        if ( Y > 0.008856 ) Y = Math.pow(Y, 1.0/3.0 );
        else Y = ( 7.787 * Y ) + ( 16 / 116 );
        if ( Z > 0.008856 ) Z = Math.pow(Z, 1.0/3.0 );
        else Z = ( 7.787 * Z ) + ( 16 / 116 );

        double l = ( 116 * Y ) - 16;
        double a = 500 * ( X - Y );
        double b = 200 * ( Y - Z );
        return new double[]{l, a, b};
    }

    public static double deltaE94(double[] labA, double[] labB){
        double la, aa, ba, lb, ab, bb;
        la = labA[0];
        aa = labA[1];
        ba = labA[2];
        lb = labB[0];
        ab = labB[1];
        bb = labB[2];
        double WHT_L = 1, WHT_C = 1, WHT_H = 1; // i guess ?

        double xC1 = Math.sqrt( Math.pow( aa , 2 ) + Math.pow( ba , 2 ) );
        double xC2 = Math.sqrt( Math.pow( ab , 2 ) + Math.pow( bb , 2 ) );
        double xDL = lb - la;
        double xDC = xC2 - xC1;
        double xDE = Math.sqrt( ( ( la - lb ) * ( la - lb ) )
                + ( ( aa - ab ) * ( aa - ab ) )
                + ( ( ba - bb ) * ( ba - bb ) ) );

        double xDH = ( xDE * xDE ) - ( xDL * xDL ) - ( xDC * xDC );
        if ( xDH > 0 ) {
            xDH = Math.sqrt( xDH );
        } else {
            xDH = 0;
        }
        double xSC = 1 + ( 0.045 * xC1 );
        double xSH = 1 + ( 0.015 * xC1 );
        xDL /= WHT_L;
        xDC /= WHT_C * xSC;
        xDH /= WHT_H * xSH;

        return Math.sqrt( Math.pow(xDL , 2) + Math.pow(xDC , 2) + Math.pow(xDH, 2) );
    }
}
