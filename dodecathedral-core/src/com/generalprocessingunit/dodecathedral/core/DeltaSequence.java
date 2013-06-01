package com.generalprocessingunit.dodecathedral.core;

import java.util.ArrayList;
import java.util.List;

public class DeltaSequence {
    public List<Integer> deltas;
    List<Float> rhythm;
    public String name;
    public String message = "Let's play %s"; //default message to be displayed for a deltasequence in an exercise

    public DeltaSequence(String name, List<Integer> deltas, List<Float> rhythm, String message) {
        this.deltas = deltas;
        setRhythm(rhythm);
        if(null != message){
            this.message = message;
        }
        setName(name);
    }

    public DeltaSequence(String name, List<Integer> deltas, String message) {
        this(name, deltas, null, message);
    }

    public DeltaSequence(String name, List<Integer> deltas,  List<Float> rhythm) {
        this(name, deltas, rhythm, null);
    }

    public DeltaSequence(String name, List<Integer> deltas) {
        this(name, deltas, null, null);
    }

    public DeltaSequence(List<Integer> deltas, String message) {
        this(null, deltas, null, message);
    }

    public DeltaSequence() {
        //default constructor
    }

    void setName(String name) {
        this.name = name;
        message = String.format(message, name);
    }

    void setRhythm(List<Float> rhythm) {
        this.rhythm = new ArrayList<Float>();

        // this is the delay before playing the first note
        this.rhythm.add(0f);

        if (null == rhythm) {
            for (int i = 0; i < deltas.size(); i++) {
                this.rhythm.add(1f);
            }
        } else {
            this.rhythm.addAll(rhythm);
        }
    }
}