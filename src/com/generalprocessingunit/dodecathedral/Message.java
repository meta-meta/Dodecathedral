package com.generalprocessingunit.dodecathedral;

import com.generalprocessingunit.dodecathedral.Modes.Mode;
import processing.core.PFont;


public class Message {
	private Dodecathedral _parent;
	private MultiTouch[] _mt;
			
	private static final Color _fillColor = new Color(75, 50, 75, 200);
	private static final Color _strokeColor = new Color(255, 255, 255, 200);
	private static final float _borderStrokeWeight = 4;
	private static PFont _messageFont;
	private static MessageType _messageType;
	private static String _message;
	
	Message(Dodecathedral parent){
		_parent = parent;
		_mt = parent.mt;
		loadFonts();		
	}
	
	private void loadFonts() {
		_messageFont = _parent.createFont("Arial", (_parent.sketchHeight() / 10));		
	}

	void plot(float x, float y, float width, float height) {
		//draw message box
		_parent.fill(_fillColor.R, _fillColor.G, _fillColor.B, _fillColor.A);
		_parent.stroke(_strokeColor.R, _strokeColor.G, _strokeColor.B, _strokeColor.A);
		_parent.strokeWeight(_borderStrokeWeight);
		_parent.rect(x, y, width, height);

		//draw message text
		_parent.textFont(_messageFont);		
		_parent.fill(_messageType.messageColor().R, _messageType.messageColor().G, _messageType.messageColor().B, _messageType.messageColor().A);
		_parent.text(_message, x + width / 40, y + height / 40, width - width / 40 , height - height / 40);
		
		multiTouch();
	}
	
	private void multiTouch() {
		// we have a finger on the screen
		if (!_mt[0].touched) {
			// check if we have a tap
			final int maxMovementForTap = 10;
			if (_mt[0].totalMovement < maxMovementForTap && _mt[0].tap) {
				Modes.switchMode(Mode.MESSAGE);
				_mt[0].tap = false;
			}
		}		
	}
	
	void showMessage(String message, MessageType messageType){
		_messageType = messageType;
		_message = message;		
		Modes.switchMode(Mode.MESSAGE);
	}
	
	public enum MessageType {
		INFORMATION(new Color(255, 255, 255)), 
		INSTRUCTION(new Color(255, 255, 255)), 
		PRAISE(new Color(127, 255, 127)), 
		REJECTION(new Color(255, 127, 127));

		private final Color _messageColor;

		MessageType(Color messageColor) {
			_messageColor = messageColor;
		}

		private Color messageColor() {
			return _messageColor;
		}
	}
}
