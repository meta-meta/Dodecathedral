package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**Contains all the geometry, orientation, colors, textures to draw the dodecahedron. 
 * Manages touch control to rotate and play the dodecahedron.
 * 
 * @author Paul M. Christian
 *
 */
public class Dodecahedron {
	private static Dodecathedral _parent;

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

	//orientation
	private static PVector _lookAt = new PVector(0, 0, -1);
	static float zRot = 0;
	static float xRot = 0;
	private static float _zRotPrev = 0;
	private static float _xRotPrev = 0;
	private static float _zMomentum = 0;
	private static float _xMomentum = 0;
	
	// tap detection
	private static final int _maxMovementForTap = 10;
	private static final int _maxMillisBetweenTwoPointers = 200;
	// used for tap indicator animation
	static int tap = 0;
	static int millisAtTap = 0;

	// rotation coordinates for each panel so the computer can play
	static float[] zRotLookup = new float[12];
	static float[] xRotLookup = new float[12];

	Dodecahedron(Dodecathedral p) {
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
		_parent.noStroke();
		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			int alpha = 169;
			if(selectedPentagon == i){
				alpha = 255;
			}
			_parent.fill(colors[i].R, colors[i].G, colors[i].B, alpha);
			_parent.beginShape();
			for (int j = 0; j < 5; j++) // loop through each pentagon's vertices
			{
				//2000 is an arbitrary distance
				float x = pentagons[i].innerPentagon[j].x * 2000;
				float y = pentagons[i].innerPentagon[j].y * 2000;
				float z = pentagons[i].innerPentagon[j].z * 2000;

				_parent.vertex(x, y, z);
			}
			_parent.endShape(PConstants.CLOSE);
		}
	}
	
	private void drawHighlightGlass() {
		_parent.noStroke();

		_parent.fill(colors[selectedPentagon].R, colors[selectedPentagon].G, colors[selectedPentagon].B, 127);

		//simpler approach
		_parent.beginShape();
		for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
		{
			//2000 is an arbitrary distance
			float x = pentagons[selectedPentagon].vertices[i].x * 1200;
			float y = pentagons[selectedPentagon].vertices[i].y * 1200;
			float z = pentagons[selectedPentagon].vertices[i].z * 1200;

			_parent.vertex(x, y, z);
		}
		_parent.endShape(PConstants.CLOSE);
	}
	
	private void drawSymbols() {
		_parent.fill(255); //needed or the texture gets tinted for some reason		
		_parent.textureMode(PConstants.NORMAL); //our pentagon coordinates are based on the unit circle		
		
		_parent.beginShape();	
		_parent.texture(_parent.symbols[selectedPentagon]);
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
			_parent.vertex(x, y, z, (u + 1) / 2, (v + 1) / 2); 																	
		}			
		_parent.endShape(PConstants.CLOSE);	
	}
	
	void drawTapIndicator() {
		_parent.noFill();		
		_parent.noStroke();	

		// single finger tap lights panel and fades for 500 milliseconds
		if (tap == 1) {
			_parent.fill(255, 256 - ((_parent.millis() - millisAtTap)*(256f/500)));
		}

		// double finger tap darkens panel and fades for 500 milliseconds
		if (tap == 2) {
			_parent.fill(0, 256 - ((_parent.millis() - millisAtTap)*(256f/500)));
		}

		// reset the animation
		if (_parent.millis() - millisAtTap > 500) {
			tap = 0;
			return;
		}				

		// draw the translucent indicator pentagon over the face corresponding to the delta played
		_parent.beginShape();
		for (int i = 0; i < 5; i++) // loop through the pentagon's vertices
		{
			float x = pentagons[PApplet.abs((_parent.deltaHistory.deltas[0]))].vertices[i].x * 1200;
			float y = pentagons[PApplet.abs((_parent.deltaHistory.deltas[0]))].vertices[i].y * 1200;
			float z = pentagons[PApplet.abs((_parent.deltaHistory.deltas[0]))].vertices[i].z * 1200;
			_parent.vertex(x, y, z);
		}
		_parent.endShape(PConstants.CLOSE);
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
			return;
		}		
		
		fingerReleased(mt);		
	}
	
	private void fingerOnScreen(MultiTouch[] mt) {
		_zRotPrev = zRot;
		_xRotPrev = xRot;
		
		zRot -= ((mt[0].currentX - mt[0].prevX)) * ((PApplet.TWO_PI * 0.8f) / _parent.screenWidth);
		xRot += ((mt[0].currentY - mt[0].prevY)) * ((PApplet.PI * 0.8f) / _parent.screenHeight);
		
		stopAtFloorAndCeiling();
		
		// calculate momentum
		int timeBetweenTouches = (mt[0].millisAtLastMove - mt[0].prevMillis);
		if (timeBetweenTouches > 0) { //avoid divide by zero
			_zMomentum = ((zRot - _zRotPrev) / timeBetweenTouches);
			_xMomentum = ((xRot - _xRotPrev) / timeBetweenTouches);
		}
	}
	
	private void fingerReleased(MultiTouch[] mt) {
		// rotate the dodecahedron
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
				tap = 2;
				_parent.doubleTap();
			} else {
				if (mt[0].id == 0) {
					tap = 1;
					_parent.singleTap();
				}
			}
			mt[0].tap = false;
			mt[1].tap = false;
			millisAtTap = _parent.millis();
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
		_parent.rotateX(-PConstants.HALF_PI);

		// now rotate according to the current rotation angles
		_parent.rotateX(xRot);
		_parent.rotateZ(zRot);

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
