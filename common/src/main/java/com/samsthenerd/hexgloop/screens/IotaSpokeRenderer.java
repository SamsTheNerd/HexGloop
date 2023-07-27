package com.samsthenerd.hexgloop.screens;

import com.samsthenerd.hexgloop.misc.wnboi.IotaProvider;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.wnboi.screen.SpokeRenderer;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.Vec3d;

public class IotaSpokeRenderer extends SpokeRenderer{
    public int iotaIndex = 0;
    public IotaProvider iotaProvider;

    IotaSpokeRenderer(double orX, double orY, double rad, int numSecs, int secIndex, int _iotaIndex, IotaProvider _iotaProvider){
        super(orX, orY, rad, numSecs, secIndex);
        iotaIndex = _iotaIndex;
        iotaProvider = _iotaProvider;
    }

    @Override
    public void select(){
        super.select();
        innerOutlineWeight = 1;
    }

    @Override
    public int getColorFill(int vI, int numOuter, int numInner, boolean isInner){
        if(!isInner){
            NbtCompound iotaNbt = iotaProvider.getIotaNBT(iotaIndex);
            if(iotaNbt == null) return Argb.getArgb(150, 200, 200, 200);
            int color = HexIotaTypes.getColor(iotaNbt);
            return Argb.getArgb(150, Argb.getRed(color), Argb.getGreen(color), Argb.getBlue(color));
        } else if(iotaProvider.currentSlot() == iotaIndex+1){
            NbtCompound iotaNbt = iotaProvider.getIotaNBT(iotaIndex);
            if(iotaNbt == null) return Argb.getArgb(150, 200, 200, 200);
            int color = HexIotaTypes.getColor(iotaNbt);
            return Argb.getArgb(200, Argb.getRed(color), Argb.getGreen(color), Argb.getBlue(color));
        }
        return Argb.getArgb(96, 200, 200, 200);
    }

    @Override
    public int getColorOutline(int vI){
        Vec3d colorPos = new Vec3d(vI + iotaProvider.getRNG().nextFloat()*0.5, 
            iotaIndex + iotaProvider.getRNG().nextFloat()*0.5, vI + iotaProvider.getRNG().nextFloat()*0.5).multiply(0.8);
        int color = iotaProvider.getColorizer().getColor((float) currentTime, colorPos);
        return color;
    }

    @Override
    public void renderLabel(MatrixStack matrices, int mouseX, int mouseY, float delta){
        Label label = iotaProvider.getLabelMaker().getLabel(iotaIndex+1);
        if(label == null) {
            return;
        }
        // HexGloop.logPrint("rendering label " + (iotaIndex+1) + ": " + label.toNbt().toString());
        int labelDistToUse = (labelDist == null) ? (int)((outerRadius-innerRadius) / 2 + innerRadius) : labelDist;
        int x = (int)(originX+offsetX+Math.cos(midAngle)*labelDistToUse);
        int y = (int)(originY+offsetY+Math.sin(midAngle)*labelDistToUse);
        label.render(matrices, x, y, 24, 24);
    }

}
