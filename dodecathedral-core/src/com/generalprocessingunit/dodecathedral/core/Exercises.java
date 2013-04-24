package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

import java.util.*;

public class Exercises {
    static Map<String, Exercise> exerciseLibrary;

    public static Boolean running = false;
    private static Exercise _currentExercise;
    private static Iterator<?> _messageIterator;
    private static Iterator<?> _sequenceIterator;
    private static DeltaSequence _currentSequence;
    private static int _currentSequenceIndex = -1;
    private static boolean[] _demoPlayed;
    private static boolean[] _messageShown;
    private static int _noteCountAtInputStart;
    private static int _noteCountAtCheck;

    static {
        exerciseLibrary = new HashMap<String, Exercise>();
        for (DeltaSequenceCollection deltaSequenceCollection : DeltaSequenceLibrary.getDeltaSequenceLibraryValues()) {

            //add an exercise that encapsulates this collection
            Exercise exercise = new Exercise(deltaSequenceCollection, true, Exercise.ExerciseType.NORMAL);
            exerciseLibrary.put(deltaSequenceCollection.name, exercise);

            //add an exercise for each individual sequence (why not)
            for (DeltaSequence deltaSequence : deltaSequenceCollection.values()) {
                exercise = new Exercise(new DeltaSequenceCollection(deltaSequence), true, Exercise.ExerciseType.NORMAL);
                exerciseLibrary.put(deltaSequence.name, exercise);
            }
        }
    }

    private Exercises() {}

    static void setExercise(Exercise exercise) {
        _currentExercise = exercise;
        _sequenceIterator = exercise.deltaSequenceCollection.values().iterator();
        _currentSequence = (DeltaSequence) _sequenceIterator.next();
        _currentSequenceIndex = 0;
        _messageIterator = exercise.deltaSequenceCollection.messages.iterator();
        _demoPlayed = new boolean[_currentExercise.deltaSequenceCollection.size()];
        _messageShown = new boolean[_currentExercise.deltaSequenceCollection.size()];
    }

    static void setExercise(String exerciseKey){
        setExercise(exerciseLibrary.get(exerciseKey));
    }

    static void setRandomExercise(int length) {
        List<Integer> deltas = new ArrayList<Integer>();
        deltas.add(0);
        for (int i = 1; i < length; i++) {
            deltas.add(PApplet.floor( (float)Math.random() * 12f ));
        }

        setExercise(new Exercise(new DeltaSequenceCollection(new DeltaSequence(deltas, "Try this.")), true, Exercise.ExerciseType.RANDOM));
    }

    public static void runExercise() {
        running = true;

        // wait for the demo to finish playing
        if (Modes.currentMode == Modes.Mode.DEMO_PLAYING) {
            return;
        }

        //we might be showing a rejection message
        if (Modes.currentMode == Modes.Mode.MESSAGE) {
            return;
        }

        //cycle through the instructional messages for this exercise
        if (_messageIterator.hasNext()) {
            Modes.switchMode(Modes.Mode.FREE_PLAY); //just something to switch back to that's not menu
            Message.showMessage((String) (_messageIterator.next()), Message.MessageType.INSTRUCTION);
            return;
        }

        // if we haven't shown the message for the current sequence, show it
        if (!_messageShown[_currentSequenceIndex]) {
            _messageShown[_currentSequenceIndex] = true;
            Modes.switchMode(Modes.Mode.FREE_PLAY); //just something to switch back to that's not menu
            Message.showMessage((String) (_currentSequence.message), Message.MessageType.INFORMATION);
            return;
        }

        // if we haven't played the demo for the current sequence, play it
        if (!_demoPlayed[_currentSequenceIndex]) {
            Demo.setSequence(_currentSequence);
            _demoPlayed[_currentSequenceIndex] = true;
            Modes.switchMode(Modes.Mode.DEMO_PLAYING);
            return;
        }

        // we've played the demo for this sequence. now, change mode to INPUT so
        // we can check what the user is playing against the sequence
        if (Modes.currentMode != Modes.Mode.INPUT) {
            _noteCountAtInputStart = DeltaHistory.noteCount;
            Modes.switchMode(Modes.Mode.INPUT);
        }

        if (!checkNotesPlayed()) {
            // You fucked up.
            Demo.setSequence(_currentSequence);
            Modes.switchMode(Modes.Mode.DEMO_PLAYING);
            Message.showMessage("You fucked up. Try Again.", Message.MessageType.REJECTION);
            return;
        }

        int numNotesPlayed = DeltaHistory.noteCount - _noteCountAtInputStart;
        if (numNotesPlayed == _currentSequence.deltas.size()) {
            // WOW! You played the sequence!!!!
            if (_sequenceIterator.hasNext()) {
                Message.showMessage(String.format("WOW! You played %s!!", _currentSequence.name), Message.MessageType.PRAISE);
                _currentSequenceIndex++;
                _currentSequence = (DeltaSequence) _sequenceIterator.next();
            } else {
                // Exercise Complete!
                completeExercise();
            }
        }
    }

    private static boolean checkNotesPlayed() {
        int numNotesPlayed = DeltaHistory.noteCount - _noteCountAtInputStart;
        if (DeltaHistory.noteCount == _noteCountAtCheck) {
            return true;
        }
        for (int i = 0; i < numNotesPlayed; i++) {
            // consult the deltaHistory to see if the most recent set of notes played matches up to the sequence
            // deltaHistory is in reverse order (most recent delta played is index 0)
            if (_currentSequence.deltas.get(i) != DeltaHistory.deltas[(numNotesPlayed - 1) - i]) {
                return false;
            }
        }
        _noteCountAtCheck = _noteCountAtInputStart;
        return true;
    }

    static void completeExercise() {
        switch (_currentExercise.exerciseType) {
            case NORMAL:
                running = false;
                Message.showMessage("WOW! You've completed the exercise!!!!", Message.MessageType.PRAISE);
                break;
            case RANDOM:
                int sequenceLength = _currentSequence.deltas.size();
                if (UserData.data.longestRandomSequencePlayed < sequenceLength) {
                    Message.showMessage("Way to go! New record!!!! You played a sequence of length " + sequenceLength, Message.MessageType.PRAISE);
                    UserData.data.longestRandomSequencePlayed = sequenceLength;
                    UserData.save();
                }
                //instead of stopping, we're just going to start a new random exercise that's longer
                //this happens till the user quits exercise from the menu
                setRandomExercise(sequenceLength + 1);
                break;
        }
    }
}
