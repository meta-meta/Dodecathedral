package com.generalprocessingunit.dodecathedral;

import processing.core.PApplet;
import processing.core.PFont;

import com.generalprocessingunit.dodecathedral.Modes.Mode;


public class Message {
	private static PApplet _p5;
	private static MultiTouch[] _mt;
	private static char[] _charset;
			
	private static final Color _fillColor = new Color(0, 0, 0, 180);
	//private static final Color _strokeColor = new Color(255, 255, 255, 200);
	//private static final float _borderStrokeWeight = 4;
	private static PFont _messageFont;
	private static MessageType _messageType;
	private static String _message;
	
	Message(IDodecathedral p){
		_p5 = p.getPApplet();		
		_mt = p.getMultiTouch();
		_charset = p.getCharset();
		loadFonts();		
	}
	
	private void loadFonts() {
		_messageFont = _p5.createFont("Arial", _p5.sketchHeight() / 11f, true, _charset);		
	}

	void plot(float x, float y, float width, float height) {
		//draw message box
		_p5.fill(_fillColor.R, _fillColor.G, _fillColor.B, _fillColor.A);
		//_parent.stroke(_strokeColor.R, _strokeColor.G, _strokeColor.B, _strokeColor.A);
		//_parent.strokeWeight(_borderStrokeWeight);
		_p5.noStroke();
		_p5.rect(x, y, width, height);

		//draw message text
		_p5.textFont(_messageFont);		
		_p5.fill(_messageType.messageColor().R, _messageType.messageColor().G, _messageType.messageColor().B, _messageType.messageColor().A);
		_p5.text(_message, x + width / 40, y + height / 40, width - width / 40 , height - height / 40);
		
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
