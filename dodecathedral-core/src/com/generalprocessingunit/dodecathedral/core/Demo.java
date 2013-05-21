package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

public class Demo {
	private static PApplet _p5;
	private static IDodecathedral _parent;

	static boolean playing = false;
	private static DeltaSequence _sequence;
	private static float _bpm = 60;
	private static int _sequencePosition = -1;
	private static boolean _loop = false;
	private static int _millisAtNoteLastPlayed = 0;
    private static int delay = 0;

    private static boolean playedOnce = false;

	public Demo(IDodecathedral p) {
		_parent = p;
		_p5 = p.getPApplet();
	}

	static void setSequence(DeltaSequence sequence) {
		_sequence = sequence;
		reset();
	}

    static void setBpm(float bpm) {
		_bpm = bpm;
	}

    static void setLoop(boolean loop) {
		_loop = loop;
	}

	public static void playSequence() {
		final int minElapsedMillis = (int) (1000 / (_bpm / 60));
		final int millisNow = _p5.millis();

        // delay before we set the current note
        if(delay > millisNow){
            return;
        }

		// -1 is the reset position
		if (_sequencePosition == -1) {
			DeltaHistory.setCurrentNote(0, _parent);
			_sequencePosition = 0;

            if(playedOnce  && !_loop){
                playing = false;
                playedOnce = false;

                // this fixes the dodecahedron going crazy when switching back to free_play
                _parent.getMultiTouch()[0].millisAtLastMove = _p5.millis();
                _parent.getMultiTouch()[1].millisAtLastMove = _p5.millis();
                Modes.switchMode(Modes.Mode.FREE_PLAY);
                return;
            }
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
				_millisAtNoteLastPlayed = millisNow;
				if (delta >= 0) {
					_parent.singleTap();
				} else {
					_parent.doubleTap();
				}

				_sequencePosition++;
			}
		}

		// if we've reached the end of the sequence either loop or stop playing
		if (_sequencePosition == _sequence.deltas.size()) {
			reset();
            playedOnce = true;
            return;
		}

		playing = true;
	}

	static void reset() {
		_sequencePosition = -1;
        delay = _p5.millis() + 3000;
	}

	static boolean rotateDodecahedron(int pentagon, float zRotVelocity, float xRotVelocity) {
		boolean inPosition = true;

		//we don't want to spin the z axis for the top and bottom pentagons
		if(pentagon != 0 && pentagon != 6){
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
