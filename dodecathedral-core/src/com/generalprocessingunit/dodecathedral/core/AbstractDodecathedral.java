package com.generalprocessingunit.dodecathedral.core;

import com.generalprocessingunit.dodecathedral.core.exercises.Exercises;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

/**
 * Author: Paul
 * Date: 4/7/13
 * Time: 2:55 PM
 */
public abstract class AbstractDodecathedral extends PApplet implements IDodecathedral {
    Demo demo = new Demo(this);
    UserData userData = new UserData();
    private static boolean drone = false;

    // we need to preload the characters we're going to use in menus and messages
    private static final char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,?!'\"(){}[]/\\-+=".toCharArray();

    // Multi-Touch input
    public MultiTouch[] mt;

    //public PShape logo;
    public PImage logo;
    private PShape logoRect;
    public Integer millisAtFrameZero = null;

    @Override
    public void setup() {
        setupPd();

        setupMultiTouch();

        loadImages();

        createLogoRect();

        Dodecahedron.createGeometry(this);

        MenuManager.loadFonts(this, charset);
        Message.loadFonts(this, charset);
    }

    private void createLogoRect() {
        logoRect = createShape();
        logoRect.beginShape();
        logoRect.noStroke();
        logoRect.texture(logo);
        logoRect.textureMode(PConstants.NORMAL);
        logoRect.vertex(0,0,0,0);
        logoRect.vertex(width,0,1,0);
        logoRect.vertex(width,height,1,1);
        logoRect.vertex(0,height,0,1);
        logoRect.endShape(PConstants.CLOSE);
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
        millisAtFrameZero = null == millisAtFrameZero ? millis() : millisAtFrameZero;

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



//        if(millis() - millisAtFrameZero < 7000)
//        {
//            showSplashscreen();
//            return;
//        }

        switch (Modes.currentMode) {
            case DEMO_PLAYING:
                demo.playSequence();
                hint(PConstants.DISABLE_DEPTH_MASK);
                fill(200,180, 160, 30);
                stroke(200,180, 160);
                strokeWeight(4);
                blendMode(PConstants.ADD);
                rect(0,0,width,height);
                hint(PConstants.ENABLE_DEPTH_MASK);
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

    private void showSplashscreen() {

        fill(66);
        tint(255);
        int millis = millis() - millisAtFrameZero;

        int t = 5000;

        int u = t - 2000;
        if(millis > u)
        {
            fill(66, 255 - (millis-u)/15f );
        }



        float w = width, h = height, x = 0, y = 0;
        if(millis > t)
        {

            w += (millis-t)*10f;
            h += (millis-t)*10f;
            x = 0;
            y = (height - h) / 2f;

        }

        hint(PConstants.DISABLE_DEPTH_MASK);
        rect(0,0,width, height);

//        if(millis > t+1800){
//            tint(255, 255 - (millis-(t + 1800))/4f );
//        }

        hint(PConstants.ENABLE_DEPTH_MASK);
        shape(logoRect, x, y, w, h);

        //image(logo, x, y, w, h);

        //shape(logo, 0, 0, logo.width, logo.height );
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
}
