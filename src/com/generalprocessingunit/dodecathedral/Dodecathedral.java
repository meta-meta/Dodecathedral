/**
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package com.generalprocessingunit.dodecathedral;

import java.io.File;
import java.io.IOException;

import org.puredata.android.processing.PureDataP5Android;
import org.puredata.core.utils.IoUtils;
import org.xmlpull.v1.XmlPullParserException;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class Dodecathedral extends PApplet {

	PureDataP5Android pd;

	DeltaHistory deltaHistory = new DeltaHistory();
	Dodecahedron dodecahedron = new Dodecahedron(this);
	MapOverlay map = new MapOverlay(this);
	Starfield starfield = new Starfield(this);
	MenuManager menuManager;
	Message message;
	DeltaSequences deltaSequences;
	Demo demo = new Demo(this);
	Exercises exercises;
	UserData userData = new UserData();
	
	//we need to preload the characters we're going to use in menus and messages
	public static final char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,?!'\"(){}[]/\\-+=".toCharArray(); 

	PImage[] symbols = new PImage[12];

	// Multi-Touch input
	private static final int maxTouchEvents = 2;
	MultiTouch[] mt;
 
	// Vibrator
	NotificationManager gNotificationManager;
	Notification gNotification;
	long[] gVibrate = { 0, 20 };
	
	boolean drone = false;
    boolean starfieldOn = false;

	@Override
	public void setup() {
		setupPD();

		orientation(LANDSCAPE);

		setupMultiTouch();

		loadImages();

		loadDeltaSequences();

		exercises = new Exercises(this);
		
		loadMenus();
		
		message = new Message(this);
		
		Modes.currentMode = Modes.Mode.FREE_PLAY;
	}

	private void loadMenus() {
		try {
			menuManager = new MenuManager(this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadDeltaSequences() {
		try {
			deltaSequences = new DeltaSequences(this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupMultiTouch() {
		// Populate our MultiTouch array that will track all of our
		// touch-points:
		mt = new MultiTouch[maxTouchEvents];
		for (int i = 0; i < maxTouchEvents; i++) {
			mt[i] = new MultiTouch();
		}
	}

	private void setupPD() {
		pd = new PureDataP5Android(this, 44100, 0, 2);
		int zipId = com.generalprocessingunit.dodecathedral.R.raw.patch;
		pd.unpackAndOpenPatch(zipId, "ShepardTone.pd");
		pd.start();
		playInitialNote();
	}

	@Override
	public void draw() {
		background(50);
		float w = this.screenWidth;
		float h = this.screenHeight;
		
		// center the screen
		translate(w / 2, h / 2);
		
		// draw the starfield
		if (starfieldOn) {
			starfield.plot(0, 0);
		}
			

		// draw the dodecahedron room
		pushMatrix();
		dodecahedron.plot();
		popMatrix();

		// uncenter the screen
		translate(-w / 2, -h / 2);

		switch (Modes.currentMode) {
		case DEMO_PLAYING:
			demo.playSequence();
			break;
		case MENU:			
			menuManager.plot(w / 40, h / 40, w - w / 20, h - h / 20);
			return;			
		case MESSAGE:			
			message.plot(w / 40, h / 2 + h / 40, w - w / 20, h / 2 - h / 20);
			return;
		default: // FREE_PLAY
			dodecahedron.touchControl(mt);
		}
		
		// draw the note map
		float mapSize = w / 7;
		map.plot(w - (mapSize + 10), 10, mapSize);
		
		if(Exercises.running){
			exercises.runExercise();
		}
	}

	void playNote() {
		pd.sendFloat("pitch", deltaHistory.currentNote + 12);
		pd.sendFloat("volume", 75);
		pd.sendBang("playNote");
	}

	void playInitialNote() {
		// The PD patch doesn't sound right on the first note, so let's get that
		// out
		// of the way.
		pd.sendFloat("pitch", 12);
		pd.sendFloat("volume", 0);
		pd.sendBang("playNote");		
	}
	
	void playDrone() {
		if(drone)
		{
			pd.sendFloat("droneVolume", 70);		
			pd.sendFloat("dronePitch", 60);
			pd.sendFloat("droneFreqOffsetL", -0.7f);
			pd.sendFloat("droneFreqOffsetR", 0.7f);
		}
		else
		{
			pd.sendFloat("droneVolume", 0);
		}
	}

	void singleTap() {
		deltaHistory.navigate(Dodecahedron.selectedPentagon, true, this.millis());
		playNote();
	}

	void doubleTap() {
		deltaHistory.navigate(Dodecahedron.selectedPentagon, false, this.millis());
		playNote();
	}

	void loadImages() {

		File dir = getFilesDir();

		try {
			IoUtils.extractZipResource(getResources().openRawResource(com.generalprocessingunit.dodecathedral.R.raw.symbols), dir, true);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		for (int i = 0; i < 12; i++) {
			Log.d("info", String.format("%s/%s.png", dir, i));
			symbols[i] = loadImage(String.format("%s/%s.png", dir, i));
		}
		Log.d("info", "Done loading images");
	}

	void vibrate() {
		gNotificationManager.notify(1, gNotification);
	}

	@Override
	protected void onPause() {
		pd.sendFloat("droneVolume", 0);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (null != pd && drone) {
			pd.sendFloat("droneVolume", 70);
		}
		
		// Create our Notification Manager:
		gNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Create our Notification that will do the vibration:
		gNotification = new Notification();
		// Set the vibration:
		gNotification.vibrate = gVibrate;
	}

	@Override
	public boolean surfaceTouchEvent(MotionEvent me) {
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
		for (int i = 0; i < maxTouchEvents; i++) {
			if (i < pointers) {
				mt[i].update(me, i, millis());
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
	public void keyPressed() {
		if (this.key == PConstants.CODED) {
			if (this.keyCode == KeyEvent.KEYCODE_MENU) {

				Modes.switchMode(Mode.MENU);

				// TODO make this less hacky. this should fix the dodecahedron
				// going crazy when switching back to free_play
				mt[0].millisAtLastMove = millis();
				mt[1].millisAtLastMove = millis();
			}
			
			if (this.keyCode == KeyEvent.KEYCODE_BACK) {
				menuManager.back();
				this.keyCode = 0; // don't quit by default
			}
		}
	}

	@Override
	public void onBackPressed() {
		// do whatever you want here, or nothing
	}

	@Override
	public void stop() {
		if (null != pd) {
			pd.release();
		}
		super.stop();
	}

	// boilerplate
	public int sketchWidth() {
		return this.screenWidth;
	}

	public int sketchHeight() {
		return this.screenHeight;
	}

	public String sketchRenderer() {
		return PApplet.OPENGL;
	}
}