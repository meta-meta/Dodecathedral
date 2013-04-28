package com.generalprocessingunit.dodecathedral.core;

import processing.core.*;

/**
 * Author: Paul
 * Date: 4/7/13
 * Time: 2:55 PM
 */
public class AbstractDodecathedral extends PApplet implements IDodecathedral {
    Demo demo = new Demo(this);
    UserData userData = new UserData();
    private static boolean drone = false;

    // we need to preload the characters we're going to use in menus and messages
    private static final char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,?!'\"(){}[]/\\-+=".toCharArray();

    // Multi-Touch input
    public MultiTouch[] mt;

    @Override
    public void setup() {
        setupPd();

        setupMultiTouch();

        loadImages();
        Dodecahedron.createGeometry(this);

        MenuManager.loadFonts(this, charset);
        Message.loadFonts(this, charset);
    }


    public PApplet getPApplet()
    {
        return this;
    }

    public void setupPd() {}

    private void setupMultiTouch() {
        // Populate our MultiTouch array that will track all of our
        // touch-points:
        mt = new MultiTouch[2];
        for (int i = 0; i < 2; i++) {
            mt[i] = new MultiTouch();
        }
    }

    public MultiTouch[] getMultiTouch(){
        return mt;
    }

    public void loadImages() {}

    @Override
    public void draw() {
        background(20);

        // center the screen
        translate(width / 2, height / 2);

        // draw the dodecahedron room
        pushMatrix();
        Dodecahedron.plot(this, this);
        perspective();
        popMatrix();


        blendMode(PConstants.BLEND);

        // decenter the screen
        translate(-width / 2, -height / 2);

        switch (Modes.currentMode) {
            case DEMO_PLAYING:
                demo.playSequence();
                break;
            case MENU:
                MenuManager.plot(this, width / 40, height / 40, width - width / 20, height - height / 20);
                MenuManager.touchControl(this, mt);
                return;
            case MESSAGE:
                Message.plot(this, width / 40, height / 2 + height / 40, width - width / 20, height / 2 - height / 20);
                Message.touchControl(mt);
                return;
            default: // FREE_PLAY
                Dodecahedron.touchControl(mt, this, this);
        }

        // draw the note map
        float mapSize = width / 7;
        MapOverlay.plot(this, width - (mapSize + 10), 10, mapSize);

        if(Exercises.running){
            Exercises.runExercise();
        }
    }

    public void playNote(){}

    @Override
    public boolean toggleDrone(){
        if(!drone){
            playDrone();
            drone = true;
        }
        else{
            stopDrone();
            drone = false;
        }
        return drone;
    }

    public void playDrone(){}

    public void stopDrone(){}

    @Override
    public void singleTap() {
        DeltaHistory.navigate(Dodecahedron.selectedPentagon, true, millis());
        Dodecahedron.tapIndicator = 1;
        Dodecahedron.millisAtTap = millis();
        playNote();
    }

    @Override
    public void doubleTap() {
        DeltaHistory.navigate(Dodecahedron.selectedPentagon, false, millis());
        Dodecahedron.tapIndicator = 2;
        Dodecahedron.millisAtTap = millis();
        playNote();
    }

    @Override
    public void vibrate() {}

    @Override
    public int getSketchWidth() {
        return width;
    }

    @Override
    public int getSketchHeight() {
        return height;
    }
}
