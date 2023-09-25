package com.fltron.server.utils;

import java.awt.Color;

public class GameConstants {
	public static final short MOVE_LEFT = 0;
	public static final short MOVE_UP = 1;
	public static final short MOVE_RIGHT = 2;
	public static final short MOVE_DOWN = 3;
	
	public static final short PLAYER_SIZE = 4;
	
	public static final short PLAYER_STATUS_ALIVE = 0;	
	public static final short PLAYER_STATUS_DEAD = 1;
	
	public static final short MAP_WIDTH = 1280;
	public static final short MAP_HEIGHT = 720;
	public static final short MOVEMENT_SPEED = 1;
	
	public static final Color[] PLAYER_COLOR = new Color[] {Color.BLUE, Color.ORANGE, Color.GREEN, Color.MAGENTA};
	public static final short[][] PLAYER_INIT_COORDS = new short[][] {
			{(short)128, (short)360}, {(short)1152, (short)360}, {(short)640, (short)72}, {(short)640, (short)648}
	};
	public static final short[] PLAYER_INIT_MOVE = new short[] {GameConstants.MOVE_RIGHT, GameConstants.MOVE_LEFT,GameConstants.MOVE_DOWN,GameConstants.MOVE_UP};
}
