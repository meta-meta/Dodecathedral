package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;

public interface IDodecathedral {
	PApplet getPApplet();
	
	MultiTouch[] getMultiTouch();
	
	void vibrate();

	void doubleTap();

	void singleTap();

	char[] getCharset();

	int sketchWidth();

	int sketchHeight();
}