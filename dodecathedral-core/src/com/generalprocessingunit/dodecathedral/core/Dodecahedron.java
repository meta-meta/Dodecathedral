package com.generalprocessingunit.dodecathedral.core;

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
    private static final float scale = 2000f;
	//dodecahedron geometry
	private static final PVector[] vertices = new PVector[]{
		// coordinates of all the dodecahedron vertices
		new PVector(0.607f*scale, 0.000f*scale, 0.795f*scale),
		new PVector(0.188f*scale, 0.577f*scale, 0.795f*scale),
		new PVector(-0.491f*scale, 0.357f*scale, 0.795f*scale),
		new PVector(-0.491f*scale, -0.357f*scale, 0.795f*scale),
		new PVector(0.188f*scale, -0.577f*scale, 0.795f*scale),
		new PVector(0.982f*scale, 0.000f*scale, 0.188f*scale),
		new PVector(0.304f*scale, 0.934f*scale, 0.188f*scale),
		new PVector(-0.795f*scale, 0.577f*scale, 0.188f*scale),
		new PVector(-0.795f*scale, -0.577f*scale, 0.188f*scale),
		new PVector(0.304f*scale, -0.934f*scale, 0.188f*scale),
		new PVector(0.795f*scale, 0.577f*scale, -0.188f*scale),
		new PVector(-0.304f*scale, 0.934f*scale, -0.188f*scale),
		new PVector(-0.982f*scale, 0.000f*scale, -0.188f*scale),
		new PVector(-0.304f*scale, -0.934f*scale, -0.188f*scale),
		new PVector(0.795f*scale, -0.577f*scale, -0.188f*scale),
		new PVector(0.491f*scale, 0.357f*scale, -0.795f*scale),
		new PVector(-0.188f*scale, 0.577f*scale, -0.795f*scale),
		new PVector(-0.607f*scale, 0.000f*scale, -0.795f*scale),
		new PVector(-0.188f*scale, -0.577f*scale, -0.795f*scale),
		new PVector(0.491f*scale, -0.357f*scale, -0.795f*scale)
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
	public static int selectedPentagon = 0;
	
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

    /* UV coordinates of the pentagons for texturing
     * (from wolfram alpha) coordinates of a pentagon inscribed in
     * unit circle 0,-1 0.951,-0.309 0.588,0.809 -0.588,0.809
     * -0.951,-0.309
     * (n + 1) / 2 centers the texture
     */
    private static final float[] textureU = {(0.588f+1)/2, (-0.588f+1)/2, (-0.951f+1)/2, (0f+1)/2, (0.951f+1)/2};
    private static final float[] textureV = {(0.809f+1)/2, (0.809f+1)/2, (-0.309f+1)/2, (-1f+1)/2, (-0.309f+1)/2};

    //symbols on the panels
	public static PImage[] symbols = new PImage[12];
	
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
	private static boolean touched = false;
	
	// tapIndicator detection
	private static final int _maxMovementForTap = 10;
	private static final int _maxMillisBetweenTwoPointers = 200;
	// used for tapIndicator indicator animation
	public static int tapIndicator = 0;
	static int millisAtTap = 0;

	// rotation coordinates for each panel so the computer can play
	static final float[] zRotLookup = new float[12];
	static final float[] xRotLookup = new float[12];

	static {
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

    private Dodecahedron(IDodecathedral p) {}

	/**Draws the dodecahedron.
	 * This method gets called in the game loop.
	 */
	public static void plot(PApplet p5, IDodecathedral parent) {
		setLookatVector(p5);

        float fov = PApplet.PI/2.7f;
        float cameraZ = (p5.height/2.0f) / PApplet.tan(fov/2.0f);
        float backedUp = 1500f;
        p5.perspective(fov, 1.05f*(float)(p5.width)/(float)p5.height, cameraZ/10f, cameraZ*10000f);
        p5.camera(-_lookAt.x * backedUp, -_lookAt.y * backedUp, -_lookAt.z * backedUp, _lookAt.x, _lookAt.y, _lookAt.z, 0f, 0f, 1f);

		getSelectedPentagon(parent);
		drawColoredPanels(p5);
		drawSelectedSymbol(p5);
		drawHighlightGlass(p5);
		drawTapIndicator(p5);


        p5.perspective();
	}

	private static void drawColoredPanels(PApplet p5) {

		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			if(selectedPentagon == i){
                p5.noStroke();
                p5.fill(colors[i].R, colors[i].G, colors[i].B, 255);
			}
            else{
//                p5.stroke(50);
//                p5.strokeWeight(8);
//                p5.fill(0.75f*colors[i].R, 0.75f*colors[i].G, 0.75f*colors[i].B, 50);

                p5.strokeWeight(4);
                p5.noFill();
                p5.stroke(0.75f*colors[i].R, 0.75f*colors[i].G, 0.75f*colors[i].B);
            }

			p5.beginShape();
			for (int j = 0; j < 5; j++) // loop through each pentagon's vertices
			{
				float x = pentagons[i].innerPentagon[j].x;
				float y = pentagons[i].innerPentagon[j].y;
				float z = pentagons[i].innerPentagon[j].z;

				p5.vertex(x, y, z);
			}
			p5.endShape(PConstants.CLOSE);
		}
	}
	
	private static void drawHighlightGlass(PApplet p5) {
		p5.noStroke();
		p5.fill(colors[selectedPentagon].R, colors[selectedPentagon].G, colors[selectedPentagon].B, 127);
		
		p5.beginShape();
		for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
		{						
			float x = pentagons[selectedPentagon].vertices[i].x;
			float y = pentagons[selectedPentagon].vertices[i].y;
			float z = pentagons[selectedPentagon].vertices[i].z;
			p5.vertex(x, y, z);
		}
		p5.endShape(PConstants.CLOSE);
	}
	
	private static void drawSelectedSymbol(PApplet p5) {
        p5.noStroke();
		p5.fill(127); //needed or the texture gets tinted for some reason
		p5.textureMode(PConstants.NORMAL); //our pentagon coordinates are based on the unit circle
		
		p5.beginShape();
		p5.texture(symbols[selectedPentagon]);
		for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
		{
			float x = pentagons[selectedPentagon].innerPentagon[i].x;
			float y = pentagons[selectedPentagon].innerPentagon[i].y;
			float z = pentagons[selectedPentagon].innerPentagon[i].z;

			p5.vertex(x, y, z, textureU[i], textureV[i]);
		}			
		p5.endShape(PConstants.CLOSE);
	}
	
	static void drawTapIndicator(PApplet p5) {
		p5.noFill();
		p5.noStroke();

		// single finger tapIndicator lights panel and fades for 500 milliseconds
		if (tapIndicator == 1) {
			p5.fill(255, 256 - ((p5.millis() - millisAtTap) * (256f / 500)));
		}

		// double finger tapIndicator darkens panel and fades for 500 milliseconds
		if (tapIndicator == 2) {
			p5.fill(0, 256 - ((p5.millis() - millisAtTap) * (256f / 500)));
		}

		// reset the animation
		if (p5.millis() - millisAtTap > 500) {
			tapIndicator = 0;
			return;
		}				

		// draw the translucent indicator pentagon over the face corresponding to the delta played
		p5.beginShape();
		for (int i = 0; i < 5; i++) // loop through the pentagon's vertices
		{
			float x = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].x;
			float y = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].y;
			float z = pentagons[PApplet.abs((DeltaHistory.deltas[0]))].vertices[i].z;
			p5.vertex(x, y, z);
		}
		p5.endShape(PConstants.CLOSE);
	}	
	
	static void getSelectedPentagon(IDodecathedral dodecathedral) {
		float smallestAngle = 10000f; // arbitrary large number
		int pentagon = 0;

		// find the pentagon whose sum of angles between each of its vertices and the lookAt vector is smallest
		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			// sum the angles between the lookAt vector and each of this pentagon's 5 vertices
			float totalAngle = 0f;
			for (int j = 0; j < 5; j++) {
				totalAngle += PVector.angleBetween(_lookAt, pentagons[i].vertices[j]);

                // no point going through the whole loop if we're already this far off
                if (totalAngle > smallestAngle)
                    break;
			}

			if (totalAngle < smallestAngle) {
				smallestAngle = totalAngle;
				pentagon = i;
			}
		}

		// set the new selected pentagon
		if (selectedPentagon != pentagon) {
			// give a little nudge each time we land on a new pentagon
			dodecathedral.vibrate();
			
			selectedPentagon = pentagon;
		}
	}

	/**Handles touch interaction with the dodecahedron
	 * This method gets called in the game loop.
	 * @param mt the MultiTouch object contains information about what is touching the screen and when
	 */
	public static void touchControl(MultiTouch[] mt, PApplet p5, IDodecathedral parent) {
		// we have a finger on the screen
		if (mt[0].touched) {				
			fingerOnScreen(mt, p5);
			touched = true;
			return;
		}		
		
		fingerReleased(mt, p5, parent);
	}
	
	private static void fingerOnScreen(MultiTouch[] mt, PApplet p5) {
		_zRotPrev = zRot;
		_xRotPrev = xRot;
		
		zRot -= ((mt[0].currentX - mt[0].prevX)) * ((PApplet.TWO_PI * 0.8f) / p5.width);
		xRot += ((mt[0].currentY - mt[0].prevY)) * ((PApplet.PI * 0.8f) / p5.height);
		
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
	
	private static void fingerReleased(MultiTouch[] mt, PApplet p5, IDodecathedral parent) {
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
		
		//Check if we have a tapIndicator
		if (mt[0].totalMovement < _maxMovementForTap && mt[0].tap) {
				
			// check to see if we have a two-finger tapIndicator and if so, how in synch the two taps are
			if (PApplet.abs(mt[1].millisAtLastMove - mt[0].millisAtLastMove) < _maxMillisBetweenTwoPointers && mt[1].tap) {
				//tapIndicator = 2;
				parent.doubleTap();
			} else {
				if (mt[0].id == 0) {
					//tapIndicator = 1;
					parent.singleTap();
				}
			}
			mt[0].tap = false;
			mt[1].tap = false;
			millisAtTap = p5.millis();
		}
	}
	
	private static void stopAtFloorAndCeiling() {
        float limit = PConstants.HALF_PI - 0.1f;
        if (xRot >= (limit)) {
			_xMomentum = 0;
			xRot = limit;
		}
		if (xRot <= -limit) {
			_xMomentum = 0;
			xRot = -limit;
		}
	}
	
	private static void setLookatVector(PApplet p5) {
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