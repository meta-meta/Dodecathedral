package com.generalprocessingunit.dodecathedral.core;

import processing.core.*;

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

    //colors of the panels
    private static final Color[] colors = new Color[]{
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
        new Color(0xd20159)};

    private static final Pentagon[] pentagons = new Pentagon[]{
		// assign vertices to the correct pentagons.
		//Order matters here!
		new Pentagon(new int[] { 0, 1, 2, 3, 4 }, vertices, colors[0]),
		new Pentagon(new int[] { 0, 1, 6, 10, 5 }, vertices, colors[1]),
		new Pentagon(new int[] { 1, 2, 7, 11, 6 }, vertices, colors[2]),
		new Pentagon(new int[] { 2, 3, 8, 12, 7 }, vertices, colors[3]),
		new Pentagon(new int[] { 3, 4, 9, 13, 8 }, vertices, colors[4]),
		new Pentagon(new int[] { 4, 0, 5, 14, 9 }, vertices, colors[5]),
		new Pentagon(new int[] { 15, 16, 17, 18, 19 }, vertices, colors[6]),
		new Pentagon(new int[] { 18, 19, 14, 9, 13 }, vertices, colors[7]),
		new Pentagon(new int[] { 17, 18, 13, 8, 12 }, vertices, colors[8]),
		new Pentagon(new int[] { 16, 17, 12, 7, 11 }, vertices, colors[9]),
		new Pentagon(new int[] { 15, 16, 11, 6, 10 }, vertices, colors[10]),
		new Pentagon(new int[] { 19, 15, 10, 5, 14 }, vertices, colors[11])
	};

	public static int selectedPentagon = 0;

    /* UV coordinates of the pentagons for texturing
     * (from wolfram alpha) coordinates of a pentagon inscribed in
     * unit circle 0,-1 0.951,-0.309 0.588,0.809 -0.588,0.809
     * -0.951,-0.309
     * (n + 1) / 2 centers the texture
     */
    protected static final float[] textureU = {(0.588f+1)/2, (-0.588f+1)/2, (-0.951f+1)/2, (0f+1)/2, (0.951f+1)/2};
    protected static final float[] textureV = {(0.809f+1)/2, (0.809f+1)/2, (-0.309f+1)/2, (-1f+1)/2, (-0.309f+1)/2};

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

    public static void createGeometry(PApplet p5) {
        for (int i = 0; i < 12; i++) {
            pentagons[i].createGeometry(p5, i, colors[i]);
        }

        Stars.createGeometry(p5);
    }

	/**Draws the dodecahedron.
	 * This method gets called in the game loop.
	 */
	public static void plot(PApplet p5, IDodecathedral parent) {
		setLookatVector(p5);

        float fov = PApplet.PI/2.7f;
        float cameraZ = (p5.height/2.0f) / PApplet.tan(fov/2.0f);
        float backedUp = 1500f;
        p5.perspective(fov, 1.05f*(float)(p5.width)/(float)p5.height, cameraZ/10f, cameraZ*100000f);
        p5.camera(-_lookAt.x * backedUp, -_lookAt.y * backedUp, -_lookAt.z * backedUp, _lookAt.x, _lookAt.y, _lookAt.z, 0f, 0f, 1f);

        Stars.plot(p5);

		getSelectedPentagon(parent);
		drawPanels(p5);
	}

	private static void drawPanels(PApplet p5) {
        for (int i = 0; i < 12; i++)
        {
            pentagons[i].drawPanel(p5, i == selectedPentagon, i == PApplet.abs(DeltaHistory.deltas[0]) ? tapIndicator : 0, millisAtTap);
        }
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