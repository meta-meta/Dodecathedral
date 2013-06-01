package com.generalprocessingunit.dodecathedral.core.exercises;

import com.generalprocessingunit.dodecathedral.core.DeltaSequence;
import com.generalprocessingunit.dodecathedral.core.DeltaSequenceCollection;
import com.generalprocessingunit.dodecathedral.core.Message;
import com.generalprocessingunit.dodecathedral.core.UserData;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Paul
 * Date: 5/12/13
 * Time: 7:32 PM
 */
public class Simon extends AbstractExercise{
    public Simon(int length){
        super(getNewDeltaSequenceCollection(length), true);
    }

    @Override
    void completeExercise() {
        int sequenceLength = currentSequence.deltas.size();
        if (UserData.data.longestRandomSequencePlayed < sequenceLength) {
            Message.showMessage("Way to go! New record!!!! You played a sequence of length " + sequenceLength, Message.MessageType.PRAISE);
            UserData.data.longestRandomSequencePlayed = sequenceLength;
            UserData.save();
        }

        //instead of stopping, we're just going to start a new random exercise that's longer
        //this happens till the user quits exercise from the menu
        Exercises.setExercise(new Simon(sequenceLength + 1));
    }

    static DeltaSequenceCollection getNewDeltaSequenceCollection(int length) {
        List<Integer> deltas = new ArrayList<Integer>();
        deltas.add(0);
        for (int i = 1; i < length; i++) {
            deltas.add(PApplet.floor((float) Math.random() * 12f));
        }

        return new DeltaSequenceCollection(new DeltaSequence(deltas, "Try this."));
    }
}
