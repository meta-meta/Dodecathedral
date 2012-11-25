package com.generalprocessingunit.dodecathedral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;

/**
 * Contains all the melodies that will be used for gameplay.
 * 
 * @author Paul M. Christian
 * 
 */
public class DeltaSequences {
	Map<String, DeltaSequenceCollection> deltaSequenceLibrary;

	DeltaSequences(Dodecathedral parent) throws XmlPullParserException, IOException {
		deltaSequenceLibrary = new HashMap<String, DeltaSequenceCollection>();
		XmlResourceParser parser = parent.getResources().getXml(com.generalprocessingunit.dodecathedral.R.xml.deltas);

		int eventType = parser.getEventType();

		Map<String, DeltaSequence> deltaSequenceCollection = new LinkedHashMap<String, DeltaSequence>();
		DeltaSequence deltaSequence = new DeltaSequence();
		String collectionName = new String();
		String name = new String();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			if (eventType == XmlPullParser.START_TAG) {
				if (tagName.equals("delta-sequence-collection")) {
					deltaSequenceCollection = new LinkedHashMap<String, DeltaSequence>();
				} else if (tagName.equals("collection-name")) {
					collectionName = parser.nextText();					
				} else if (tagName.equals("delta-sequence")) {
					deltaSequence = new DeltaSequence();
				} else if (tagName.equals("name")) {
					name = parser.nextText();
					deltaSequence.name = name;
				} else if (tagName.equals("deltas")) {
					deltaSequence.setDeltas(parser.nextText());
				} else if (tagName.equals("rhythm")) {
					deltaSequence.setRhythm(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (tagName.equals("delta-sequence")) {
					deltaSequenceCollection.put(name, deltaSequence);
				} else if (tagName.equals("delta-sequence-collection")) {
					deltaSequenceLibrary.put(collectionName, new DeltaSequenceCollection(deltaSequenceCollection, collectionName));
				}				
			}
			eventType = parser.next();
		}
	}

	public class DeltaSequence {
		List<Integer> deltas;
		List<Float> rhythm;
		String name;

		static final String delimiter = ",";

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
}
