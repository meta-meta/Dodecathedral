package com.generalprocessingunit.dodecathedral.core.exercises;

import com.generalprocessingunit.dodecathedral.core.*;

import java.util.HashMap;
import java.util.Map;

public class Exercises {
    static Map<String, IExercise> exerciseLibrary;

    public static Boolean running = false;
    private static IExercise _currentExercise;

    private static int _noteCountAtInputStart;
    private static int _noteCountAtCheck;

    static {
        exerciseLibrary = new HashMap<String, IExercise>();
        for (DeltaSequenceCollection deltaSequenceCollection : DeltaSequenceLibrary.getDeltaSequenceLibraryValues()) {

            //add an exercise that encapsulates this collection
            IExercise exercise = new DefaultExercise(deltaSequenceCollection, true);
            exerciseLibrary.put(deltaSequenceCollection.name, exercise);

            //add an exercise for each individual sequence (why not)
            for (DeltaSequence deltaSequence : deltaSequenceCollection.values()) {
                exercise = new DefaultExercise(new DeltaSequenceCollection(deltaSequence), true);
                exerciseLibrary.put(deltaSequence.name, exercise);
            }
        }
    }

    private Exercises() {}

    public static void setExercise(IExercise exercise) {
        _currentExercise = exercise;
        _currentExercise.reset();
    }

    public static void setExercise(String exerciseKey){
        setExercise(exerciseLibrary.get(exerciseKey));
    }

    public static void runExercise() {
        running = true;

        // wait for the demo to finish playing
        if (Modes.currentMode == Modes.Mode.DEMO_PLAYING) {
            return;
        }

        // we for a message to be cleared
        if (Modes.currentMode == Modes.Mode.MESSAGE) {
            return;
        }

        // cycle through the instructional messages for this exercise
        if (_currentExercise.showExerciseMessage()) {
            return;
        }

        // if we haven't shown the message for the current sequence, show it
        if (_currentExercise.showSequenceMessage()) {
            return;
        }

        // if we haven't played the demo for the current sequence, play it
        if (_currentExercise.playDemo()) {
            return;
        }

        // we've played the demo for this sequence. now, change mode to INPUT so
        // we can check what the user is playing against the sequence
        if (Modes.currentMode != Modes.Mode.INPUT) {
            _noteCountAtInputStart = DeltaHistory.noteCount;
            Modes.switchMode(Modes.Mode.INPUT);
        }

        // nothing to check
        if (DeltaHistory.noteCount == _noteCountAtCheck) {
            return;
        }

        _currentExercise.monitorInput(_noteCountAtInputStart);
        _noteCountAtCheck = DeltaHistory.noteCount;
    }
}
