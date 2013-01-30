package com.generalprocessingunit.dodecathedral;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.generalprocessingunit.dodecathedral.DeltaSequences.DeltaSequence;
import com.generalprocessingunit.dodecathedral.Message.MessageType;
import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class Exercises {
	private static Dodecathedral _parent;
	Map<String, Exercise> exerciseLibrary;
	
	static Boolean running = false;
	private Exercise _currentExercise;
	private Iterator<?> _messageIterator;
	private Iterator<?> _sequenceIterator;	
	private DeltaSequence _currentSequence;	
	private int _currentSequenceIndex = -1;
	private boolean[] _demoPlayed;
	private boolean[] _messageShown;
	private int _noteCountAtInputStart;

	/**Everything to do with Exercises. To run an exercise, must call setExercise() first then runExercise()
	 * @param parent
	 */
	Exercises(Dodecathedral parent) {
		_parent = parent;
		exerciseLibrary = new HashMap<String, Exercise>();		
		
		for (DeltaSequenceCollection deltaSequenceCollection : _parent.deltaSequences.values())
		{
			//add an exercise that encapsulates this collection
			Exercise exercise = new Exercise(deltaSequenceCollection, true);
			exerciseLibrary.put(deltaSequenceCollection.name, exercise);
						
			//add an exercise for each individual sequence (why not)
			for (DeltaSequence deltaSequence : deltaSequenceCollection.values()) {				
				exercise = new Exercise(new DeltaSequenceCollection(deltaSequence), true);				
				exerciseLibrary.put(deltaSequence.name, exercise);
			}
		}
	}

	void setExercise(Exercise exercise) {
		_currentExercise = exercise;
		_sequenceIterator = exercise.deltaSequenceCollection.values().iterator();
		_currentSequence = (DeltaSequence)_sequenceIterator.next();
		_currentSequenceIndex = 0;
		_messageIterator = exercise.deltaSequenceCollection.messages.iterator();
		_demoPlayed = new boolean[_currentExercise.deltaSequenceCollection.size()];
		_messageShown = new boolean[_currentExercise.deltaSequenceCollection.size()];
	}

	void runExercise() {
		running = true;
		
		// wait for the demo to finish playing
		if (Modes.currentMode == Mode.DEMO_PLAYING) {
			return;
		}
		
		//we might be showing a rejection message
		if (Modes.currentMode == Mode.MESSAGE) {
			return;
		}
		
		//cycle through the instructional messages for this exercise
		if(_messageIterator.hasNext())
		{
			Modes.switchMode(Mode.FREE_PLAY); //just something to switch back to that's not menu
			_parent.message.showMessage((String)(_messageIterator.next()), MessageType.INSTRUCTION);
			return;
		}
		
		// if we haven't shown the message for the current sequence, show it
		if (!_messageShown[_currentSequenceIndex]) {
			_messageShown[_currentSequenceIndex] = true;
			Modes.switchMode(Mode.FREE_PLAY); //just something to switch back to that's not menu
			_parent.message.showMessage((String)(_currentSequence.message), MessageType.INFORMATION);
			return;
		}

		// if we haven't played the demo for the current sequence, play it
		if (!_demoPlayed[_currentSequenceIndex]) {			
			_parent.demo.setSequence(_currentSequence);
			_demoPlayed[_currentSequenceIndex] = true;
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
			_parent.demo.setSequence(_currentSequence);			
			Modes.switchMode(Mode.DEMO_PLAYING);
			_parent.message.showMessage("You fucked up. Try Again.", MessageType.REJECTION);
			return;
		}

		int numNotesPlayed = _parent.deltaHistory.noteCount - _noteCountAtInputStart;
		if (numNotesPlayed == _currentSequence.deltas.size()) {
			// WOW! You played the sequence!!!!
			if (_sequenceIterator.hasNext()) {
				_parent.message.showMessage(String.format("WOW! You played %s!!", _currentSequence.name), MessageType.PRAISE);
				_currentSequenceIndex++;
				_currentSequence = (DeltaSequence)_sequenceIterator.next();				
			} else {				
				// Exercise Complete!
				running = false;
				_parent.message.showMessage("WOW! You've completed the exercise!!!!", MessageType.PRAISE);
			}
		}
	}

	private boolean checkNotesPlayed() {
		int numNotesPlayed = _parent.deltaHistory.noteCount - _noteCountAtInputStart;		
		for (int i = 0; i < numNotesPlayed; i++) {
			// consult the deltaHistory to see if the most recent set of notes played matches up to the sequence
			// deltaHistory is in reverse order (most recent delta played is index 0)
			if (_currentSequence.deltas.get(i) != _parent.deltaHistory.deltas[(numNotesPlayed - 1) - i]) {
				return false;
			}
		}
		return true;
	}

	public class Exercise {
		DeltaSequenceCollection deltaSequenceCollection;
		boolean playDemo;
		float demoBpm = 120;
		
		Exercise(DeltaSequenceCollection deltaSequenceCollection, boolean playDemo){
			this.deltaSequenceCollection = deltaSequenceCollection;
			this.playDemo = playDemo;
		}
	}
}
