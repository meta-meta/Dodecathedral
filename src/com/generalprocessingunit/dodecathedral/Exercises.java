package com.generalprocessingunit.dodecathedral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.generalprocessingunit.dodecathedral.DeltaSequences.DeltaSequence;
import com.generalprocessingunit.dodecathedral.Message.MessageType;
import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class Exercises {
	private Dodecathedral _parent;
	Map<String, Exercise> exerciseLibrary;
	private Exercise _currentExercise;
	Boolean running = false;
	private int _currentSequence = -1;
	private boolean[] _demoPlayed;
	private int _noteCountAtInputStart;

	Exercises(Dodecathedral parent) {
		_parent = parent;
		exerciseLibrary = new HashMap<String, Exercise>();
		// TODO replace with xml parsing

		// currently just one sequence per exercise, but the capability exists
		// to make an exerise that is a series of sequences		
		for (String deltaSequenceCollectionKey : parent.deltaSequences.deltaSequenceLibrary.keySet())
		{
			Map<String, DeltaSequence> deltaSequenceCollection = _parent.deltaSequences.deltaSequenceLibrary.get(deltaSequenceCollectionKey); 
			for (String key : deltaSequenceCollection.keySet()) {
				Exercise exercise = new Exercise();
				DeltaSequence seq = deltaSequenceCollection.get(key);
				exercise.deltaSequences = new ArrayList<DeltaSequence>();
				exercise.deltaSequences.add(seq);
				exercise.playDemo = true;
				exerciseLibrary.put(seq.name, exercise);
			}
		}
	}

	void setExercise(Exercise exercise) {
		_currentExercise = exercise;
		_currentSequence = 0;
		_demoPlayed = new boolean[_currentExercise.deltaSequences.size()];
	}

	void runExercise() {
		running = true;

		// Exercise Complete!
		if (_currentSequence == _currentExercise.deltaSequences.size()) {
			running = false;
			_parent.message.showMessage("WOW! You played the sequence!!!!", MessageType.PRAISE);
			return;
		}

		// wait for the demo to finish playing
		if (Modes.currentMode == Mode.DEMO_PLAYING) {
			return;
		}
		
		//we might be showing a rejection message
		if (Modes.currentMode == Mode.MESSAGE) {
			return;
		}

		// if we haven't played the demo for the current sequence, play it
		if (!_demoPlayed[_currentSequence]) {
			_parent.demo.setSequence(_currentExercise.deltaSequences.get(_currentSequence));
			_demoPlayed[_currentSequence] = true;
			Modes.switchMode(Mode.DEMO_PLAYING);
			return;
		}

		// we've played the demo for this sequence. now, change mode to INPUT so
		// we can check what the user is playing against the sequence
		if (Modes.currentMode != Mode.INPUT) {
			_noteCountAtInputStart = _parent.deltaHistory.noteCount;
			Modes.switchMode(Mode.INPUT);
		}

		if (!checkNotesPlayed()) {
			// You fucked up.
			_parent.demo.setSequence(_currentExercise.deltaSequences.get(_currentSequence));
			_demoPlayed[_currentSequence] = true;
			Modes.switchMode(Mode.DEMO_PLAYING);
			_parent.message.showMessage("You fucked up. Try Again", MessageType.REJECTION);
			return;
		}

		int numNotesPlayed = _parent.deltaHistory.noteCount - _noteCountAtInputStart;
		if (numNotesPlayed == _currentExercise.deltaSequences.get(_currentSequence).deltas.size()) {
			// WOW! You played the sequence!!!!
			_currentSequence++;
			Modes.switchMode(Mode.FREE_PLAY);			
		}

	}

	private boolean checkNotesPlayed() {
		int numNotesPlayed = _parent.deltaHistory.noteCount - _noteCountAtInputStart;
		DeltaSequence sequence = _currentExercise.deltaSequences.get(_currentSequence);
		for (int i = 0; i < numNotesPlayed; i++) {
			// consult the deltaHistory to see if the most recent set of notes
			// played matches up to the sequence
			// deltaHistory is in reverse order (most recent delta played is
			// index 0)
			if (sequence.deltas.get(i) != _parent.deltaHistory.deltas[(numNotesPlayed - 1) - i]) {
				return false;
			}
		}
		return true;
	}

	public class Exercise {
		List<DeltaSequence> deltaSequences;
		boolean playDemo;
		float demoBpm = 120;
	}
}
