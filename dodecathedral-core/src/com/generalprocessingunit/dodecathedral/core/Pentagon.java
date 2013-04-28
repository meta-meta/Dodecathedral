package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

/**
 * Holds the five vertices that comprise a panel of the dodecahedron. Contains
 * an outer pentagon that corresponds to a face of the dodecahedron. Also
 * Contains an inner pentagon that is inset on that face.
 *
 * @author Paul M. Christian
 */
public class Pentagon {
    final PVector[] vertices = new PVector[5];
    final PVector[] innerPentagon = new PVector[5];
    final Color color;
    PVector center;
    PShape coloredPanel;
    PShape lightPanel;
    PShape symbol;
    PShape highlightGlass;

    private int _tapIndicator = 0;
    private int _millisAtTap = 0;

    Pentagon(int[] dodecahedronVertexIndexes, PVector[] dodecahedronVertices, Color color) {
        for (int i = 0; i < 5; i++) {
            vertices[i] = dodecahedronVertices[dodecahedronVertexIndexes[i]];
        }

        this.color = color;

        setInnerVertices();
    }

    protected void createGeometry(PApplet p5, int index, Color color) {
        createColoredPanel(p5, color);

        createLightPanel(p5, color);

        createSymbolPanel(p5, index);

        createHighlightGlass(p5, color);
    }

    protected void drawPanel(PApplet p5, boolean selected, int tapIndicator, int millisAtTap){
        final int tapIndicatorLength = 500;

        if(tapIndicator != 0){
            _tapIndicator = tapIndicator;
            _millisAtTap = millisAtTap;
        }

        // reset the animation
        if (p5.millis() - _millisAtTap > tapIndicatorLength && _tapIndicator != 0) {
            _tapIndicator = 0;
            int r = color.R;
            int g = color.G;
            int b = color.B;
            highlightGlass.setFill(1,p5.color(r,g,b,127));
            highlightGlass.setFill(3,p5.color(r,g,b,127));
            highlightGlass.setFill(4,p5.color(r,g,b,127));
            lightPanel.setFill(p5.color(220));
        }

        // single finger tapIndicator lights panel and fades for tapIndicatorLength milliseconds
        if (_tapIndicator == 1) {
            float a = 255 - ((p5.millis() - _millisAtTap) * (255f / tapIndicatorLength));
            float r = color.R + a;
            float g = color.G + a;
            float b = color.B + a;
            highlightGlass.setFill(1,p5.color(r,g,b,127 + (a)));
            highlightGlass.setFill(3,p5.color(r,g,b,127 + (a)));
            highlightGlass.setFill(4,p5.color(r,g,b,127 + (a)));
        }

        // double finger tapIndicator darkens panel and fades for tapIndicatorLength milliseconds
        if (_tapIndicator == 2) {
            float a = 255 - ((p5.millis() - _millisAtTap) * (255f / tapIndicatorLength));
            float r = color.R - a;
            float g = color.G - a;
            float b = color.B - a;
            highlightGlass.setFill(1,p5.color(r,g,b,127 - (a)));
            highlightGlass.setFill(3,p5.color(r,g,b,127 - (a)));
            highlightGlass.setFill(4,p5.color(r,g,b,127 - (a)));
            lightPanel.setFill(p5.color(220,255-a));
        }

        if(selected){
            p5.shape(lightPanel);
            p5.shape(symbol);
            p5.blendMode(PConstants.SCREEN);
            p5.shape(highlightGlass);
            p5.blendMode(PConstants.BLEND);
        }
        else{
            p5.shape(coloredPanel);
        }
    }

    private void createColoredPanel(PApplet p5, Color color) {
        coloredPanel = p5.createShape();
        coloredPanel.beginShape();
        coloredPanel.stroke(50);
        coloredPanel.strokeWeight(10);
        coloredPanel.fill(color.R, color.G,color.B, 127);
        for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
        {
            coloredPanel.vertex(innerPentagon[i].x, innerPentagon[i].y, innerPentagon[i].z);

        }
        coloredPanel.endShape(PConstants.CLOSE);
    }

    private void createLightPanel(PApplet p5, Color color) {
        lightPanel = p5.createShape();
        lightPanel.beginShape();
        lightPanel.stroke(color.R, color.G, color.B);
        lightPanel.strokeWeight(10);
        lightPanel.fill(220);
        for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
        {
            lightPanel.vertex(innerPentagon[i].x, innerPentagon[i].y, innerPentagon[i].z);
        }
        lightPanel.endShape(PConstants.CLOSE);
    }

    private void createSymbolPanel(PApplet p5, int index) {
        symbol = p5.createShape();
        symbol.beginShape();
        symbol.noStroke();
        symbol.textureMode(PConstants.NORMAL); //our pentagon coordinates are based on the unit circle
        symbol.texture(Dodecahedron.symbols[index]);
        for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
        {
            symbol.vertex(vertices[i].x * 0.9f, vertices[i].y * 0.9f, vertices[i].z * 0.9f, Dodecahedron.textureU[i], Dodecahedron.textureV[i]);
        }
        symbol.endShape(PConstants.CLOSE);
    }

    private void createHighlightGlass(PApplet p5, Color color) {
        highlightGlass = p5.createShape();
        highlightGlass.beginShape();
        highlightGlass.stroke(color.R * 1.5f, color.G * 1.5f, color.B * 1.5f);
        highlightGlass.strokeWeight(3);
        highlightGlass.fill(color.R, color.G, color.B, 127);
        for (int i = 0; i < 5; i++) // loop through each pentagon's vertices
        {
            float x = vertices[i].x - center.x * 0.6f;
            float y = vertices[i].y - center.y * 0.6f;
            float z = vertices[i].z - center.z * 0.6f;
            highlightGlass.vertex(x * 0.98f, y * 0.98f, z * 0.98f);
        }
        highlightGlass.endShape(PConstants.CLOSE);
        int r = color.R/3;
        int g = color.G/3;
        int b = color.B/3;
        highlightGlass.setFill(0, p5.color(r,g,b,127));
        highlightGlass.setFill(2,p5.color(r,g,b,127));
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
                    vertices[i].x + center.x * 0.15f,
                    vertices[i].y + center.y * 0.15f,
                    vertices[i].z + center.z * 0.15f);
        }
    }
}
