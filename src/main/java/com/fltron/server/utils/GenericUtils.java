package com.fltron.server.utils;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.entities.Coords;

public class GenericUtils {
	public static final void fillResponse(ResponseDTO response, String code, String description) {
		response.setResultCode(code);
		response.setResultDescription(description);;
	}

	public static short[] getValidMoves() {
		return new short[] {GameConstants.MOVE_LEFT, GameConstants.MOVE_UP, 
				GameConstants.MOVE_RIGHT, GameConstants.MOVE_DOWN};
	}
	
	public static Coords getBiggerCoords(Coords m1, Coords m2) {
		Coords biggerCoods = new Coords();
		if (m1.getX() >= m2.getX()) {
			biggerCoods.setX(m1.getX());
		} else if (m2.getX() > m1.getX()) {
			biggerCoods.setX(m2.getX());
		}		
		
		if (m1.getY() >= m2.getY()) {
			biggerCoods.setY(m1.getY());
		} else if (m2.getY() > m1.getY()) {
			biggerCoods.setX(m2.getY());
		}
		
		return biggerCoods;
		
	}
	
	public static Coords getMinorCoords(Coords m1, Coords m2) {
		Coords minorCoods = new Coords();
		if (m1.getX() <= m2.getX()) {
			minorCoods.setX(m1.getX());
		} else if (m2.getX() < m1.getX()) {
			minorCoods.setX(m2.getX());
		}		
		
		if (m1.getY() <= m2.getY()) {
			minorCoods.setY(m1.getY());
		} else if (m2.getY() < m1.getY()) {
			minorCoods.setX(m2.getY());
		}
		
		return minorCoods;
		
	}
}
