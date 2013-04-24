package com.generalprocessingunit.dodecathedral.core;

public class Color {
	int R;
	int G;
	int B;
	int A;

	Color(int hex) {
		this(hex, 255);
	}

	Color(int hex, int alpha) {
		this((hex & 0xFF0000) >> 16, (hex & 0xFF00) >> 8, (hex & 0xFF), alpha);
	}

	Color(int red, int green, int blue) {
		this(red, green, blue, 255);
	}

	Color(int red, int green, int blue, int alpha) {
		R = red;
		G = green;
		B = blue;
		A = alpha;
	}
}
