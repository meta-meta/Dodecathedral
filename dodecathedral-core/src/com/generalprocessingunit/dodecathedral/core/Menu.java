package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Paul
 * Date: 3/20/13
 * Time: 10:44 PM
 */
public class Menu {
    Menu parentMenu;
    String title;
    private List<MenuItem> _items = new ArrayList<MenuItem>();
    private int _currentPage = 0;

    Menu(List<MenuItem> items, String title, Menu parentMenu) {
        _items = items;
        this.title = title;
        this.parentMenu = parentMenu;
    }

    Menu(List<MenuItem> items, String title) {
        this(items, title, null);
    }

    void addMenuItem(MenuItem item) {
        _items.add(item);
    }

    void plot(PApplet p5, float x, float y, float width, float height, int rows, int columns) {
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                int itemIndex = r + (c * rows) + (_currentPage * rows * columns);
                if (itemIndex < _items.size()) {
                    _items.get(itemIndex).plot(p5, x + (c * (width / columns)), y + (r * (height / rows)), width / columns, height / rows, _currentPage);
                }
            }
        }
    }

    void pageNext(int rows, int columns) {
        if (hasNextPage(rows, columns)) {
            _currentPage++;
        }
    }

    void pagePrev() {
        if (_currentPage > 0) {
            _currentPage--;
        }
    }

    boolean hasNextPage(int rows, int columns) {
        return _items.size() > rows * columns * (_currentPage+1);
    }

    void selectItem(IDodecathedral dodecathedral, float x, float y) {
        for (MenuItem item : _items) {
            if (item.isSelected(x, y, _currentPage)) {
                item.select(dodecathedral);
            }
        }
    }
}
