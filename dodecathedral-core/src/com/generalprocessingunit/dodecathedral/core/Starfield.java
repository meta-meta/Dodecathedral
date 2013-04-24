package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
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
    private static boolean _starfieldOn = true;
	

	private static PVector[] stars = new PVector[50];
	private static int[] millisAtSpawn = new int[50];
	
	private Starfield() {}

    private static void generateVectors(PApplet p5){
		for(int i=0; i< stars.length; i++){
			generateVector(p5, i);
		}
	}

	private static void generateVector(PApplet p5, int i){
		stars[i] = new PVector();

		float theta = (float)Math.random()*PConstants.TWO_PI;
		float v = (float)Math.random()/2 + 0.25f;
		stars[i].x = PApplet.cos(theta) * v;
		stars[i].y = PApplet.sin(theta) * v;

		millisAtSpawn[i] = p5.millis();
	}

	public static void plot(PApplet p5, PGraphics pg, float x, float y, float w, float h) {
        if(null == stars[0]){
            generateVectors(p5);
        }

        if(!_starfieldOn){
            pg.background(30, 0, 30);
            return;
        }


		pg.strokeWeight(5);

		for (int i = 0; i < stars.length/3; i++) {

			int millis = p5.millis() - millisAtSpawn[i];
            pg.stroke(0,i*10,i*10,100-millis/10);

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				pg.line(newX * 1.7f, newY * 1.7f - 50, newX, newY - 50);
				//p5.point(newX, newY);
			} else {
				generateVector(p5, i);
			}
		}
		
		//p5.stroke(50);
		pg.strokeWeight(3);
		
		for (int i = stars.length/3; i < 2 * stars.length/3 ; i++) {

			int millis = p5.millis() - millisAtSpawn[i];
            pg.stroke(i*10,i*10,0,100-millis/10);

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				pg.line(newX * 1.7f, newY * 1.7f - 50, newX, newY - 50);
				//p5.point(newX, newY);
				//_parent.point(newX, newY);
			} else {
				generateVector(p5, i);
			}
		}
		

		pg.strokeWeight(2);
		
		for (int i = 2 * stars.length/3; i < stars.length; i++) {

			int millis = p5.millis() - millisAtSpawn[i];
            pg.stroke(i*10,0,i*10,100-millis/10);

			float newX = x + (stars[i].x) * millis;
			float newY = y + (stars[i].y) * millis;

			if (Math.abs(newX) < w/2 && Math.abs(newY) < w/2) {
				
				pg.line(newX * 1.7f, newY * 1.7f - 50, newX, newY - 50);
				//p5.point(newX, newY);
				//_parent.point(newX, newY);
			} else {
				generateVector(p5, i);
			}
		}
		
	}
	
	static boolean toggleStarfield(){
        _starfieldOn = !_starfieldOn;
        return _starfieldOn;
    }
}
