package com.generalprocessingunit.dodecathedral;

import java.util.LinkedHashMap;
import java.util.Map;

import com.generalprocessingunit.dodecathedral.DeltaSequences.DeltaSequence;

public class DeltaSequenceCollection extends LinkedHashMap<String, DeltaSequence>
{
	String name;

	public DeltaSequenceCollection(Map<? extends String, ? extends DeltaSequence> map, String name) {
		super(map);
		this.name = name;
	}
	
	/**Constructor for making a DeltaSequenceCollection consisting of a single sequence
	 * @param deltaSequence
	 */
	public DeltaSequenceCollection(DeltaSequence deltaSequence) {
		super();
		this.put(deltaSequence.name, deltaSequence);			
		this.name = deltaSequence.name;
	}	
}