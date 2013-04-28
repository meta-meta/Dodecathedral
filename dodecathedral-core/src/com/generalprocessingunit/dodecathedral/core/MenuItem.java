package com.generalprocessingunit.dodecathedral.core;

import processing.core.PApplet;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 3/20/13
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuItem {
    private MenuItemType _itemType;
    private String _sequenceCollectionKey;
    private String _itemText;
    private String _itemKey;
    private float _x, _y, _height, _width;
    private int _page;

    /**
     * @param itemType              What kind of menu item
     * @param itemText              What text gets displayed on the menu
     * @param itemKey               Hashmap Key for the item
     * @param sequenceCollectionKey only needed if the item is coming from a DataSequence collection in the library
     */
    MenuItem(MenuItemType itemType, String itemText, String itemKey, String sequenceCollectionKey) {
        _itemType = itemType;
        _itemText = itemText;
        _itemKey = itemKey;
        if (itemType == MenuItemType.DEMO_SEQUENCE) {
            _sequenceCollectionKey = sequenceCollectionKey;
        }
    }

    MenuItem(MenuItemType itemType, String itemText, String itemKey) {
        this(itemType, itemText, itemKey, null);
    }

    void plot(PApplet p5, float x, float y, float width, float height, int page) {
        if (_itemKey.equals(Command.QUIT_EXERCISE.name()) && !Exercises.running) {
            return;
        }

        //set these so we can check later if the user touches this item
        this._x = x;
        this._y = y;
        this._height = height;
        this._width = width;
        this._page = page;

        p5.fill(_itemType.menuItemColor().R, _itemType.menuItemColor().G, _itemType.menuItemColor().B, _itemType.menuItemColor().A);
        p5.text(_itemText, x, y, width, height);
    }

    void select(IDodecathedral dodecathedral) {
        switch (_itemType) {
            case MENU:
                MenuManager.setCurrentMenu(_itemKey);
                break;
            case DEMO_SEQUENCE:
                Demo.setSequence(DeltaSequenceLibrary.getDeltaSequenceCollection(_sequenceCollectionKey).get(_itemKey));
                Modes.switchMode(Modes.Mode.DEMO_PLAYING);
                break;
            case EXERCISE:
                Exercises.setExercise(_itemKey);
                Exercises.runExercise();
                break;
            case COMMAND:
                executeMenuCommand(dodecathedral);
                break;
            default: // MESSAGE
                break;
        }
    }

    void executeMenuCommand(IDodecathedral dodecathedral) {
        switch (Command.valueOf(_itemKey)) {
            case TOGGLE_DRONE:
                _itemText = dodecathedral.toggleDrone()? "Stop Drone" : "Play Drone";
                break;
            case TOGGLE_STARFIELD:
                _itemText = Stars.toggleStarfield() ? "Stars Off" : "Stars On";
                break;
            case RANDOM_EXERCISE:
                Exercises.setRandomExercise(3);
                Exercises.runExercise();
                break;
            case QUIT_EXERCISE:
                Exercises.running = false;
                Modes.switchMode(Modes.Mode.FREE_PLAY);
                break;
        }
    }

    /**
     * @param x cursor x position
     * @param y cursor y position
     * @return True if cursor is within this item
     */
    boolean isSelected(float x, float y, int page) {
        return _page == page && _x < x && _x + _width > x && _y < y && _y + _height > y;
    }

    public enum MenuItemType {
        COMMAND(new Color(127, 127, 255)),
        DEMO_SEQUENCE(new Color(127, 127, 127)),
        EXERCISE(new Color(127, 255, 127)),
        MENU(new Color(255, 255, 255));

        private final Color _menuItemColor;

        MenuItemType(Color menuItemColor) {
            _menuItemColor = menuItemColor;
        }

        private Color menuItemColor() {
            return _menuItemColor;
        }
    }
}
