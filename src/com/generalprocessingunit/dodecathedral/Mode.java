package com.generalprocessingunit.dodecathedral;

public class Mode {
	public static Modes currentMode;
	public static Modes prevMode;
	
	public enum Modes {
		DEMO_PLAYING, INPUT, FREE_PLAY, MENU
	}

	//not sure how to work this yet, but at least this is how switching to the Menu should work
	//if we are already in Menu mode, and switch with Menu, it should go back whatever it was doing before we went to the Menu
	public static void switchMode(Modes mode) {
			
		if (currentMode == mode) {
			currentMode = prevMode;
			return;
		}

		prevMode = currentMode;
		currentMode = mode;
	}
}
