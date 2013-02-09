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

	private static PVector[] stars = new PVector[50];
	private int[] millisAtSpawn = new int[50];
	
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
		
		float theta = (float)Math.random()*PConstants.TWO_PI;
		float v = (float)Math.random()/2 + 0.25f;
		stars[i].x = PApplet.cos(theta) * v;
		stars[i].y = PApplet.sin(theta) * v;
			
		millisAtSpawn[i] = _parent.millis();
	}

	void plot(float x, float y) {
		_parent.stroke(255);
		_parent.strokeWeight(3);		
		
		_parent.hint(PConstants.DISABLE_DEPTH_MASK);
				
		for (int i = 0; i < 15; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.98f , newY*0.98f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.stroke(127);
		_parent.strokeWeight(3);		
		
		for (int i = 15; i < 30; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.98f , newY*0.98f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.stroke(127);
		_parent.strokeWeight(3);		
		
		for (int i = 30; i < stars.length; i++) {
			int millis = _parent.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < 400 && Math.abs(newY) < 400) {
				
				_parent.line(newX*0.98f , newY*0.98f - 50,newX,newY-50);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_parent.hint(PConstants.ENABLE_DEPTH_MASK);
		
	}
	
	

	
}
