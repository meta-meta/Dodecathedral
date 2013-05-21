package com.generalprocessingunit.dodecathedral.droid;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.generalprocessingunit.dodecathedral.core.*;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import processing.core.PApplet;

import java.io.*;

public class Dodecathedral extends AbstractDodecathedral {

    // Vibrator
    NotificationManager gNotificationManager;
    Notification gNotification;
    long[] gVibrate = { 0, 20 };


    @Override
    public void setup() {
        orientation(LANDSCAPE);
        super.setup();
    }

    @Override
    public void setupPd() {
        try{
            AudioParameters.init(this);
            int srate = Math.max(44100, AudioParameters.suggestSampleRate());
            PdAudio.initAudio(srate, 0, 2, 1, true);

            File dir = getFilesDir();
            File patchFile = new File(dir, "ShepardTone.pd");
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch), dir, true);
            PdBase.openPatch(patchFile.getAbsolutePath());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        PdAudio.startAudio(this);
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
    public void loadImages() {
        File dir = getFilesDir();

        try {
            IoUtils.extractZipResource(getResources().openRawResource(com.generalprocessingunit.dodecathedral.droid.R.raw.images), dir, true);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 12; i++) {
            Log.d("info", String.format("%s/%s.png", dir, i));
            Dodecahedron.symbols[i] = loadImage(String.format("%s/%s.png", dir, i));
        }


//        InputStream ins = getResources().openRawResource(R.drawable.logo);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        int size = 0;
//        byte[] buffer = new byte[1024];
//
//        try {
//            while ((size = ins.read(buffer, 0, 1024)) >= 0) {
//                outputStream.write(buffer, 0, size);
//            }
//            ins.close();
//            buffer = outputStream.toByteArray();
//        } catch (Exception e) {
//        }
//
//        try {
//            File file = new File(dir, "logo.png");
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(buffer);
//            fos.close();
//        } catch (Exception e) {
//        }



        logo = loadImage(String.format("%s/logo.png", dir));

        Log.d("info", "Done loading images");
    }

    @Override
    public void vibrate() {
        gNotificationManager.notify(1, gNotification);
    }

    @Override
    public boolean surfaceTouchEvent(MotionEvent me) {
        // Avoid null pointer exception
        if(null == me){
            return super.surfaceTouchEvent(null);
        }

        // Find number of touch points:
        int pointers = me.getPointerCount();

        // integer representing the type of action, which can be pressing down,
        // moving, releasing or other stuff
        final int action = me.getAction() & MotionEvent.ACTION_MASK;

        // get the pointer index for this action
        // NOTABLE BUG: ACTION_POINTER_ID_SHIFT actually returns the INDEX not
        // the id. This made me pull my hair out
        int pointerIndex = (me.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;

        // Update MultiTouch that 'is touched':
        for (int i = 0; i < mt.length; i++) {
            if (i < pointers) {
                mt[i].update(me.getPointerId(i), me.getX(i), me.getY(i), me.getSize(i), i, millis());
            }
            // Update MultiTouch that 'isn't touched':
            else {
                mt[i].update();
            }
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            mt[pointerIndex].update();
        }

        // If you want the variables for motionX/motionY, mouseX/mouseY etc.
        // to work properly, you'll need to call super.surfaceTouchEvent().
        return super.surfaceTouchEvent(me);
    }

    @Override
    public boolean surfaceKeyDown(int code, KeyEvent event) {
        if (code == KeyEvent.KEYCODE_MENU) {
            Modes.switchMode(Modes.Mode.MENU);

            // keep dodecahedron from going crazy when switching back to free_play
            mt[0].millisAtLastMove = millis();
            mt[1].millisAtLastMove = millis();
        }

        if (code == KeyEvent.KEYCODE_BACK) {
            if(Modes.currentMode.equals(Modes.Mode.MENU)){
                MenuManager.back();
            }
            return true;
        }
        return super.surfaceKeyDown(code, event);
    }

    @Override
    public String sketchRenderer() {
        return PApplet.P3D;
    }

    @Override
    protected void onResume() {
        super.onResume();

        PdAudio.startAudio(this);

        // Create our Notification Manager:
        gNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Create our Notification that will do the vibration:
        gNotification = new Notification();
        // Set the vibration:
        gNotification.vibrate = gVibrate;
    }

    @Override
    protected void onPause() {
        PdAudio.stopAudio();
        super.onPause();
    }


    @Override
    protected void onStop() {
        PdAudio.stopAudio();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // make sure to release all resources
        PdAudio.release();
        PdBase.release();
        super.onDestroy();
    }
}
