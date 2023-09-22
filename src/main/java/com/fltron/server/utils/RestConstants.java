package com.fltron.server.utils;

public class RestConstants {
	public static final String REST_GENERIC_ERROR = "GEN_0000";
	public static final String REST_GENERIC_ERROR_DESC = "Error interno en el contorlador";
	
	public static final String REST_GENERIC_SERVICE_ERROR = "GEN_0001";
	public static final String REST_GENERIC_SERVICE_ERROR_DESC = "Error interno en el contorlador";
	

	public static final String REST_GENERIC_SERVICE_OK = "GEN_0002";
	public static final String REST_GENERIC_SERVICE_OK_DESC = "OK";
	
	public static final String REST_GENERIC_ERROR_PARAM = "GEN_0003";
	public static final String REST_GENERIC_ERROR_PARAM_DESC = "Mandatory params not in the request or bad format";
	
	// Rooms
	public static final String REST_ROOM_CREATE_ERROR = "ROOM_0000";
	public static final String REST_ROOM_CREATE_ERROR_DESC = "Error while adding user to a room";
	
	public static final String REST_ROOM_CREATE_EXISTING_USER = "ROOM_0001";
	public static final String REST_ROOM_CREATE_EXISTING_USER_DESC = "Error while adding user to a room, already exists this user";
	
	public static final String REST_ROOM_USER_NOT_IN_ROOM = "ROOM_0002";
	public static final String REST_ROOM_USER_NOT_IN_ROOM_DESC = "User not in room";
	
	public static final String REST_ROOM_INVALID_MOVE = "ROOM_0003";
	public static final String REST_ROOM_INVALID_MOVE_DESC = "Invalid move";
}
