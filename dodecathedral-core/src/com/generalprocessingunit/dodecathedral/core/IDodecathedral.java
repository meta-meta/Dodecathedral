package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

public interface IDodecathedral {
	PApplet getPApplet();
	
	MultiTouch[] getMultiTouch();
	
	void vibrate();

	void doubleTap();

	void singleTap();

	int getSketchWidth();

	int getSketchHeight();

    boolean toggleDrone();

    void setNote();
}