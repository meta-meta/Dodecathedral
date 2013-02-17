package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

/**Contains all the geometry, orientation, colors, textures to draw the dodecahedron. 
 * Manages touch control to rotate and play the dodecahedron.
 * 
 * @author Paul M. Christian
 *
 */
public class Dodecahedron {
	private static PApplet _p5;
	private static IDodecathedral _parent;

	//dodecahedron geometry
	private static final PVector[] vertices = new PVector[]{
		// coordinates of all the dodecahedron vertices
		new PVector(0.607f, 0.000f, 0.795f),
		new PVector(0.188f, 0.577f, 0.795f),
		new PVector(-0.491f, 0.357f, 0.795f),
		new PVector(-0.491f, -0.357f, 0.795f),
		new PVector(0.188f, -0.577f, 0.795f),
		new PVector(0.982f, 0.000f, 0.188f),
		new PVector(0.304f, 0.934f, 0.188f),
		new PVector(-0.795f, 0.577f, 0.188f),
		new PVector(-0.795f, -0.577f, 0.188f),
		new PVector(0.304f, -0.934f, 0.188f),
		new PVector(0.795f, 0.577f, -0.188f),
		new PVector(-0.304f, 0.934f, -0.188f),
		new PVector(-0.982f, 0.000f, -0.188f),
		new PVector(-0.304f, -0.934f, -0.188f),
		new PVector(0.795f, -0.577f, -0.188f),
		new PVector(0.491f, 0.357f, -0.795f),
		new PVector(-0.188f, 0.577f, -0.795f),
		new PVector(-0.607f, 0.000f, -0.795f),
		new PVector(-0.188f, -0.577f, -0.795f),
		new PVector(0.491f, -0.357f, -0.795f)
	};
	
	private static final Pentagon[] pentagons = new Pentagon[]{
		// assign vertices to the correct pentagons. 
		//Order matters here!
		new Pentagon(new int[] { 0, 1, 2, 3, 4 }, vertices),
		new Pentagon(new int[] { 0, 1, 6, 10, 5 }, vertices),
		new Pentagon(new int[] { 1, 2, 7, 11, 6 }, vertices),
		new Pentagon(new int[] { 2, 3, 8, 12, 7 }, vertices),
		new Pentagon(new int[] { 3, 4, 9, 13, 8 }, vertices),
		new Pentagon(new int[] { 4, 0, 5, 14, 9 }, vertices),		
		new Pentagon(new int[] { 15, 16, 17, 18, 19 }, vertices),
		new Pentagon(new int[] { 18, 19, 14, 9, 13 }, vertices),
		new Pentagon(new int[] { 17, 18, 13, 8, 12 }, vertices),
		new Pentagon(new int[] { 16, 17, 12, 7, 11 }, vertices),
		new Pentagon(new int[] { 15, 16, 11, 6, 10 }, vertices),
		new Pentagon(new int[] { 19, 15, 10, 5, 14 }, vertices)
	};
	static int selectedPentagon = 0;
	
	//colors of the panels
	private static final Color[] colors = new Color[] { 
		new Color(0xFF0000), 
		new Color(0xff6c00), 
		new Color(0xffe400), 
		new Color(0x80ff00), 
		new Color(0x06c229), 
		new Color(0x00fea7), 
		new Color(0x00ffff),
		new Color(0x008eff), 
		new Color(0x0016ff), 
		new Color(0x8000ff), 
		new Color(0xf177ff), 
		new Color(0xd20159) };

	//symbols on the panels
	PImage[] symbols = new PImage[12];
	
	//orientation
	private static PVector _lookAt = new PVector(0, 0, -1);
	static float zRot = 0;
	static float xRot = 0;
	private static float _zRotPrev = 0;
	private static float _xRotPrev = 0;
	private static float _zMomentum = 0;
	private static float _xMomentum = 0;
	private static float[] _zMomentumBuffer = new float[2];
	private static float[] _xMomentumBuffer = new float[2];
	boolean touched = false;
	
