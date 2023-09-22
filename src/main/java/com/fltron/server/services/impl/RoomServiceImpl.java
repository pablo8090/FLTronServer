package com.fltron.server.services.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.entities.Room;
import com.fltron.server.room.RoomHandler;
import com.fltron.server.room.RoomThread;
import com.fltron.server.services.RoomService;
import com.fltron.server.utils.RestConstants;

import io.micrometer.common.util.StringUtils;

@Service
public class RoomServiceImpl extends BaseServiceImpl implements RoomService {
	
//	@Autowired
//	private RoomHandler roomHandler;
	
	private Room newRoom = new Room();
	ArrayList<RoomThread> startedRooms = new ArrayList<>();

	@Override
	public void joinRoom(ResponseDTO response, String userName) {
		try {
			if (StringUtils.isBlank(userName)) {
				fillResponseRequestError(response);
				return;
			}
			
			if (existsUser(userName)) {
				fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
						RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
				return;
			}
			
			
			boolean addNew = addNewPlayer(response, userName);
			if (!addNew) {
				return;
			} 
			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
		
	}
	
	
	private boolean existsUser(String userName) {
		synchronized (startedRooms) {
			for (RoomThread roomTh : startedRooms) {
				Room room = roomTh.getRoom();
				boolean exists = existsUserInRoom(room, userName);
				if (exists) {
					return true;
				}
			}
		}
		
		return false;
	}


	private boolean existsUserInRoom(Room room, String userName) {
		if (userName.equals(room.getPlayer1()) 
				|| userName.equals(room.getPlayer2())) {
			return true;
		}
		return false;
	}


	private boolean addNewPlayer (ResponseDTO response, String userName) {
		synchronized (newRoom) {
			try {
				if (StringUtils.isBlank(newRoom.getPlayer1())
						&& StringUtils.isBlank(newRoom.getPlayer2())) {
					newRoom.setPlayer1(userName);
				} else if (!StringUtils.isBlank(newRoom.getPlayer1()) 
						&& StringUtils.isBlank(newRoom.getPlayer2())) {
					boolean alreadyExists = existsUserInRoom(newRoom, userName);
					if (alreadyExists) {
						fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
								RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
						return false; 
					}
					
					newRoom.setPlayer2(userName);
					RoomThread rt = new RoomThread(newRoom);
					rt.start();
					startedRooms.add(rt);
					newRoom = new Room();				
				} else {
					fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
							RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
					return false;
				}
				
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
			return false;
		}	
	}

}
