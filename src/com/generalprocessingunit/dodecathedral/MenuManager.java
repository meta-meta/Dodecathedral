package com.generalprocessingunit.dodecathedral;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.generalprocessingunit.dodecathedral.DeltaSequences.DeltaSequence;
import com.generalprocessingunit.dodecathedral.Modes.Mode;

public class MenuManager {
	private Dodecathedral _parent;
	private MultiTouch[] _mt;
	private static final Color _fillColor = new Color(75, 50, 75, 200);
	private static final Color _strokeColor = new Color(255, 255, 255, 200);
	private static final float _borderStrokeWeight = 4;

	private static PFont _menuItemFont;
	private static PFont _menuTitleFont;
	private static final Color _menuItemFontColor = new Color(255, 255, 255);
	private static final Color _menuTitleFontColor = new Color(200, 200, 180);

	private float xMomentum = 0;

	private static Map<String, Menu> menus;
	private static Menu currentMenu;	

	public enum MenuItemType {
		DEMO_SEQUENCE, EXERCISE, MENU, MESSAGE, COMMAND
	};

	MenuManager(Dodecathedral parent) throws XmlPullParserException, IOException {
		_parent = parent;
		_mt = parent.mt;

		loadFonts();
		menus = new HashMap<String, Menu>();
		
		XmlResourceParser parser = parent.getResources().getXml(com.generalprocessingunit.dodecathedral.R.xml.menus);
		int eventType = parser.getEventType();

		Menu menu = new Menu();		
		String name = new String();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			if (eventType == XmlPullParser.START_TAG) {
				if (tagName.equals("menu")) {
					menu = new Menu();
				} else if (tagName.equals("delta-sequence-collection")) {
					String sequenceCollection = parser.nextText();
					String[] sequenceKeys = _parent.deltaSequences.deltaSequenceLibrary.get(sequenceCollection).keySet().toArray(new String[0]);					
					for (String sequenceKey : sequenceKeys) {
						menu.addMenuItem(new MenuItem(MenuItemType.EXERCISE, sequenceKey, sequenceKey, sequenceCollection));
					}
				} else if (tagName.equals("delta-sequence")) {
					String sequenceKey = parser.nextText();
					menu.addMenuItem(new MenuItem(MenuItemType.EXERCISE, sequenceKey, sequenceKey, null));
				} else if (tagName.equals("menu-link")) {
					String menuKey = parser.nextText();
					menu.addMenuItem(new MenuItem(MenuItemType.MENU, menuKey, menuKey, null));
				} else if (tagName.equals("command")) {
					String menuText = parser.getAttributeValue(null, "text");
					String commandName = parser.nextText();
					menu.addMenuItem(new MenuItem(MenuItemType.COMMAND, menuText, commandName, null));
				} else if (tagName.equals("parent-menu")) {
					String menuKey = parser.nextText();
					menu.parentMenu = menus.get(menuKey);
				} else if (tagName.equals("name")) {
					name = parser.nextText();
					menu.title = name;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (tagName.equals("menu")) {					
					menus.put(name, menu);
				} 			
			}
			eventType = parser.next();
		}		
		
		currentMenu = menus.get("Main Menu");
	}

	private void loadFonts() {
		_menuTitleFont = _parent.createFont("Arial", (int) (_parent.sketchHeight() / 10), true);
		_menuItemFont = _parent.createFont("Arial", (int) (_parent.sketchHeight() / 12), true);
	}

	void plot(float x, float y, float width, float height) {
		//draw menu box
		_parent.fill(_fillColor.R, _fillColor.G, _fillColor.B, _fillColor.A);
		_parent.stroke(_strokeColor.R, _strokeColor.G, _strokeColor.B, _strokeColor.A);
		_parent.strokeWeight(_borderStrokeWeight);
		_parent.rect(x, y, width, height);

		//draw menu title
		_parent.textFont(_menuTitleFont);
		_parent.textAlign(PConstants.CENTER, PConstants.CENTER);
		_parent.fill(_menuTitleFontColor.R, _menuTitleFontColor.G, _menuTitleFontColor.B, _menuTitleFontColor.A);
		_parent.text(currentMenu.title, x + width / 2, height - height / 6 + height / 12);

		currentMenu.plot(x, y, width, height - height / 6, 4, 2);

		multiTouch();
	}

	private void multiTouch() {
		int elapsedTime = _parent.millis() - _mt[0].millisAtLastMove;

		// we have a finger on the screen
		if (_mt[0].touched) {
			if (elapsedTime < 100) {
				xMomentum = (_mt[0].currentX - _mt[0].prevX) * 50f;
			} else {
				xMomentum = 0;
			}

		} else {

			// check if we have a tap
			final int maxMovementForTap = 10;

			if (_mt[0].totalMovement < maxMovementForTap && _mt[0].tap) {

				float tX = _mt[0].currentX;
				float tY = _mt[0].currentY;

				currentMenu.selectItem(tX, tY);

				_mt[0].tap = false;
			}

			// prevent entering a loop of infinite division
			if (PApplet.abs(xMomentum) < 1) {
				xMomentum = 0;
			}else{
				xMomentum -= xMomentum * (elapsedTime/500f);
			}
			
			if(xMomentum < -500) { currentMenu.pageNext(4, 2); xMomentum = 0; }
			if(xMomentum > 500) { currentMenu.pagePrev(); xMomentum = 0; }
		}
		//Log.d("menu", "xmomentum:" + xMomentum);
	}

