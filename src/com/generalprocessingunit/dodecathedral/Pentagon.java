package com.generalprocessingunit.dodecathedral;

import processing.core.PVector;

/**
 * Holds the five vertices that comprise a panel of the dodecahedron. Contains
 * an outer pentagon that corresponds to a face of the dodecahedron. Also
 * Contains an inner pentagon that is inset on that face.
 * 
 * @author Paul M. Christian
 * 
 */
public class Pentagon {
	final PVector[] vertices = new PVector[5];
	final PVector[] innerPentagon = new PVector[5];
	PVector center;

	Pentagon(int[] dodecahedronVertexIndexes, PVector[] dodecahedronVertices) {

		for (int i = 0; i < 5; i++) {
			vertices[i] = dodecahedronVertices[dodecahedronVertexIndexes[i]];
		}

		setInnerVertices();
	}

	private void setInnerVertices() {
		// get the center point of the pentagon
		float x = 0;
		float y = 0;
		float z = 0;
		for (int i = 0; i < 5; i++) {
			x += vertices[i].x;
			y += vertices[i].y;
			z += vertices[i].z;
		}
		center = new PVector(x / 5, y / 5, z / 5);

		// calculate the inner pentagon coordinates
		for (int i = 0; i < 5; i++) {
			innerPentagon[i] = new PVector(
					vertices[i].x + center.x * 0.2f, 
					vertices[i].y + center.y * 0.2f, 
					vertices[i].z + center.z * 0.2f);
		}
	}
}
