package com.generalprocessingunit.dodecathedral;

import android.util.Log;
import processing.core.*;

/**Contains all the geometry, orientation, colors, textures to draw the dodecahedron. 
 * Manages touch control to rotate and play the dodecahedron.
 * 
 * @author Paul M. Christian
 *
 */
public class Dodecahedron {
	private static Dodecathedral parent;

	//dodecahedron geometry
	private static final PVector[] vertices = new PVector[20];
	private static final  Pentagon[] pentagons = new Pentagon[12];
	int selectedPentagon = 0;
	
	// used for tap indicator animation
	int tap = 0;
	int millisAtTap = 0;

	//colors of the panels
	private static final Color[] colors = new Color[] { new Color(0xFF0000), new Color(0xff6c00), new Color(0xffe400), new Color(0x80ff00), new Color(0x06c229), new Color(0x00fea7), new Color(0x00ffff),
			new Color(0x008eff), new Color(0x0016ff), new Color(0x8000ff), new Color(0xf177ff), new Color(0xd20159) };

	//orientation
	private static PVector lookAt = new PVector(0, 0, -1);
	static float zRot = 0;
	static float xRot = 0;
	private static float zMomentum = 0;
	private static float xMomentum = 0;

	// rotation coordinates for each panel so the computer can play
	static float[] zRotLookup = new float[12];
	static float[] xRotLookup = new float[12];	
	
	private static boolean magnet=false;

	Dodecahedron(Dodecathedral p) {
		parent = p;

		// coordinates of all the dodecahedron vertices
		vertices[0] = new PVector(0.607f, 0.000f, 0.795f);
		vertices[1] = new PVector(0.188f, 0.577f, 0.795f);
		vertices[2] = new PVector(-0.491f, 0.357f, 0.795f);
		vertices[3] = new PVector(-0.491f, -0.357f, 0.795f);
		vertices[4] = new PVector(0.188f, -0.577f, 0.795f);
		vertices[5] = new PVector(0.982f, 0.000f, 0.188f);
		vertices[6] = new PVector(0.304f, 0.934f, 0.188f);
		vertices[7] = new PVector(-0.795f, 0.577f, 0.188f);
		vertices[8] = new PVector(-0.795f, -0.577f, 0.188f);
		vertices[9] = new PVector(0.304f, -0.934f, 0.188f);
		vertices[10] = new PVector(0.795f, 0.577f, -0.188f);
		vertices[11] = new PVector(-0.304f, 0.934f, -0.188f);
		vertices[12] = new PVector(-0.982f, 0.000f, -0.188f);
		vertices[13] = new PVector(-0.304f, -0.934f, -0.188f);
		vertices[14] = new PVector(0.795f, -0.577f, -0.188f);
		vertices[15] = new PVector(0.491f, 0.357f, -0.795f);
		vertices[16] = new PVector(-0.188f, 0.577f, -0.795f);
		vertices[17] = new PVector(-0.607f, 0.000f, -0.795f);
		vertices[18] = new PVector(-0.188f, -0.577f, -0.795f);
		vertices[19] = new PVector(0.491f, -0.357f, -0.795f);

		// assign vertices to the correct pentagons
		pentagons[0] = new Pentagon(new int[] { 0, 1, 2, 3, 4 }, vertices);

		pentagons[1] = new Pentagon(new int[] { 0, 1, 6, 10, 5 }, vertices);
		pentagons[2] = new Pentagon(new int[] { 1, 2, 7, 11, 6 }, vertices);
		pentagons[3] = new Pentagon(new int[] { 2, 3, 8, 12, 7 }, vertices);
		pentagons[4] = new Pentagon(new int[] { 3, 4, 9, 13, 8 }, vertices);
		pentagons[5] = new Pentagon(new int[] { 4, 0, 5, 14, 9 }, vertices);

		pentagons[10] = new Pentagon(new int[] { 15, 16, 11, 6, 10 }, vertices);
		pentagons[9] = new Pentagon(new int[] { 16, 17, 12, 7, 11 }, vertices);
		pentagons[8] = new Pentagon(new int[] { 17, 18, 13, 8, 12 }, vertices);
		pentagons[7] = new Pentagon(new int[] { 18, 19, 14, 9, 13 }, vertices);
		pentagons[11] = new Pentagon(new int[] { 19, 15, 10, 5, 14 }, vertices);

		pentagons[6] = new Pentagon(new int[] { 15, 16, 17, 18, 19 }, vertices);

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
			
			Log.d("RotLookups", "zRotLookup" + i + ": " + zRotLookup[i] + "xRotLookup" + i + ": " + xRotLookup[i]);
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
		
		drawBeveledEdges();
			
		drawTapIndicator();
	}

