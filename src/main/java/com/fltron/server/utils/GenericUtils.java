package com.fltron.server.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.fltron.server.dto.CoordsDTO;
import com.fltron.server.dto.responses.ResponseDTO;

public class GenericUtils {
	public static final void fillResponse(ResponseDTO response, String code, String description) {
		response.setResultCode(code);
		response.setResultDescription(description);;
	}

	public static short[] getValidMoves() {
		return new short[] {GameConstants.MOVE_LEFT, GameConstants.MOVE_UP, 
				GameConstants.MOVE_RIGHT, GameConstants.MOVE_DOWN};
	}
	
	public static CoordsDTO getBiggerCoords(CoordsDTO m1, CoordsDTO m2) {
		CoordsDTO biggerCoods = new CoordsDTO();
		if (m1.getX() >= m2.getX()) {
			biggerCoods.setX(m1.getX());
		} else if (m2.getX() > m1.getX()) {
			biggerCoods.setX(m2.getX());
		}		
		
		if (m1.getY() >= m2.getY()) {
			biggerCoods.setY(m1.getY());
		} else if (m2.getY() > m1.getY()) {
			biggerCoods.setY(m2.getY());
		}
		
		return biggerCoods;
		
	}
	
	public static CoordsDTO getMinorCoords(CoordsDTO m1, CoordsDTO m2) {
		CoordsDTO minorCoods = new CoordsDTO();
		if (m1.getX() <= m2.getX()) {
			minorCoods.setX(m1.getX());
		} else if (m2.getX() < m1.getX()) {
			minorCoods.setX(m2.getX());
		}		
		
		if (m1.getY() <= m2.getY()) {
			minorCoods.setY(m1.getY());
		} else if (m2.getY() < m1.getY()) {
			minorCoods.setY(m2.getY());
		}
		
		return minorCoods;
		
	}
	
	public static String get_SHA_512_SecurePassword(String passwordToHash, String salt){
	    String generatedPassword = null;
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-512");
	        md.update(salt.getBytes(StandardCharsets.UTF_8));
	        byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< bytes.length ;i++){
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        generatedPassword = sb.toString();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return generatedPassword;
	}
	
	public static boolean areMovesOpposite(short moveDirection1, short moveDirection2) {
		if (GameConstants.MOVE_DOWN == moveDirection1 && GameConstants.MOVE_UP == moveDirection2) {
			return true;
		}
		
		if (GameConstants.MOVE_LEFT == moveDirection1 && GameConstants.MOVE_RIGHT == moveDirection2) {
			return true;
		}
		
		if (GameConstants.MOVE_RIGHT == moveDirection1 && GameConstants.MOVE_LEFT == moveDirection2) {
			return true;
		}
		
		if (GameConstants.MOVE_UP == moveDirection1 && GameConstants.MOVE_DOWN == moveDirection2) {
			return true;
		}
		
		return false;
	}
	
	public static boolean areMovesValid(short lastMove, short newMove) {
		if (GameConstants.MOVE_DOWN == lastMove || GameConstants.MOVE_UP == lastMove) {
			return GameConstants.MOVE_LEFT == newMove || GameConstants.MOVE_RIGHT == newMove;
		}
		
		if (GameConstants.MOVE_LEFT == lastMove || GameConstants.MOVE_RIGHT == lastMove) {
			return GameConstants.MOVE_DOWN == newMove || GameConstants.MOVE_UP == newMove;
		}
		
		return false;
	}

	public static CoordsDTO copyCoords(CoordsDTO coords) {
		CoordsDTO newCoords = new CoordsDTO();
		newCoords.setX(coords.getX());
		newCoords.setY(coords.getY());
		return newCoords;
	}
}
