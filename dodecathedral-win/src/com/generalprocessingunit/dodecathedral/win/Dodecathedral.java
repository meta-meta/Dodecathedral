package com.generalprocessingunit.dodecathedral.win;

import com.generalprocessingunit.dodecathedral.core.*;
import org.puredata.core.PdBase;
import processing.core.PApplet;
import processing.core.PConstants;

public class Dodecathedral extends AbstractDodecathedral {

    JavaSoundThread audioThread;
    int patch;

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "com.generalprocessingunit.dodecathedral.win.Dodecathedral" });
    }

    @Override
    public void setup() {
        size(displayWidth, displayHeight, PApplet.OPENGL);
        super.setup();
    }

    @Override
    public void draw() {
        if(mt[0].touched){
            mt[0].update(0, mouseX, mouseY, 0, 0, millis());
        }

        super.draw();
    }

    @Override
	public void setupPd() {
        audioThread = new JavaSoundThread(44100, 1, 1);
        try{
            patch = PdBase.openPatch("data/ShepardTone/ShepardTone.pd");
            audioThread.start();
        }
        catch(Exception e){
            System.out.println(e);
        }
        PdBase.sendFloat("volume", 80f);
	}

    @Override
    public void playNote() {
        PdBase.sendFloat("delta", DeltaHistory.getCurrentDelta());
    }

    @Override
    public void setNote() {
        PdBase.sendFloat("pitch", DeltaHistory.getCurrentNote());
    }

    @Override
    public void playDrone(){
        PdBase.sendFloat("dronePitch", 60f);
        //        PdBase.sendFloat("droneVibratoDepth", 0.3f);
        PdBase.sendFloat("droneVolume", 90f);
    }
    @Override
    public void stopDrone(){
        PdBase.sendFloat("droneVolume", 0f);
    }

    @Override
    public void singleTap() {
        if((mouseButton == PConstants.RIGHT)){
            doubleTap();
            return;
        }
        super.singleTap();
    }

    @Override
    public void loadImages() {
        for (int i = 0; i < 12; i++) {
            Dodecahedron.symbols[i] = loadImage(String.format("%s.png", i));
        }
        //logo = loadShape("logo.svg");
        logo = loadImage("logo.png");

    }

    @Override
    public void mousePressed() {
        if(mouseButton == PConstants.CENTER){
            Modes.switchMode(Modes.Mode.MENU);
            return;
        }

        mt[0].update(0, mouseX, mouseY, 0, 0, millis());
        super.mousePressed();
    }

    @Override
    public void keyPressed() {
        if(keyCode == PConstants.DOWN)
        {
            Modes.switchMode(Modes.Mode.MENU);
            return;
        }

        if(keyCode == PConstants.UP)
        {
            MenuManager.back();
            return;
        }

        if(keyCode == PConstants.LEFT){
            millisAtFrameZero = millis();
            return;
        }

        super.keyPressed();
    }

    @Override
    public void mouseReleased() {
        mt[0].update();

        super.mouseReleased();
    }

    @Override
    public void stop() {
        try
        {
            audioThread.interrupt();
            audioThread.join();
            PdBase.closePatch(patch);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        super.stop();
    }
}