	private void drawColoredPanels() {
		parent.noStroke();
		for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		{
			int alpha = 127;
			if(selectedPentagon == i){
				alpha = 255;
			}
			parent.fill(colors[i].R, colors[i].G, colors[i].B, alpha);
			parent.beginShape();
			for (int j = 0; j < 5; j++) // loop through each pentagon's vertices
			{
				//2000 is an arbitrary distance
				float x = pentagons[i].innerPentagon[j].x * 2000;
				float y = pentagons[i].innerPentagon[j].y * 2000;
				float z = pentagons[i].innerPentagon[j].z * 2000;

				parent.vertex(x, y, z);
			}
			parent.endShape(PConstants.CLOSE);
		}
	}
	
	private void drawBeveledEdges() {
		parent.noStroke();

		parent.fill(colors[selectedPentagon].R, colors[selectedPentagon].G, colors[selectedPentagon].B, 127);

		//simpler approach
		parent.beginShape();
		for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
		{
			//2000 is an arbitrary distance
			float x = pentagons[selectedPentagon].vertices[i].x * 1200;
			float y = pentagons[selectedPentagon].vertices[i].y * 1200;
			float z = pentagons[selectedPentagon].vertices[i].z * 1200;

			parent.vertex(x, y, z);
		}
		parent.endShape(PConstants.CLOSE);
		
		/*for (int i = 0; i < 5; i++) {
			int nextIndex = (i + 1) % 5;

			parent.beginShape();

			parent.vertex(pentagons[selectedPentagon].innerPentagon[i].x * 2000, pentagons[selectedPentagon].innerPentagon[i].y * 2000, pentagons[selectedPentagon].innerPentagon[i].z * 2000);
			parent.vertex(pentagons[selectedPentagon].innerPentagon[nextIndex].x * 2000, pentagons[selectedPentagon].innerPentagon[nextIndex].y * 2000, pentagons[selectedPentagon].innerPentagon[nextIndex].z * 2000);
			parent.vertex(pentagons[selectedPentagon].vertices[nextIndex].x * 1200, pentagons[selectedPentagon].vertices[nextIndex].y * 1200, pentagons[selectedPentagon].vertices[nextIndex].z * 1200);
			parent.vertex(pentagons[selectedPentagon].vertices[i].x * 1200, pentagons[selectedPentagon].vertices[i].y * 1200, pentagons[selectedPentagon].vertices[i].z * 1200);

			parent.endShape(PConstants.CLOSE);
		}*/
	}
	
	private void drawSymbols() {
		parent.fill(255); //needed or the texture gets tinted for some reason		
		parent.textureMode(PConstants.NORMAL); //our pentagon coordinates are based on the unit circle

		//for (int i = 0; i < 12; i++) // loop through the 12 pentagons
		//{
			parent.beginShape();	
			parent.texture(parent.symbols[selectedPentagon]);
			for (int j = 0; j < 5; j++) // loop through each pentagon's vertices										
			{
				//1200 is an arbitrary distance closer than 2000
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
				parent.vertex(x, y, z, (u + 1) / 2, (v + 1) / 2); 																	
			}			
			parent.endShape(PConstants.CLOSE);
		//}
	}
	
	void drawTapIndicator() {
		parent.noFill();		
		parent.noStroke();		
		//parent.stroke(colors[selectedPentagon].R, colors[selectedPentagon].G, colors[selectedPentagon].B);

		// single finger tap lights panel and fades for 500 milliseconds
		if (tap == 1) {
			parent.fill(255, 256 - ((parent.millis() - millisAtTap)*(256f/500)));
		}

		// double finger tap darkens panel and fades for 500 milliseconds
		if (tap == 2) {
			parent.fill(0, 256 - ((parent.millis() - millisAtTap)*(256f/500)));
		}

		// reset the animation
		if (parent.millis() - millisAtTap > 500) {
			tap = 0;
			return;
		}				

		// draw the translucent indicator pentagon over the face corresponding to the delta played
		parent.beginShape();
		for (int i = 0; i < 5; i++) // loop through the pentagon's vertices
		{
			float x = pentagons[PApplet.abs((parent.deltaHistory.deltas[0]))].vertices[i].x * 1200;
			float y = pentagons[PApplet.abs((parent.deltaHistory.deltas[0]))].vertices[i].y * 1200;
			float z = pentagons[PApplet.abs((parent.deltaHistory.deltas[0]))].vertices[i].z * 1200;
			parent.vertex(x, y, z);
		}
		parent.endShape(PConstants.CLOSE);
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
				totalAngle += PVector.angleBetween(lookAt, pentagons[i].vertices[j]);
			}

