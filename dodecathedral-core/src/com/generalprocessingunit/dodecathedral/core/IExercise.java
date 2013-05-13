package com.generalprocessingunit.dodecathedral.core;

/**
 * Author: Paul
 * Date: 5/12/13
 * Time: 3:21 PM
 */
public interface IExercise {
    boolean showExerciseMessage();

    void reset();

    boolean showSequenceMessage();

    boolean playDemo();

    void monitorInput(int noteCountAtInputStart);
}
