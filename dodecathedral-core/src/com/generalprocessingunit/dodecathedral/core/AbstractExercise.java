package com.generalprocessingunit.dodecathedral.core;

import java.util.Iterator;

/**
 * Author: Paul
 * Date: 5/12/13
 * Time: 3:34 PM
 */
public abstract class AbstractExercise implements IExercise{
    DeltaSequenceCollection deltaSequenceCollection;
    Iterator<?> sequenceIterator;
    Iterator<?> messageIterator;
    DeltaSequence currentSequence;
    int currentSequenceIndex = -1;

    boolean[] messageShown;
    boolean[] demoPlayed;

    boolean playDemo;

    AbstractExercise(DeltaSequenceCollection deltaSequenceCollection, boolean playDemo){
        this.deltaSequenceCollection = deltaSequenceCollection;
        this.playDemo = playDemo;
    }

    @Override
    public void reset() {
        currentSequenceIndex = 0;
        messageShown = new boolean[deltaSequenceCollection.size()];
        demoPlayed = new boolean[deltaSequenceCollection.size()];
        sequenceIterator = deltaSequenceCollection.values().iterator();
        messageIterator = deltaSequenceCollection.messages.iterator();
        currentSequence = (DeltaSequence)sequenceIterator.next();
    }

    @Override
    public boolean showExerciseMessage() {
        if(messageIterator.hasNext()){
            Modes.switchMode(Modes.Mode.FREE_PLAY); //just something to switch back to that's not menu
            Message.showMessage((String) (messageIterator.next()), Message.MessageType.INSTRUCTION);
            return true;
        }
        return false;
    }

    @Override
    public boolean showSequenceMessage() {
        if (!messageShown[currentSequenceIndex]) {
            messageShown[currentSequenceIndex] = true;
            Modes.switchMode(Modes.Mode.FREE_PLAY); //just something to switch back to that's not menu
            Message.showMessage(currentSequence.message, Message.MessageType.INFORMATION);
            return true;
        }
        return false;
    }

    @Override
    public boolean playDemo() {
        if (!demoPlayed[currentSequenceIndex]) {
            demoPlayed[currentSequenceIndex] = true;
            Demo.setSequence(currentSequence);
            Modes.switchMode(Modes.Mode.DEMO_PLAYING);
            return true;
        }
        return false;
    }

    @Override
    public void monitorInput(int noteCountAtInputStart) {
        if (!checkNotesPlayed(noteCountAtInputStart)) {
            // You fucked up.
            Demo.setSequence(currentSequence);
            Modes.switchMode(Modes.Mode.DEMO_PLAYING);
            Message.showMessage("You fucked up. Try Again.", Message.MessageType.REJECTION);
            return;
        }

        int numNotesPlayed = DeltaHistory.noteCount - noteCountAtInputStart;
        if (numNotesPlayed == currentSequence.deltas.size()) {
            // WOW! You played the sequence!!!!
            if (sequenceIterator.hasNext()) {
                Message.showMessage(String.format("WOW! You played %s!!", currentSequence.name), Message.MessageType.PRAISE);
                currentSequenceIndex++;
                currentSequence = (DeltaSequence) sequenceIterator.next();
            } else {
                // Exercise Complete!
                completeExercise();
            }
        }
    }

    private boolean checkNotesPlayed(int noteCountAtInputStart) {
        int numNotesPlayed = DeltaHistory.noteCount - noteCountAtInputStart;

        for (int i = 0; i < numNotesPlayed; i++) {
            // consult the deltaHistory to see if the most recent set of notes played matches up to the sequence
            // deltaHistory is in reverse order (most recent delta played is index 0)
            if (currentSequence.deltas.get(i) != DeltaHistory.deltas[(numNotesPlayed - 1) - i]) {
                return false;
            }
        }
        return true;
    }

    void completeExercise() {
        Message.showMessage("WOW! You've completed the exercise!!!!", Message.MessageType.PRAISE);
        Exercises.running = false;
    }
}