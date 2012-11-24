package com.generalprocessingunit.dodecathedral;

public class Modes {
	public static Mode currentMode;
	public static Mode prevMode;
	
	public enum Mode {
		DEMO_PLAYING, INPUT, FREE_PLAY, MENU, MESSAGE
	}

	//not sure how to work this yet, but at least this is how switching to the Menu should work
	//if we are already in Menu mode, and switch with Menu, it should go back to whatever it was doing before we went to the Menu
	public static void switchMode(Mode mode) {
			
		if (currentMode == mode) {
			currentMode = prevMode;
			return;
		}

		prevMode = currentMode;
		currentMode = mode;
	}
}
