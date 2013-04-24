package com.generalprocessingunit.dodecathedral.core;

import java.util.*;

/**
 * Contains all the melodies that will be used for gameplay.
 *
 * @author Paul M. Christian
 */

public class DeltaSequenceLibrary {
    private static final Map<String, DeltaSequenceCollection> _library;

    static {
        /* NOTE: for now, due to the way the exercise library is built,
        delta sequences cannot have the same name as delta sequence collections*/
        Map<String, DeltaSequenceCollection> l = new HashMap<String, DeltaSequenceCollection>();

        DeltaSequenceCollection c;

        // Church Modes
        c = new DeltaSequenceCollection("Church Modes");
        c.messages.add("Let's play through the modes.");
        c.messages.add("The major scale is a sequence of ascending intervals.");
        c.messages.add("Each mode uses the same sequence only starting from a different spot.");
        c.messages.add("Note that the sequence is shifted by one place in each successive mode.");
        c.put("The Ionian Mode", new DeltaSequence("The Ionian Mode", Arrays.asList(0, 2, 2, 1, 2, 2, 2, 1), "Let's play the Ionian Mode, commonly referred to as the Major Scale"));
        c.put("The Dorian Mode", new DeltaSequence("The Dorian Mode", Arrays.asList(0, 2, 1, 2, 2, 2, 1, 2)));
        c.put("The Phrygian Mode", new DeltaSequence("The Phrygian Mode", Arrays.asList(0, 1, 2, 2, 2, 1, 2, 2)));
        c.put("The Lydian Mode", new DeltaSequence("The Lydian Mode", Arrays.asList(0, 2, 2, 2, 1, 2, 2, 1)));
        c.put("The Mixolydian Mode", new DeltaSequence("The Mixolydian Mode", Arrays.asList(0, 2, 2, 1, 2, 2, 1, 2)));
        c.put("The Aeolian Mode", new DeltaSequence("The Aeolian Mode", Arrays.asList(0, 2, 1, 2, 2, 1, 2, 2), "Let's play The Aeolian Mode, or \"Natural Minor Scale\""));
        c.put("The Locrian Mode", new DeltaSequence("The Locrian Mode", Arrays.asList(0, 1, 2, 2, 1, 2, 2, 2)));
        l.put(c.name, c);

        // Arpeggios
        c = new DeltaSequenceCollection("Arpeggios");
        c.messages.add("Let's play through some common arpeggios.");
        c.messages.add("Arpeggios are chords played one note at a time.");
        c.put("Major", new DeltaSequence("Major", Arrays.asList(0, 4, 3, 5)));
        c.put("Minor", new DeltaSequence("Minor", Arrays.asList(0, 3, 4, 5)));
        c.put("Diminished", new DeltaSequence("Diminished", Arrays.asList(0, 3, 3, 6)));
        c.put("Augmented", new DeltaSequence("Augmented", Arrays.asList(0, 4, 4, 4)));
        c.put("Major 7", new DeltaSequence("Major 7", Arrays.asList(0, 4, 3, 4, 1)));
        c.put("Minor 7", new DeltaSequence("Minor 7", Arrays.asList(0, 3, 4, 3, 2)));
        c.put("Dominant 7", new DeltaSequence("Dominant 7", Arrays.asList(0, 4, 3, 3, 2)));
        c.put("Minor Major 7", new DeltaSequence("Minor Major 7", Arrays.asList(0, 3, 4, 4, 1)));
        c.put("Diminished 7", new DeltaSequence("Diminished 7", Arrays.asList(0, 3, 3, 3, 3)));
        c.put("Half-diminished", new DeltaSequence("Half-diminished", Arrays.asList(0, 3, 3, 4, 2)));
        c.put("Augmented 7", new DeltaSequence("Augmented 7", Arrays.asList(0, 4, 4, 2, 2)));
        l.put(c.name, c);

        // Melodies
        c = new DeltaSequenceCollection("Melodies");
        c.put("Home Alone", new DeltaSequence("Home Alone",
                Arrays.asList(3, -3, 3, -3, 8, -5, -5, 7, -2, -7, 5, -1, -2, 5, -3, 3, -3, 8, -5, 2, 2, 1, -5, 2, 2, 1, -5, 2, 2, 1, -5, -7, 5, -1, -2, -2),
                Arrays.asList(2f, 2f, 2f, 2f, 4f, 4f, 2f, 2f, 2f, 1f, 1f, 4f, 4f, 2f, 2f, 2f, 2f, 4f, 6f, 0.5f, 0.5f, 1f, 6f, 0.5f, 0.5f, 1f, 6f, 0.5f, 0.5f, 1f, 2f, 1f, 1f, 4f, 4f, 8f),
                "Home Alone"));
        c.put("Jurassic Park", new DeltaSequence("Jurassic Park",
                Arrays.asList(5, -1, 1, -5, -2, 7, -1, 1, -5, -2, 7, -1, 1, 2, 0, 3, 0),
                Arrays.asList(1f, 1f, 2f, 2f, 2f, 1f, 1f, 2f, 2f, 2f, 1f, 1f, 3f, 1f, 3f, 1f, 3f)));
        c.put("Mozart", new DeltaSequence("Mozart",
                Arrays.asList(5, -5, 5, -5, 5, -5, 5, 4, 3, -2, -3, 3, -3, 3, -3, -3, 3, -7),
                Arrays.asList(1.5f, 0.5f, 1.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 2f, 1.5f, 0.5f, 1.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 2f)));
        l.put(c.name, c);


        _library = Collections.unmodifiableMap(l);
    }

    public static DeltaSequenceCollection getDeltaSequenceCollection(String key) {
        return _library.get(key);
    }

    public static Collection<DeltaSequenceCollection> getDeltaSequenceLibraryValues() {
        return _library.values();
    }
}
