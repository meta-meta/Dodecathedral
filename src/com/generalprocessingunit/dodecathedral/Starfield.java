package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Draws a map that displays the current note on a clock face. Trails are drawn
 * that indicate the direction of movement from the last note to the current
 * one.
 * 
 * @author Paul M. Christian
 * 
 */
public class Starfield {
	private Dodecathedral _parent;
	
	private static final Color stroke = new Color(255, 255, 255, 255);
	//private static final Color fill = new Color(127, 127, 127, 127);
	//private static final int strokeWeight = 3;

	private static PVector[] stars = new PVector[100];
	private int[] millisAtSpawn = new int[100];
	
	Starfield(Dodecathedral parent) {
		_parent = parent;
		generateVectors();
	}
	
	void generateVectors(){
		for(int i=0; i< stars.length; i++){
			generateVector(i);			
		}
	}

	void generateVector(int i){
		stars[i] = new PVector();
		stars[i].x = (float)Math.random();
		stars[i].y = (float)Math.random();
		millisAtSpawn[i] = _parent.millis();
	}

	void plot(float x, float y) {
		_parent.stroke(255);
		_parent.strokeWeight(3);		
		
		_parent.hint(PConstants.DISABLE_DEPTH_MASK);
		
		
		
		
		for (int i = 0; i < 25; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x - .5f) * millis * 2;
			float newY = y + (stars[i].y - .5f) * millis * 2;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.99f , newY*0.99f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.stroke(127);
		_parent.strokeWeight(3);		
		
		for (int i = 25; i < 50; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x - .5f) * millis * 2;
			float newY = y + (stars[i].y - .5f) * millis * 2;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.99f , newY*0.99f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.stroke(127);
		_parent.strokeWeight(2);		
		
		for (int i = 50; i < stars.length; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x - .5f) * millis * 2;
			float newY = y + (stars[i].y - .5f) * millis * 2;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.99f , newY*0.99f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.hint(PConstants.ENABLE_DEPTH_MASK);
		
	}
	
	

	
}
