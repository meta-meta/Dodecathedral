package com.generalprocessingunit.dodecathedral.core;

/**
 * Stores the current note, manages navigating between notes with deltas
 * (intervals), stores a history of past notes, past intervals and times they
 * were played
 *
 * @author Paul M. Christian
 */
public class DeltaHistory {
    static final int historyLength = 100;

    static int[] notes;
    static int[] deltas;
    static int[] millis;
    static int noteCount = 0;
    static int currentNote;

    private DeltaHistory() {
    }

    static {
        currentNote = 0;
        notes = new int[historyLength];
        deltas = new int[historyLength];
        millis = new int[historyLength];
    }

    public static int getCurrentNote(){
        return currentNote;
    }

    public static void navigate(int selectedInterval, boolean up, int millis) {
        updateHistory(notes, currentNote);
        updateHistory(deltas, up ? selectedInterval : -selectedInterval);
        updateHistory(DeltaHistory.millis, millis);

        if (up) {
            currentNote = deltaAddition(currentNote, selectedInterval);

        } else {
            currentNote = deltaAddition(currentNote, -selectedInterval);
        }
        noteCount++;
    }

    private static void updateHistory(int[] hist, int n) {
        for (int i = historyLength - 1; i > 0; i--) {
            hist[i] = hist[i - 1];
        }

        hist[0] = n;
    }

    static int deltaAddition(int currentNote, int delta) {
        currentNote += delta;
        if (currentNote < 0) {
            currentNote = 12 + currentNote;
        }
        currentNote = currentNote % 12;
        return currentNote;
    }
}
