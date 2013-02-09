package com.generalprocessingunit.dodecathedral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;

import com.generalprocessingunit.dodecathedral.Message.MessageType;
import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class Exercises {
	private static Dodecathedral _parent;
	static Map<String, Exercise> exerciseLibrary;
	
	static Boolean running = false;
	private static Exercise _currentExercise;
	private static Iterator<?> _messageIterator;
	private static Iterator<?> _sequenceIterator;	
	private static DeltaSequence _currentSequence;	
	private static int _currentSequenceIndex = -1;
	private static boolean[] _demoPlayed;
	private static boolean[] _messageShown;
	private static int _noteCountAtInputStart;
	
	private static int _noteCountAtCheck;
	
	/**Everything to do with Exercises. To run an exercise, must call setExercise() first then runExercise()
	 * @param parent
	 */
	Exercises(Dodecathedral parent) {
		_parent = parent;
		exerciseLibrary = new HashMap<String, Exercise>();		
		
		for (DeltaSequenceCollection deltaSequenceCollection : _parent.deltaSequences.values())
		{
			//add an exercise that encapsulates this collection
			Exercise exercise = new Exercise(deltaSequenceCollection, true, ExerciseType.NORMAL);
			exerciseLibrary.put(deltaSequenceCollection.name, exercise);
						
			//add an exercise for each individual sequence (why not)
			for (DeltaSequence deltaSequence : deltaSequenceCollection.values()) {				
				exercise = new Exercise(new DeltaSequenceCollection(deltaSequence), true, ExerciseType.NORMAL);				
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
	
	void setRandomExercise(int length){
		List<Integer> deltas = new ArrayList<Integer>();
		deltas.add(0);
		for(int i = 1; i< length; i++){
			deltas.add(PApplet.parseInt(_parent.random(12)));
		}
		
		Exercise exercise = new Exercise(new DeltaSequenceCollection(new DeltaSequence(deltas, "Try this.")), true, ExerciseType.RANDOM);
		setExercise(exercise);
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
				_currentExercise.exerciseType.completeExercise();
			}
		}
	}

	private boolean checkNotesPlayed() {
		int numNotesPlayed = _parent.deltaHistory.noteCount - _noteCountAtInputStart;	
		if(_parent.deltaHistory.noteCount == _noteCountAtCheck)
		{
			return true;
		}
		for (int i = 0; i < numNotesPlayed; i++) {
			// consult the deltaHistory to see if the most recent set of notes played matches up to the sequence
			// deltaHistory is in reverse order (most recent delta played is index 0)
			if (_currentSequence.deltas.get(i) != _parent.deltaHistory.deltas[(numNotesPlayed - 1) - i]) {
				return false;
			}
		}
		_noteCountAtCheck = _noteCountAtInputStart;
		return true;
	}
	
	public class Exercise {
		DeltaSequenceCollection deltaSequenceCollection;
		boolean playDemo;
		float demoBpm = 120;
		ExerciseType exerciseType;
		
		Exercise(DeltaSequenceCollection deltaSequenceCollection, boolean playDemo, ExerciseType exerciseType){
			this.deltaSequenceCollection = deltaSequenceCollection;
			this.playDemo = playDemo;
			this.exerciseType = exerciseType;
		}
	}
	
	public enum ExerciseType{
		NORMAL, RANDOM;
		
		void completeExercise(){
			switch (this){
			case NORMAL:
				running = false;
				_parent.message.showMessage("WOW! You've completed the exercise!!!!", MessageType.PRAISE);
				break;
			case RANDOM:
				int sequenceLength = _currentSequence.deltas.size();
				if(_parent.userData.data.longestRandomSequencePlayed < sequenceLength)
				{
					_parent.message.showMessage("Way to go! New record!!!! You played a sequence of length " + sequenceLength, MessageType.PRAISE);
					_parent.userData.data.longestRandomSequencePlayed = sequenceLength;
					_parent.userData.save();
				}
				//instead of stopping, we're just going to start a new random exercise that's longer
				//this happens till the user quits exercise from the menu
				_parent.exercises.setRandomExercise(sequenceLength + 1);
				break;			
			}
		}
	}
}
