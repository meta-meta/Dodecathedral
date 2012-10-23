package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import android.view.MotionEvent;

public class MultiTouch {
	// Public attrs that can be queried for each touch point:
	float firstX, firstY;
	float prevX, prevY;
	float currentX, currentY;
	float totalMovement;
	float size, prevSize;
	int millisAtFirstTouch, millisAtLastMove = 0, prevMillis;
	int id;
	boolean touched = false;
	boolean tap = false;

	// Executed when this index has been touched:
	void update(MotionEvent me, int index, int millis) {
		// me : The passed in MotionEvent being queried
		// index : the index of the item being queried
		// newId : The id of the pressed item.

		prevMillis = millisAtLastMove;
		millisAtLastMove = millis;
		tap = true;

		prevX = currentX;
		prevY = currentY;

		currentX = me.getX(index);
		currentY = me.getY(index);

		// keep tabs on the total movement of this pointer
		totalMovement += PApplet.abs(currentX - prevX);
		totalMovement += PApplet.abs(currentY - prevY);

		prevSize = size;
		size = me.getSize(index);

		// if this is the first touch, record where it starts
		if (!touched) {
			millisAtFirstTouch = millis;
			prevX = currentX;
			prevY = currentY;
			firstX = currentX;
			firstY = currentY;
			totalMovement = 0;
			prevSize = size;
		}

		id = me.getPointerId(index);
		touched = true;
	}

	// Executed if this index hasn't been touched
	void update() {
		touched = false;
	}
}