	// tap detection
	private static final int _maxMovementForTap = 10;
	private static final int _maxMillisBetweenTwoPointers = 200;
	// used for tap indicator animation
	static int tap = 0;
	static int millisAtTap = 0;

	// rotation coordinates for each panel so the computer can play
	static float[] zRotLookup = new float[12];
	static float[] xRotLookup = new float[12];

	Dodecahedron(IDodecathedral p) {
		_p5 = p.getPApplet();
		_parent = p;

		setRotationLookupCoordinates();
	}

	private void setRotationLookupCoordinates() {
		// set the rotation coordinates for each panel
		for(int i = 0; i < 12; i++){
			float zR = 0;
			float xR = 0;
			
			float minAngle = 10; //arbitrary number
			
			float minZR = 0;
			float minXR = 0;
			
			for(zR = 0; zR < PConstants.TWO_PI; zR += PConstants.PI/120){
				PVector look = rotatePVectorX(PConstants.HALF_PI - xR, new PVector(0, 0, -1));
				look = rotatePVectorZ(-zR, look);
				
				float angle = PApplet.sq(PVector.angleBetween(look, pentagons[i].center));
				if(minAngle > angle){
					minAngle = angle;
					minZR = zR;
				}
			}
			
			minAngle = 10;
			for(xR = 0; xR < PConstants.TWO_PI; xR += PConstants.PI/120){
				PVector look = rotatePVectorX(PConstants.HALF_PI - xR, new PVector(0, 0, -1));
				look = rotatePVectorZ(-minZR, look);
				
				float angle = PApplet.sq(PVector.angleBetween(look, pentagons[i].center));
				if(minAngle > angle){
					minAngle = angle;
					minXR = xR;
				}
			}
						
			zRotLookup[i] = minZR;
			xRotLookup[i] = minXR;
		}
	}

	/**Draws the dodecahedron.
	 * This method gets called in the game loop.
	 */
	void plot() {
		orientate();
		
		getSelectedPentagon();
		
		drawColoredPanels();
		
		drawSymbols();
		
		drawHighlightGlass();
			
		drawTapIndicator();
	}

