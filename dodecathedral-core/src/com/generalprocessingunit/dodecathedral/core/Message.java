package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PFont;


public class Message {
	private static final Color _fillColor = new Color(0, 0, 0, 180);
	//private static final Color _strokeColor = new Color(255, 255, 255, 200);
	//private static final float _borderStrokeWeight = 4;
	private static PFont _messageFont;
	private static MessageType _messageType;
	private static String _message;

    private  Message(){}

	
	public static void loadFonts(PApplet p5, char[] charset) {
		_messageFont = p5.createFont("Arial", 40, true, charset);
	}

	public static void plot(PApplet p5, float x, float y, float width, float height) {
		//draw message box
        p5.fill(_fillColor.R, _fillColor.G, _fillColor.B, _fillColor.A);
		//_parent.stroke(_strokeColor.R, _strokeColor.G, _strokeColor.B, _strokeColor.A);
		//_parent.strokeWeight(_borderStrokeWeight);
        p5.noStroke();
        p5.rect(x, y, width, height);

		//draw message text
        p5.textFont(_messageFont);
        p5.fill(_messageType.messageColor().R, _messageType.messageColor().G, _messageType.messageColor().B, _messageType.messageColor().A);
        p5.text(_message, x + width / 40, y + height / 40, width - width / 40 , height - height / 40);
	}
	
	public static void touchControl(MultiTouch[] mt) {
		// we have a finger on the screen
		if (!mt[0].touched) {
			// check if we have a tapIndicator
			final int maxMovementForTap = 10;
			if (mt[0].totalMovement < maxMovementForTap && mt[0].tap) {
				Modes.switchMode(Modes.Mode.MESSAGE);
				mt[0].tap = false;
			}
		}		
	}
	
	public static void showMessage(String message, MessageType messageType){
		_messageType = messageType;
		_message = message;		
		Modes.switchMode(Modes.Mode.MESSAGE);
	}
	
	public enum MessageType {
		INFORMATION(new Color(127, 127, 255)), 
		INSTRUCTION(new Color(255, 255, 127)), 
		PRAISE(new Color(50, 255, 50)), 
		REJECTION(new Color(255, 50, 50));

		private final Color _messageColor;

		MessageType(Color messageColor) {
			_messageColor = messageColor;
		}

		private Color messageColor() {
			return _messageColor;
		}
	}
}
