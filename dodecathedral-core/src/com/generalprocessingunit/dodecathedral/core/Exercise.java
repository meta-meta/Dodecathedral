package com.generalprocessingunit.dodecathedral.core;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 3/20/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
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

    public enum ExerciseType{
        NORMAL, RANDOM;
    }
}
