package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

/**
 * Draws a map that displays the current note on a clock face. Trails are drawn
 * that indicate the direction of movement from the last note to the current
 * one.
 * 
 * @author Paul M. Christian
 * 
 */
public class Stars {
    private static boolean _starfieldOn = true;
    private static PShape stars;

	private Stars() {}

    static void createGeometry(PApplet p5){
        stars = p5.createShape();
        stars.beginShape(PConstants.POINTS);

        stars.noFill();
        stars.strokeWeight(5);

        for (int i = 0; i < 100; i++) {
            float u = p5.random(1);
            float v = p5.random(1);
            float theta = 2 * PConstants.PI * u;
            float phi = PApplet.acos(2 * v - 1);

            float r = p5.random(2200, 10000);
            float x = r * (PApplet.sin(phi) * PApplet.cos(theta));
            float y = r * (PApplet.sin(phi) * PApplet.sin(theta));
            float z = r * (PApplet.cos(phi));

            stars.vertex(x, y, z);
            float d = 1 - (r - 2200) / 10000;
            stars.stroke(p5.random(127, 255) * d, p5.random(127, 255) * d, p5.random(127, 255) * d);
            stars.strokeWeight(10);
        }
        stars.endShape();
    }


	public static void plot(PApplet p5) {
        if(!_starfieldOn)
            return;

        p5.pushMatrix();
        p5.rotateZ((p5.millis()/10000f) % PConstants.TWO_PI);
        p5.shape(stars);
        p5.pushMatrix();

        for  (int i=0; i< 10; i++)
        {
            p5.rotateZ(i);
            p5.rotateX(i*1.1f);

            p5.shape(stars);
        }

        p5.popMatrix();
        p5.popMatrix();
	}
	
	static boolean toggleStarfield(){
        _starfieldOn = !_starfieldOn;
        return _starfieldOn;
    }
}
