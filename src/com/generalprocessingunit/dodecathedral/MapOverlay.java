package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Draws a map that displays the current note on a clock face. Trails are drawn
 * that indicate the direction of movement from the last note to the current
 * one.
 * 
 * @author Paul M. Christian
 * 
 */
public class MapOverlay {
	private Dodecathedral _parent;
	private static final float radsBetweenPearls = PConstants.PI / 6;
	private static final float piOverTwo = PConstants.PI / 2;

	private static final Color stroke = new Color(255, 255, 255, 127);
	private static final Color fill = new Color(127, 127, 127, 127);
	private static final int strokeWeight = 3;

	MapOverlay(Dodecathedral parent) {
		_parent = parent;
	}

	void plot(float x, float y, float size) {

		// draw the box
		_parent.fill(fill.R, fill.G, fill.B, fill.A);
		_parent.stroke(stroke.R, stroke.G, stroke.B, stroke.A);
		_parent.strokeWeight(strokeWeight);
		_parent.rect(x, y, size, size);

		float mapRadius = size / 2 - size / 8;

		_parent.strokeWeight(strokeWeight / 2);
		_parent.ellipseMode(PConstants.RADIUS);

		float pearlRadius = size / 12;
		for (int i = 0; i < 12; i++) {
			if (i == _parent.deltaHistory.currentNote) {
				_parent.fill(stroke.R, stroke.G, stroke.B, stroke.A);
			} else if (i == _parent.deltaHistory.notes[0]) {
				_parent.fill(stroke.R, stroke.G, stroke.B, stroke.A - (int) ((_parent.millis() - _parent.deltaHistory.millis[0]) / 5));
			} else {
				_parent.noFill();
			}
			drawPearl(i, x, y, size, mapRadius, pearlRadius);
		}

		// check to see if we moved by an interval more than a half step and if
		// so, draw a trail
		int delta = PApplet.abs(_parent.deltaHistory.deltas[0]);
		if (delta > 1) {			
			for (int i = 1; i < delta; i++) {
				int note = DeltaHistory.deltaAddition(_parent.deltaHistory.currentNote, -(_parent.deltaHistory.deltas[0] / delta) * i);

				_parent.noStroke();
				_parent.fill(0f, 127 - (int) ((_parent.millis() - _parent.deltaHistory.millis[0]) / (10 / i)));

				drawPearl(note, x, y, size, mapRadius, pearlRadius);
			}
		}

	}

	void drawPearl(int i, float x, float y, float size, float mapRadius, float pearlRadius) {
		float theta = i * radsBetweenPearls;
		float pX = x + size / 2 + (PApplet.cos(theta - piOverTwo) * mapRadius);
		float pY = y + size / 2 + (PApplet.sin(theta - piOverTwo) * mapRadius);
		_parent.ellipse(pX, pY, pearlRadius, pearlRadius);
	}
}
