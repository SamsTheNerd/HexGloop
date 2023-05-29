package com.samsthenerd.hexgloop.screens;

import com.samsthenerd.hexgloop.misc.wnboi.IotaProvider;
import com.samsthenerd.wnboi.screen.SpokeRenderer;

import net.minecraft.util.math.Vec3d;

// for the center of it
public class CenterModSpokeRenderer extends SpokeRenderer{
    public boolean currentPage = false;
    IotaProvider iotaProvider;


    CenterModSpokeRenderer(double orX, double orY, double rad, int numSecs, int secIndex, IotaProvider _iotaProvider){
        super(orX, orY, rad, numSecs, secIndex);
        iotaProvider = _iotaProvider;
        innerOutlineWeight = 1;
        numDivisions = 10;
    }

    @Override
    public int getColorFill(int vI, int numOuter, int numInner, boolean isInner){
        if(currentPage){
            Vec3d colorPos;
            if(isInner){
                colorPos = new Vec3d(iotaProvider.getRNG().nextFloat()*0.5, 
                    sectionIndex + iotaProvider.getRNG().nextFloat()*0.5, iotaProvider.getRNG().nextFloat()*0.5).multiply(0.3);
            } else {
                colorPos = new Vec3d(sectionIndex + iotaProvider.getRNG().nextFloat()*0.5, 
                    iotaProvider.getRNG().nextFloat()*0.5, sectionIndex + iotaProvider.getRNG().nextFloat()*0.5).multiply(0.3);
            }
            int color = iotaProvider.getColorizer().getColor((float) currentTime, colorPos);
            return color;
        }
        return 0x00000000; // clear
    }

    @Override
    public int getColorOutline(int vI){
        if(currentPage){
            return 0x00000000; // clear
        }
        Vec3d colorPos = new Vec3d(vI + iotaProvider.getRNG().nextFloat()*0.5, 
            sectionIndex + iotaProvider.getRNG().nextFloat()*0.5, sectionIndex + iotaProvider.getRNG().nextFloat()*0.5).multiply(0.25);
        int color = iotaProvider.getColorizer().getColor((float) currentTime, colorPos);
        return color;
    }
}
