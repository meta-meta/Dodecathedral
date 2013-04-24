package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Author: Paul
 * Date: 4/7/13
 * Time: 2:55 PM
 */
public class AbstractDodecathedral extends PApplet implements IDodecathedral {
    Demo demo = new Demo(this);
    UserData userData = new UserData();
    private static boolean drone = false;

    PGraphics bg;

    // we need to preload the characters we're going to use in menus and messages
    private static final char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,?!'\"(){}[]/\\-+=".toCharArray();

    // Multi-Touch input
    public MultiTouch[] mt;

    int i=0;

    PVector[] stars = new PVector[1000];

    @Override
    public void setup() {
        setupPd();

        setupMultiTouch();

        loadImages();

        MenuManager.loadFonts(this, charset);
        Message.loadFonts(this, charset);


        for(int i=0;i<1000;i++)
        {
            stars[i] = new PVector();
            stars[i].x = random(2000,5000)*random(-1,1);
            stars[i].y = random(2000,5000)*random(-1,1);
            stars[i].z = random(2000,5000)*random(-1,1);
        }
        bg = createGraphics(width,height, P3D);
        bg.blendMode(PConstants.ADD);
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
        background(40);
//        noStroke();
//
//        fill(40,50);
//
//        hint(PConstants.DISABLE_DEPTH_MASK);
//        rect(0,0,width,height);
//        hint(PConstants.ENABLE_DEPTH_MASK);

       /* // draw the starfield
        i++;
        i=i%61;

        bg.beginDraw();
        bg.fill(100*(i>35?1:0f),1);
        bg.rect(0,0,width,height);

        bg.translate(width / 2, height / 2);

        Starfield.plot(this, bg, 0, 0, width, height+5);


        bg.endDraw();

        hint(PConstants.DISABLE_DEPTH_MASK);
        //tint(200,200,255,200);
        image(bg,0,0);
        hint(PConstants.ENABLE_DEPTH_MASK);*/

        // center the screen
        translate(width / 2, height / 2);

        // draw the dodecahedron room

        pushMatrix();
        Dodecahedron.plot(this, this);

//        stroke(255);
//        for (int i=0;i<1000;i++) {
//            point(stars[i].x,stars[i].y,stars[i].z);
//        }
        popMatrix();




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
        DeltaHistory.navigate(Dodecahedron.selectedPentagon, true, this.millis());
        Dodecahedron.tapIndicator = 1;
        playNote();
    }

    @Override
    public void doubleTap() {
        DeltaHistory.navigate(Dodecahedron.selectedPentagon, false, this.millis());
        Dodecahedron.tapIndicator = 2;
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