	public class MenuItem {
		private MenuItemType _itemType;
		private String _sequenceCollectionKey;
		private String _itemText;
		private String _itemKey;
		private float _x, _y, _height, _width;
		private int _page;

		// PGraphics pg;
		

		
		/**
		 * @param itemType What kind of menu item
		 * @param itemText What text gets displayed on the menu
		 * @param itemKey Hash Key for the item
		 * @param sequenceCollectionKey only needed if the item is coming from a DataSequence collection in the library
		 * 
		 */
		MenuItem(MenuItemType itemType, String itemText, String itemKey, String sequenceCollectionKey) {
			_itemType = itemType;
			_itemText = itemText;
			_itemKey = itemKey;
			if(itemType == MenuItemType.DEMO_SEQUENCE)
			{
				_sequenceCollectionKey = sequenceCollectionKey;
			}
		}

		void plot(float x, float y, float width, float height, int page) {
			this._x = x;
			this._y = y;
			this._height = height;
			this._width = width;
			this._page = page;

			/*
			 * pg = _parent.createGraphics((int)width,(int)height,
			 * PConstants.JAVA2D); pg.beginDraw(); pg.smooth();
			 * pg.textAlign(PConstants.CENTER, PConstants.CENTER);
			 * pg.textFont(_menuItemFont); pg.background(50);
			 * pg.fill(_menuItemFontColor.R, _menuItemFontColor.G,
			 * _menuItemFontColor.B, _menuItemFontColor.A); pg.text(_itemText,
			 * width / 2, height / 2); pg.endDraw(); _parent.image(pg, x, y);
			 */

			_parent.text(_itemText, x + width / 2, y + height / 2);

		}

		void select() {
			switch (_itemType) {
			case MENU:
				currentMenu = menus.get(_itemKey);
				break;
			case DEMO_SEQUENCE:
				_parent.demo.setSequence(_parent.deltaSequences.deltaSequenceLibrary.get(_sequenceCollectionKey).get(_itemKey));
				Modes.switchMode(Mode.DEMO_PLAYING);
				break;
			case EXERCISE:
				_parent.exercises.setExercise(_parent.exercises.exerciseLibrary.get(_itemKey));
				_parent.exercises.runExercise();
				break;
			case COMMAND:
				executeMenuCommand();
				break;
			default: // MESSAGE
				break;

			}
		}
		
		void executeMenuCommand(){
			switch(Command.valueOf(_itemKey)){
			case TOGGLE_DRONE:
				toggleDrone();				
			}
		}

		private void toggleDrone() {
			// TODO move commands out to their own class and figure out how to gracefully change corresponding menu text
			_parent.drone = !_parent.drone;
			_itemText = _parent.drone ? "Stop Drone" : "Play Drone";
			_parent.playDrone();
		}

		/**
		 * @param x
		 *            cursor x position
		 * @param y
		 *            cursor y position
		 * @return True if cursor is within this item
		 */
		boolean isSelected(float x, float y, int page) {
			return _page == page && _x < x && _x + _width > x && _y < y && _y + _height > y;
		}
	}

	public class Menu {
		public Menu parentMenu;
		public String title;
		private List<MenuItem> _items = new ArrayList<MenuItem>();
		private int _currentPage = 0;

		Menu(){};
		
		Menu(List<MenuItem> items, String title) {
			this(items, title, null);
		}

		Menu(List<MenuItem> items, String title, Menu parentMenu) {
			_items = items;
			this.title = title;
			this.parentMenu = parentMenu;			
		}

		void addMenuItem(MenuItem item) {
			_items.add(item);
		}

		void plot(float x, float y, float width, float height, int rows, int columns) {

			_parent.textFont(_menuItemFont);
			_parent.textAlign(PConstants.CENTER, PConstants.CENTER);
			_parent.fill(_menuItemFontColor.R, _menuItemFontColor.G, _menuItemFontColor.B, _menuItemFontColor.A);

			for (int c = 0; c < columns; c++) {
				for (int r = 0; r < rows; r++) {
					int itemIndex = r + (c * rows) + (_currentPage * rows * columns);
					if (itemIndex < _items.size()) {
						_items.get(itemIndex).plot(x + (c * (width / columns)), y + (r * (height / rows)), width / columns, height / rows, _currentPage);
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

		void selectItem(float x, float y) {
			for (MenuItem item : _items) {
				if (item.isSelected(x, y, _currentPage)) {
					item.select();
				}
			}
		}
	}

	public void back() {
		if(currentMenu.parentMenu !=null){
			currentMenu = currentMenu.parentMenu;
		}		
	}

}
