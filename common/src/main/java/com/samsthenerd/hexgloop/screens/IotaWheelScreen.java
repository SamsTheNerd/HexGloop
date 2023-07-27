package com.samsthenerd.hexgloop.screens;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.SpellbookScreenInterface;
import com.samsthenerd.hexgloop.misc.wnboi.IotaProvider;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;
import com.samsthenerd.wnboi.screen.SpokeRenderer;

import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class IotaWheelScreen extends AbstractContextWheelScreen{
    public IotaProvider iotaProvider;

    public int onPage;
    private Screen oldScreen;

    public IotaWheelScreen(IotaProvider _iotaProvider, Screen _oldScreen){
        super(Text.of("Iota Selection Wheel"), _iotaProvider.perPage());
        requireKeydown = true;
        keyBinding = HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
        iotaProvider = _iotaProvider;
        numSections = iotaProvider.perPage();
        onPage = (iotaProvider.currentSlot()-1) / iotaProvider.perPage();     
        oldScreen = _oldScreen;
    }

    public IotaWheelScreen(IotaProvider _iotaProvider){
        this(_iotaProvider, null);
    }


    public void triggerSpoke(int index){
        // WNBOI.LOGGER.info("triggered spoke " + index);
        selectedSection = -1; // so that we don't recurse ourselves
        iotaProvider.toSlot(index + onPage*iotaProvider.perPage());
    }

    @Override
    protected void initConsts(){
        this.centerX = this.width / 2.0;
        this.centerY = this.height / 2.0;

        this.outerRadius = this.height * 0.3;
        upperBoundRadius = outerRadius*1.1;
        lowerBoundRadius = outerRadius*0.35;

        angleOffset = Math.PI * 0.5 + Math.PI / this.numSections; // so that the first spoke is centered at the top
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // HexGloop.logPrint("currentSlot: " + (iotaProvider.currentSlot()-1) + ", perPage: " + iotaProvider.perPage() + ", onPage: " + onPage);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected SpokeRenderer genSpokeRenderer(double orX, double orY, double rad, int numSecs, int secIndex){
        return new IotaSpokeRenderer(orX, orY, rad, numSecs, secIndex, 
            secIndex + onPage*iotaProvider.perPage(), iotaProvider).setGap(gap).setInnerRadius(outerRadius*0.35).setAngleOffset(angleOffset);
    }

    @Override
    protected void addAllSections(){
        initConsts();
        spokeRenderers = new ArrayList<SpokeRenderer>(); // just to clear it
        for(int i = 0; i < this.numSections; i++){
            spokeRenderers.add(genSpokeRenderer(centerX, centerY, outerRadius, this.numSections, i));
        }
        // additional spokes to select between pages of pages
        if(iotaProvider.getCount() > iotaProvider.perPage()){
            for(int i = 0; i < iotaProvider.getCount() / iotaProvider.perPage(); i++){
                spokeRenderers.add(new CenterModSpokeRenderer(centerX, centerY, outerRadius*0.34, 
                    numSections, i, iotaProvider).setGap(gap).setInnerRadius(0).setAngleOffset(angleOffset));
                }
            ((CenterModSpokeRenderer) spokeRenderers.get(onPage + numSections)).currentPage = true;
        }
    }

    @Override
    public void close(){
        this.client.setScreen(oldScreen);
        HexGloopItems.MULTI_FOCUS_ITEM.get().screen = null;
        ((SpellbookScreenInterface) HexItems.SPELLBOOK).clearScreen();
    }

    @Override
    protected void doRenderTooltip(MatrixStack matrices, int mouseX, int mouseY){
        NbtCompound iotaNBT = iotaProvider.getIotaNBT(selectedSection + onPage*iotaProvider.perPage());
        if(iotaNBT == null){
            this.renderTooltip(matrices, Text.of("Empty"), mouseX, mouseY);
        } else {
            List<Text> tooltip = new ArrayList<Text>();
            tooltip.add(HexIotaTypes.getDisplay(iotaNBT));
            Text name = iotaProvider.getName(selectedSection + onPage*iotaProvider.perPage());
            if(name != null){
                tooltip.add(name);
            }
            this.renderTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    public void changePage(int newPage){
        onPage = newPage;
        addAllSections();
    }

    public int didClickInner(int mouseX, int mouseY){
        double diffX = mouseX - centerX;
        double diffY = mouseY - centerY;
        if(diffX == 0 && diffY == 0){
            // center
            return -1;
        }
        double dist = Math.sqrt(diffX * diffX + diffY * diffY);
        if(dist >= lowerBoundRadius){
            // outside of the wheel
            return -1;
        }
        // otherwise inside the wheel
        double theta;
        if(diffX == 0){
            if(diffY > 0){
                theta = Math.PI * 0.5;
            }else{
                theta = Math.PI * 1.5;
            }
        } else {
            theta = Math.atan(diffY / diffX);
            if(diffX < 0){
                theta += Math.PI;
            }
        }
        return getSectionIndexFromAngle(theta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0){
            int inner = didClickInner((int)mouseX, (int)mouseY);
            if(inner >= 0 && iotaProvider.getCount() > iotaProvider.perPage()){
                changePage(inner);
                return true;
            }
            return closeWheel(true);
        }
        if(button == 1 && iotaProvider.getCount() > iotaProvider.perPage()){
            int sectionIndex = getSectionIndexFromMouse((int)mouseX, (int)mouseY);
            if(sectionIndex >= 0 && sectionIndex < iotaProvider.perPage()){
                changePage(sectionIndex);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9){
            int index = keyCode - GLFW.GLFW_KEY_0 -1; // -1 so that 1 key triggers 0th index
            if(index == -1) index = 9;
            if(modifiers == GLFW.GLFW_MOD_SHIFT && index <= iotaProvider.getCount() / iotaProvider.perPage()
                && iotaProvider.getCount() > iotaProvider.perPage()){
                changePage(index);
                return true;
            }
            if(index < this.numSections){
                triggerSpoke(index);
                if(this != null){
                    this.close();
                }
                return true;
            } 
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
