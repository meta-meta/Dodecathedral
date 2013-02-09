package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;

import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class Demo {

	private Dodecathedral _parent;

	boolean playing = false;
	private DeltaSequence _sequence;
	private float _bpm = 120;
	private int _sequencePosition = -1;
	private boolean _loop = false;
	private int _millisAtNoteLastPlayed = 0;

	Demo(PApplet parent) {
		_parent = (Dodecathedral) parent;
	}

	void setSequence(DeltaSequence sequence) {
		_sequence = sequence;
		reset();
	}

	void setBpm(float bpm) {
		_bpm = bpm;
	}

	void setLoop(boolean loop) {
		_loop = loop;
	}

	void playSequence() {
		final int minElapsedMillis = (int) (1000 / (_bpm / 60));
		final int millisNow = _parent.millis();

		// -1 is the reset position
		if (_sequencePosition == -1) {
			_parent.deltaHistory.currentNote = 0;
			_sequencePosition = 0;
		}

		int elapsedMillis = millisNow - _millisAtNoteLastPlayed;

		if (playing) {
			// pause for a moment after note played before rotating
			if (elapsedMillis < (minElapsedMillis * _sequence.rhythm.get(_sequencePosition)) / 4) {
				return;
			}

			int delta = _sequence.deltas.get(_sequencePosition);

			// get the dodecahedron in place then play the note
			if (rotateDodecahedron(PApplet.abs(delta), 0.3f, 0.1f) && elapsedMillis > minElapsedMillis * _sequence.rhythm.get(_sequencePosition)) {
				Dodecahedron.millisAtTap = _parent.millis();
				_millisAtNoteLastPlayed = millisNow;
				if (delta >= 0) {
					Dodecahedron.tap = 1;
					_parent.singleTap();
				} else {
					Dodecahedron.tap = 2;
					_parent.doubleTap();
				}

				_sequencePosition++;
			}
		}

		// if we've reached the end of the sequence either loop or stop playing
		if (_sequencePosition == _sequence.deltas.size()) {
			_parent.deltaHistory.currentNote = 0;
			if (!_loop) {
				playing = false;
				reset();
				Modes.switchMode(Mode.FREE_PLAY);

				// TODO make this less hacky. this should fix the dodecahedron
				// going crazy when switching back to free_play
				_parent.mt[0].millisAtLastMove = _parent.millis();
				_parent.mt[1].millisAtLastMove = _parent.millis();
				return;
			} else {
				_sequencePosition = -1;
			}
		}

		playing = true;

	}

	void reset() {
		_sequencePosition = -1;
	}

	boolean rotateDodecahedron(int pentagon, float zRotVelocity, float xRotVelocity) {
		boolean inPosition = true;

		if (!(getRotationalDistance(Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]) <= zRotVelocity * 2)) {
			Dodecahedron.zRot += getRotationVelocity(zRotVelocity, Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]);
			inPosition = false;
		} else if (!(getRotationalDistance(Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]) <= zRotVelocity)) {
			Dodecahedron.zRot += getRotationVelocity(zRotVelocity, Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]) / 2;
			inPosition = false;
		} else if (!(getRotationalDistance(Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]) <= zRotVelocity / 2)) {
			Dodecahedron.zRot += getRotationVelocity(zRotVelocity, Dodecahedron.zRot, Dodecahedron.zRotLookup[pentagon]) / 4;
			inPosition = false;
		}

		if (!(getRotationalDistance(Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]) <= xRotVelocity * 2)) {
			Dodecahedron.xRot += getRotationVelocity(xRotVelocity, Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]);
			inPosition = false;
		} else if (!(getRotationalDistance(Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]) <= xRotVelocity)) {
			Dodecahedron.xRot += getRotationVelocity(xRotVelocity, Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]) / 2;
			inPosition = false;
		}
		if (!(getRotationalDistance(Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]) <= xRotVelocity / 2)) {
			Dodecahedron.xRot += getRotationVelocity(xRotVelocity, Dodecahedron.xRot, Dodecahedron.xRotLookup[pentagon]) / 4;
			inPosition = false;
		}

		return inPosition;
	}

	static float getRotationalDistance(float currentRotation, float finalRotation) {
		currentRotation %= PApplet.TWO_PI;
		finalRotation %= PApplet.TWO_PI;

		currentRotation = (currentRotation < 0) ? currentRotation + PApplet.TWO_PI : currentRotation;
		finalRotation = (finalRotation < 0) ? finalRotation + PApplet.TWO_PI : finalRotation;

		return PApplet.abs(currentRotation - finalRotation);
	}

	// get the velocity for the quickest direction to move around a circle
	static float getRotationVelocity(float velocity, float currentRotation, float finalRotation) {

		currentRotation %= PApplet.TWO_PI;
		finalRotation %= PApplet.TWO_PI;

		currentRotation = (currentRotation < 0) ? currentRotation + PApplet.TWO_PI : currentRotation;
		finalRotation = (finalRotation < 0) ? finalRotation + PApplet.TWO_PI : finalRotation;

		if (getRotationalDistance(currentRotation, finalRotation) < PApplet.PI) {
			return (finalRotation - currentRotation > 0) ? velocity : -velocity;
		} else {
			return (finalRotation - currentRotation > 0) ? -velocity : velocity;
		}
	}

}
