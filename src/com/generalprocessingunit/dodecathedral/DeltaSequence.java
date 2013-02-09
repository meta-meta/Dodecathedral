package com.generalprocessingunit.dodecathedral;

import java.util.ArrayList;
import java.util.List;

public class DeltaSequence {
	List<Integer> deltas;
	List<Float> rhythm;
	String name;
	String message = "Let's play %s"; //default message to be displayed for a deltasequence in an exercise

	static final String delimiter = ",";

	public DeltaSequence(List<Integer> deltas, String message){
		this.deltas = deltas;	
		setRhythm("");
		this.message = message;
	}
	
	public DeltaSequence() {
		//default constructor
	}

	/**
	 * Parses a {@value #delimiter} delimited string of integers
	 * representing musical intervals that comprise a melody.
	 * 
	 * @param deltas
	 *            The string to be parsed.
	 */
	void setDeltas(String deltas) {
		String[] ss = deltas.split(delimiter);

		this.deltas = new ArrayList<Integer>();

		for (String s : ss) {
			this.deltas.add(Integer.parseInt(s));
		}
	}
	
	void setName(String name){
		this.name = name;
		message = String.format(message, name);
	}

	/**
	 * Parses a {@value #delimiter} delimited string of floats representing
	 * the number of beats that must elapse before playing the next note in
	 * the sequence.
	 * 
	 * @param rhythm
	 *            The string to be parsed.
	 */
	void setRhythm(String rhythm) {
		this.rhythm = new ArrayList<Float>();

		// this is the delay before playing the first note
		this.rhythm.add(0f);

		if (rhythm.length() == 0) {
			for (int i = 0; i < deltas.size(); i++) {
				this.rhythm.add(1f);
			}
		} else {
			String[] ss = rhythm.split(delimiter);
			for (String s : ss) {
				this.rhythm.add(Float.parseFloat(s));
			}
		}
	}
}