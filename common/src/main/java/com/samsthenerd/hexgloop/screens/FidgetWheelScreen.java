package com.samsthenerd.hexgloop.screens;

import com.samsthenerd.hexgloop.items.ItemFidget;
import com.samsthenerd.hexgloop.items.ItemFidget.FidgetSettings;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.wnboi.LabelProvider;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;
import com.samsthenerd.wnboi.screen.SpokeRenderer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FidgetWheelScreen extends AbstractContextWheelScreen{
    public LabelProvider labelProvider;
    public FidgetSettings settings;

    private Screen oldScreen;

    public FidgetWheelScreen(LabelProvider labelProvider, Screen _oldScreen, FidgetSettings settings){
        super(Text.of("Fidget Wheel"), labelProvider.perPage());
        requireKeydown = true;
        keyBinding = HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
        this.labelProvider = labelProvider;
        numSections = labelProvider.perPage();  
        oldScreen = _oldScreen;
        this.settings = settings;
        
    }

    public FidgetWheelScreen(LabelProvider labelProvider){
        this(labelProvider, null, new FidgetSettings(labelProvider.getCount()));
    }

    @Override
    protected SpokeRenderer genSpokeRenderer(double orX, double orY, double rad, int numSecs, int secIndex){
        return new FidgetSpokeRenderer(orX, orY, rad, secIndex, labelProvider, settings).setGap(gap).setInnerRadius(outerRadius*0.35).setAngleOffset(angleOffset);
    }

    @Override
    protected void initConsts(){
        this.centerX = this.width / 2.0;
        this.centerY = this.height / 2.0;

        this.outerRadius = this.height * 0.3;
        upperBoundRadius = outerRadius*1.1;
        lowerBoundRadius = outerRadius*0.35;

        angleOffset = settings.startAngle; // so that the first spoke is centered at the top
    }

    @Override
    public void close(){
        this.client.setScreen(oldScreen);
        ItemFidget.screen = null;
    }

    public void triggerSpoke(int index){
        // WNBOI.LOGGER.info("triggered spoke " + index);
        selectedSection = -1; // so that we don't recurse ourselves
        labelProvider.toSlot(index);
    }
}
