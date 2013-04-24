package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

public class MultiTouch {
	// Public attrs that can be queried for each touch point:
	float firstX, firstY;
	float prevX, prevY;
	float currentX, currentY;
	float totalMovement;
	float size, prevSize;
	public int millisAtFirstTouch, millisAtLastMove = 0, prevMillis;
	int id;
	boolean touched = false;
	boolean tap = false;

	// Executed when this index has been touched:
	public void update(int pointerId, float x, float y, float size, int index, int millis) {
		// me : The passed in MotionEvent being queried
		// index : the index of the item being queried
		// newId : The id of the pressed item.

		prevMillis = millisAtLastMove;
		millisAtLastMove = millis;
		tap = true;

		prevX = currentX;
		prevY = currentY;

		currentX = x;
		currentY = y;

		// keep tabs on the total movement of this pointer
		totalMovement += PApplet.abs(currentX - prevX);
		totalMovement += PApplet.abs(currentY - prevY);

		prevSize = this.size;
		this.size = size;

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

		this.id = pointerId;
		touched = true;
	}

	// Executed if this index hasn't been touched
	public void update() {
		touched = false;
	}
}