			if (totalAngle < smallestAngle) {
				smallestAngle = totalAngle;
				pentagon = i;
			}
		}

		// set the new selected pentagon
		if (selectedPentagon != pentagon) {
			// give a little nudge each time we land on a new pentagon
			parent.vibrate();
			
			selectedPentagon = pentagon;
		}
	}
	
	/**Handles touch interaction with the dodecahedron
	 * This method gets called in the game loop.
	 * @param mt the MultiTouch object contains information about what is touching the screen and when
	 */
	void touchControl(MultiTouch[] mt) {
		// rotate the dodecahedron
		xRot += xMomentum / 10000;
		zRot -= zMomentum / 10000;
				
		// stop at the floor and ceiling
		if (xRot >= PConstants.HALF_PI) {
			xMomentum = 0;
			xRot = PConstants.HALF_PI;
		}
		if (xRot <= -PConstants.HALF_PI) {
			xMomentum = 0;
			xRot = -PConstants.HALF_PI;
		}

		//Calculate the time elapsed since the last touch event
		int elapsedTime = parent.millis() - mt[0].millisAtLastMove;		
		
		// we have a finger on the screen
		if (mt[0].touched) {

			int timeBetweenMoves = (mt[0].millisAtLastMove - mt[0].prevMillis);
			if (timeBetweenMoves > 0) {
				zMomentum += ((mt[0].currentX - mt[0].prevX) / timeBetweenMoves) * 180;
				xMomentum += ((mt[0].currentY - mt[0].prevY) / timeBetweenMoves) * 120;
			}

			magnet = false;

			return;
		}
		
		// At this point, the finger is no longer on the screen
		
		
		//magnet stuff
		if ((PApplet.abs(zMomentum) + PApplet.abs(xMomentum)) < 300) {
			magnet = true;
		}
		
		/*if (magnet) {
			float angle = PApplet.sqrt(PVector.angleBetween(lookAt, pentagons[selectedPentagon].center));
			float magneticForce = angle > PConstants.PI / 15 ? 1 / angle : 0;
			Log.d("magnet", "angle: " + angle + " " + PConstants.PI / 60);

			if (selectedPentagon != 0 && selectedPentagon != 6) {
				zMomentum -= Demo.getRotationVelocity(magneticForce, zRot, zRotLookup[selectedPentagon]) * 10;
			}
			xMomentum += Demo.getRotationVelocity(magneticForce, xRot, xRotLookup[selectedPentagon]) * 10;
		}		*/
		
		// Decelerate				
		zMomentum -= zMomentum * (PApplet.abs(zMomentum) >= 50 ? 0.05 : 0.2);
		xMomentum -= xMomentum * (PApplet.abs(xMomentum) >= 50 ? 0.05 : 0.2);
		
		//Check if we have a tap
		final int maxMovementForTap = 10;
		final int maxMillisBetweenTwoPointers = 200;
		if (mt[0].totalMovement < maxMovementForTap && mt[0].tap) {

			// check to see if we have a double tap and if so, how in synch the two taps are
			if (PApplet.abs(mt[1].millisAtLastMove - mt[0].millisAtLastMove) < maxMillisBetweenTwoPointers && mt[1].tap) {
				tap = 2;
				parent.doubleTap();
			} else {
				if (mt[0].id == 0) {
					tap = 1;
					parent.singleTap();
				}
			}
			mt[0].tap = false;
			mt[1].tap = false;
			millisAtTap = parent.millis();
		}		
	}

	private void orientate() {
		// initially, orient the dodecahedron room so we're not looking at the
		// ceiling
		parent.rotateX(-PConstants.HALF_PI);

		// now rotate according to the current rotation angles
		parent.rotateX(xRot);
		parent.rotateZ(zRot);

		// rotate lookAt vector in the opposite direction
		lookAt = rotatePVectorX(PConstants.HALF_PI - xRot, new PVector(0, 0, -1));
		lookAt = rotatePVectorZ(-zRot, lookAt);
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
