package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {
	private static Map<String, Menu> _menus;
	private static Menu _currentMenu;

	private static float _xMomentum = 0;
	
	private static final Color _fillColor = new Color(75, 50, 75, 200);
	private static final Color _strokeColor = new Color(255, 255, 255, 200);
	private static final float _borderStrokeWeight = 4;

	private static PFont _menuItemFont;
	private static PFont _menuTitleFont;
	private static final Color _menuTitleFontColor = new Color(200, 200, 180);

    static{
        _menus = new HashMap<String, Menu>();

        List<MenuItem> items;

        // Main Menu
        items = new ArrayList<MenuItem>();
        items.add(new MenuItem(MenuItem.MenuItemType.MENU, "Arpeggios", "Arpeggios" ));
        items.add(new MenuItem(MenuItem.MenuItemType.MENU, "Church Modes", "Church Modes" ));
        items.add(new MenuItem(MenuItem.MenuItemType.MENU, "Melodies", "Melodies" ));
        items.add(new MenuItem(MenuItem.MenuItemType.COMMAND, "Play Drone", "TOGGLE_DRONE" ));
        items.add(new MenuItem(MenuItem.MenuItemType.COMMAND, "Starfield Off", "TOGGLE_STARFIELD" ));
        items.add(new MenuItem(MenuItem.MenuItemType.COMMAND, "Simon", "RANDOM_EXERCISE" ));
        items.add(new MenuItem(MenuItem.MenuItemType.COMMAND, "Quit Exercise", "QUIT_EXERCISE" ));
        _menus.put("Main Menu", new Menu(items, "Main Menu"));

        // Arpeggios
        items = new ArrayList<MenuItem>();
        for(DeltaSequence ds : DeltaSequenceLibrary.getDeltaSequenceCollection("Arpeggios").values()){
            items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, ds.name, ds.name));
        }
        items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, "Play All", "Arpeggios" ));
        _menus.put("Arpeggios", new Menu(items, "Arpeggios", _menus.get("Main Menu")));

        // Church Modes
        items = new ArrayList<MenuItem>();
        for(DeltaSequence ds : DeltaSequenceLibrary.getDeltaSequenceCollection("Church Modes").values()){
            items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, ds.name, ds.name));
        }
        items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, "Play All", "Church Modes" ));
        _menus.put("Church Modes", new Menu(items, "Church Modes", _menus.get("Main Menu")));

        // Melodies
        items = new ArrayList<MenuItem>();
        for(DeltaSequence ds : DeltaSequenceLibrary.getDeltaSequenceCollection("Melodies").values()){
            items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, ds.name, ds.name));
        }
        items.add(new MenuItem(MenuItem.MenuItemType.EXERCISE, "Play All", "Melodies" ));
        _menus.put("Melodies", new Menu(items, "Melodies", _menus.get("Main Menu")));

        setCurrentMenu("Main Menu");
    }

    private MenuManager(){}

	static void setCurrentMenu(String menuKey){
        _currentMenu = _menus.get(menuKey);
    }

    public static void loadFonts(PApplet p5, char[] charset) {
		_menuTitleFont = p5.createFont("Arial", 40 , true, charset);
		_menuItemFont = p5.createFont("Arial", 30, true, charset);
	}

	public static void plot(PApplet p5, float x, float y, float width, float height) {
		//draw menu box
        p5.fill(_fillColor.R, _fillColor.G, _fillColor.B, _fillColor.A);
        p5.stroke(_strokeColor.R, _strokeColor.G, _strokeColor.B, _strokeColor.A);
        p5.strokeWeight(_borderStrokeWeight);
        p5.rect(x, y, width, height);

		//draw menu title
        p5.textFont(_menuTitleFont);
        p5.textAlign(PConstants.CENTER, PConstants.CENTER);
        p5.fill(_menuTitleFontColor.R, _menuTitleFontColor.G, _menuTitleFontColor.B, _menuTitleFontColor.A);
        p5.text(_currentMenu.title, x + width / 2, height - height / 6 + height / 12);

        p5.textFont(_menuItemFont);
		_currentMenu.plot(p5, x, y, width, height - height / 6, 4, 2);
	}

	public static void touchControl(IDodecathedral dodecathedral, MultiTouch[] mt) {
		int timeBetweenTouches = (mt[0].millisAtLastMove - mt[0].prevMillis);

		// we have a finger on the screen
		if (mt[0].touched) {
			if (timeBetweenTouches < 100) {
				_xMomentum = (mt[0].currentX - mt[0].prevX) * 50f;
			} else {
				_xMomentum = 0;
			}

		} else {

			// check if we have a tapIndicator
			final int maxMovementForTap = 10;

			if (mt[0].totalMovement < maxMovementForTap && mt[0].tap) {

				float tX = mt[0].currentX;
				float tY = mt[0].currentY;

				_currentMenu.selectItem(dodecathedral, tX, tY);

				mt[0].tap = false;
			}

			// prevent entering a loop of infinite division
			if (PApplet.abs(_xMomentum) < 1) {
				_xMomentum = 0;
			}else{
				_xMomentum -= _xMomentum * (timeBetweenTouches/500f);
			}
			
			if(_xMomentum < -500) { _currentMenu.pageNext(4, 2); _xMomentum = 0; }
			if(_xMomentum > 500) { _currentMenu.pagePrev(); _xMomentum = 0; }
		}		
	}

	public static void back() {
		if(_currentMenu.parentMenu !=null){
			_currentMenu = _currentMenu.parentMenu;
		}		
	}



}
