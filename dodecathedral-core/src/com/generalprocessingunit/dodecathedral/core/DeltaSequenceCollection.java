package com.generalprocessingunit.dodecathedral.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class DeltaSequenceCollection extends LinkedHashMap<String, DeltaSequence>
{
	public String name;
	public ArrayList<String> messages;

	public DeltaSequenceCollection(String name) {
        super();
        this.name = name;
		messages = new ArrayList<String>();
	}
	
	public DeltaSequenceCollection(Map<? extends String, ? extends DeltaSequence> map, String name, ArrayList<String> messages) {
		super(map);
		this.name = name;
		this.messages = messages;
	}
	
	/**Constructor for making a DeltaSequenceCollection consisting of a single sequence
	 * @param deltaSequence
	 */
	public DeltaSequenceCollection(DeltaSequence deltaSequence) {
		super();
		this.put(deltaSequence.name, deltaSequence);			
		this.name = deltaSequence.name;
		messages = new ArrayList<String>();
	}
}