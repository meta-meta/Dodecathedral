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
	private static PApplet _p5;	
	
	private static final Color stroke = new Color(255, 255, 255, 255);
	//private static final Color fill = new Color(127, 127, 127, 127);
	//private static final int strokeWeight = 3;

	private static PVector[] stars = new PVector[50];
	private int[] millisAtSpawn = new int[50];
	
	Starfield(IDodecathedral d) {
		_p5 = d.getPApplet();
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
			
		millisAtSpawn[i] = _p5.millis();
	}

	void plot(float x, float y, float w, float h) {
		_p5.stroke(255);
		_p5.strokeWeight(3);		
		
		_p5.hint(PConstants.DISABLE_DEPTH_MASK);
				
		for (int i = 0; i < stars.length/3; i++) {
			int millis = _p5.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				_p5.line(newX*0.98f , newY*0.98f - 50,newX,newY-50);
				//_p5.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_p5.stroke(127);
		_p5.strokeWeight(3);		
		
		for (int i = stars.length/3; i < 2 * stars.length/3 ; i++) {
			int millis = _p5.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				_p5.line(newX*0.98f , newY*0.98f - 50, newX,newY-50);
				//_p5.point(newX, newY);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_p5.stroke(127);
		_p5.strokeWeight(3);		
		
		for (int i = 2 * stars.length/3; i < stars.length; i++) {
			int millis = _p5.millis() - millisAtSpawn[i];

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				_p5.line(newX*0.98f , newY*0.98f - 50,newX,newY-50);
				//_p5.point(newX, newY);
				//_parent.point(newX, newY);
			} else {
				generateVector(i);
			}
		}
		
		_p5.hint(PConstants.ENABLE_DEPTH_MASK);		
	}
	
	

	
}
