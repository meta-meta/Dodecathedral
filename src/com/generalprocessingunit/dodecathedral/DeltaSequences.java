package com.generalprocessingunit.dodecathedral;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;

/**
 * Contains all the melodies that will be used for gameplay.
 * 
 * @author Paul M. Christian
 * 
 */
@SuppressWarnings("serial")
public class DeltaSequences extends HashMap<String, DeltaSequenceCollection> {
	DeltaSequences(Dodecathedral parent) throws XmlPullParserException, IOException {
		super();		
		XmlResourceParser parser = parent.getResources().getXml(com.generalprocessingunit.dodecathedral.R.xml.deltas);

		int eventType = parser.getEventType();

		DeltaSequenceCollection deltaSequenceCollection = new DeltaSequenceCollection();
		DeltaSequence deltaSequence = new DeltaSequence();			
		Boolean inDeltaSequence = false;		
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();			

			if (eventType == XmlPullParser.START_TAG) {
				if (tagName.equals("delta-sequence-collection")) {
					deltaSequenceCollection = new DeltaSequenceCollection();
					deltaSequenceCollection.name = parser.getAttributeValue(null, "name");
				} else if (tagName.equals("delta-sequence")) {
					deltaSequence = new DeltaSequence();
					deltaSequence.setName(parser.getAttributeValue(null, "name"));
					inDeltaSequence = true;								
				} else if (tagName.equals("message")) {
					if(inDeltaSequence){
						deltaSequence.message = parser.nextText();
					}
					else
					{
						deltaSequenceCollection.messages.add(parser.nextText());
					}
				} else if (tagName.equals("deltas")) {
					deltaSequence.setDeltas(parser.nextText());
				} else if (tagName.equals("rhythm")) {
					deltaSequence.setRhythm(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (tagName.equals("delta-sequence")) {
					deltaSequenceCollection.put(deltaSequence.name, deltaSequence);
					inDeltaSequence = false;							
				} else if (tagName.equals("delta-sequence-collection")) {
					this.put(deltaSequenceCollection.name, deltaSequenceCollection);					
				}				
			}
			eventType = parser.next();
		}
	}
}