	private void drawColoredPanels() {
		_p5.noStroke();
		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			int alpha = 169;
			if(selectedPentagon == i){
				alpha = 255;
			}
			_p5.fill(colors[i].R, colors[i].G, colors[i].B, alpha);
			_p5.beginShape();
			for (int j = 0; j < 5; j++) // loop through each pentagon's vertices
			{
				//2000 is an arbitrary distance
				float x = pentagons[i].innerPentagon[j].x * 2000;
				float y = pentagons[i].innerPentagon[j].y * 2000;
				float z = pentagons[i].innerPentagon[j].z * 2000;

				_p5.vertex(x, y, z);
			}
			_p5.endShape(PConstants.CLOSE);
		}
	}
	
	private void drawHighlightGlass() {
		_p5.noStroke();

		_p5.fill(colors[selectedPentagon].R, colors[selectedPentagon].G, colors[selectedPentagon].B, 127);
		
		_p5.beginShape();
		for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
		{						
			//1700 is an arbitrary distance
			float x = pentagons[selectedPentagon].vertices[i].x * 1700;
			float y = pentagons[selectedPentagon].vertices[i].y * 1700;
			float z = pentagons[selectedPentagon].vertices[i].z * 1700;
			_p5.vertex(x, y, z);
		}
		_p5.endShape(PConstants.CLOSE);
	}
	
	private void drawSymbols() {
		_p5.fill(127); //needed or the texture gets tinted for some reason		
		_p5.textureMode(PConstants.NORMAL); //our pentagon coordinates are based on the unit circle		
		
		_p5.beginShape();	
		_p5.texture(symbols[selectedPentagon]);
		for (int j = 0; j < 5; j++) // loop through each pentagon's vertices										
		{
			//1500 is an arbitrary distance closer than 2000
			float x = pentagons[selectedPentagon].innerPentagon[j].x * 1500; 
			float y = pentagons[selectedPentagon].innerPentagon[j].y * 1500;
			float z = pentagons[selectedPentagon].innerPentagon[j].z * 1500;

			// generate uv texture mapping coordinates for square to pentagon				
			float u = 0, v = 0;

			/*
			 * (from wolfram alpha) coordinates of a pentagon inscribed in
			 * unit circle 0,-1 0.951,-0.309 0.588,0.809 -0.588,0.809
			 * -0.951,-0.309
			 */

			switch (j) {
			case 3: // top of each pentagon
				u = 0;
				v = -1;
				break;
			case 4:
				u = 0.951f;
				v = -0.309f;
				break;
			case 0:
				u = 0.588f;
				v = 0.809f;
				break;
			case 1:
				u = -0.588f;
				v = 0.809f;
				break;
			case 2:
				u = -0.951f;
				v = -0.309f;
				break;
			}

			// (n + 1) / 2 centers the texture
			_p5.vertex(x, y, z, (u + 1) / 2, (v + 1) / 2); 																	
		}			
		_p5.endShape(PConstants.CLOSE);	
	}
	
	void drawTapIndicator() {
		_p5.noFill();		
		_p5.noStroke();	

		// single finger tap lights panel and fades for 500 milliseconds
		if (tap == 1) {
			_p5.fill(255, 256 - ((_p5.millis() - millisAtTap)*(256f/500)));
		}

		// double finger tap darkens panel and fades for 500 milliseconds
		if (tap == 2) {
			_p5.fill(0, 256 - ((_p5.millis() - millisAtTap)*(256f/500)));
		}

		// reset the animation
		if (_p5.millis() - millisAtTap > 500) {
			tap = 0;
			return;
		}				

		// draw the translucent indicator pentagon over the face corresponding to the delta played
		_p5.beginShape();
		for (int i = 0; i < 5; i++) // loop through the pentagon's vertices
		{
			float x = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].x * 1700;
			float y = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].y * 1700;
			float z = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].z * 1700;
			_p5.vertex(x, y, z);
		}
		_p5.endShape(PConstants.CLOSE);
	}	
	
	void getSelectedPentagon() {
		float smallestAngle = 10000f; // arbitrary large number
		int pentagon = 0;

		// find the pentagon whose sum of angles between each of its vertices and the lookAt vector is smallest
		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			// sum the angles between the lookAt vector and each of this pentagon's 5 vertices
			float totalAngle = 0f;
			for (int j = 0; j < 5; j++) {
				totalAngle += PVector.angleBetween(_lookAt, pentagons[i].vertices[j]);
			}

			if (totalAngle < smallestAngle) {
				smallestAngle = totalAngle;
				pentagon = i;
			}
		}

		// set the new selected pentagon
		if (selectedPentagon != pentagon) {
			// give a little nudge each time we land on a new pentagon
			_parent.vibrate();
			
			selectedPentagon = pentagon;
		}
	}
	
	/**Handles touch interaction with the dodecahedron
	 * This method gets called in the game loop.
	 * @param mt the MultiTouch object contains information about what is touching the screen and when
	 */
	void touchControl(MultiTouch[] mt) {
				
		// we have a finger on the screen
		if (mt[0].touched) {				
			fingerOnScreen(mt);
			touched = true;
			return;
		}		
		
		fingerReleased(mt);		
	}
	
	private void fingerOnScreen(MultiTouch[] mt) {
		_zRotPrev = zRot;
		_xRotPrev = xRot;
		
		zRot -= ((mt[0].currentX - mt[0].prevX)) * ((PApplet.TWO_PI * 0.8f) / _parent.sketchWidth());
		xRot += ((mt[0].currentY - mt[0].prevY)) * ((PApplet.PI * 0.8f) / _parent.sketchHeight());
		
		stopAtFloorAndCeiling();
				
		for (int i = _zMomentumBuffer.length - 1; i > 0; i--) {
			_zMomentumBuffer[i] = _zMomentumBuffer[i - 1];
			_xMomentumBuffer[i] = _xMomentumBuffer[i - 1];
		}

		_zMomentumBuffer[0] = _zMomentum;
		_xMomentumBuffer[0] = _xMomentum;	
	
		// calculate momentum
		int timeBetweenTouches = (mt[0].millisAtLastMove - mt[0].prevMillis);
		if (timeBetweenTouches > 0) { //avoid divide by zero
			_zMomentum = ((zRot - _zRotPrev) / timeBetweenTouches);
			_xMomentum = ((xRot - _xRotPrev) / timeBetweenTouches);
		}
	}
	
	private void fingerReleased(MultiTouch[] mt) {
		// rotate the dodecahedron
		
		if(touched){
			float zM = 0f, xM = 0f;
			int zI = 0, xI = 0;
			for(int i=0; i<_zMomentumBuffer.length; i++){
				if(zM < PApplet.abs(_zMomentumBuffer[i])){
					zM = PApplet.abs(_zMomentumBuffer[i]);
					zI = i;
				}
				if(xM < PApplet.abs(_xMomentumBuffer[i])){
					xM = PApplet.abs(_xMomentumBuffer[i]);
					xI = i;
				}
			}			
			
			_zMomentum = _zMomentumBuffer[zI];
			_xMomentum = _xMomentumBuffer[xI];
			
			touched = false;	
			
			for(int i=0; i<_zMomentumBuffer.length; i++){
				_zMomentumBuffer[i] = 0;
				_xMomentumBuffer[i] = 0;
			}	
		}
		
		zRot += _zMomentum * 10;
		xRot += _xMomentum * 10;		
				
		stopAtFloorAndCeiling();
		
		// Decelerate				
		_zMomentum -= _zMomentum * (PApplet.abs(_zMomentum) >= 0.003f ? 0.05f : 0.1f);
		_xMomentum -= _xMomentum * (PApplet.abs(_xMomentum) >= 0.003f ? 0.05f : 0.1f);
		
		//Check if we have a tap		
		if (mt[0].totalMovement < _maxMovementForTap && mt[0].tap) {
				
			// check to see if we have a two-finger tap and if so, how in synch the two taps are
			if (PApplet.abs(mt[1].millisAtLastMove - mt[0].millisAtLastMove) < _maxMillisBetweenTwoPointers && mt[1].tap) {
				//tap = 2;
				_parent.doubleTap();
			} else {
				if (mt[0].id == 0) {
					//tap = 1;
					_parent.singleTap();
				}
			}
			mt[0].tap = false;
			mt[1].tap = false;
			millisAtTap = _p5.millis();
		}
	}
	
	private void stopAtFloorAndCeiling() {
		if (xRot >= PConstants.HALF_PI) {
			_xMomentum = 0;
			xRot = PConstants.HALF_PI;
		}
		if (xRot <= -PConstants.HALF_PI) {
			_xMomentum = 0;
			xRot = -PConstants.HALF_PI;
		}
	}
	
	private void orientate() {
		// initially, orient the dodecahedron room so we're not looking at the
		// ceiling
		_p5.rotateX(-PConstants.HALF_PI);

		// now rotate according to the current rotation angles
		_p5.rotateX(xRot);
		_p5.rotateZ(zRot);

		// rotate lookAt vector in the opposite direction
		_lookAt = rotatePVectorX(PConstants.HALF_PI - xRot, new PVector(0, 0, -1));
		_lookAt = rotatePVectorZ(-zRot, _lookAt);
	}
	
	static PVector rotatePVectorZ(float angle, PVector vector) {

		PVector v = new PVector(0, 0, vector.z);

		v.x += (vector.x * PApplet.cos(angle) - vector.y * PApplet.sin(angle));
		v.y += (vector.x * PApplet.sin(angle) + vector.y * PApplet.cos(angle));

		return v;
	}	

	static PVector rotatePVectorX(float angle, PVector vector) {

		PVector v = new PVector(vector.x, 0, 0);

		v.y = (vector.y * PApplet.cos(angle) - vector.z * PApplet.sin(angle));
		v.z = (vector.y * PApplet.sin(angle) + vector.z * PApplet.cos(angle));

		return v;
	}

}
