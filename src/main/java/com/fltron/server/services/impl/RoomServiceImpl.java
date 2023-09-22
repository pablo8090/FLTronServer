package com.fltron.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	
	private ArrayList<Room> newRooms = new ArrayList<>();
	ArrayList<RoomThread> startedRooms = new ArrayList<>();

	@Override
	public void joinRoom(ResponseDTO response, String userName, Integer roomSize) {
		try {
			if (StringUtils.isBlank(userName) || roomSize < 2) {
				fillResponseRequestError(response);
				return;
			}
			
			
			if (existsUser(userName)) {
				fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
						RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
				return;
			}
			
			
			boolean addNew = addNewPlayer(response, userName, roomSize);
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
			synchronized (newRooms) {
				for (RoomThread roomTh : startedRooms) {
					Room room = roomTh.getRoom();
					boolean exists = existsUserInRoom(room, userName);
					if (exists) {
						return true;
					}
				}

				for (Room room : newRooms) {
					boolean exists = existsUserInRoom(room, userName);
					if (exists) {
						return true;
					}
				}
			}
		}
		return false;
	}


	private boolean existsUserInRoom(Room room, String userName) {
		List<String> players = room.getPlayers();
		for (String player : players) {
			if (userName.equalsIgnoreCase(player)) {
				return true;
			}
		}		
		return false;
	}


	private boolean addNewPlayer (ResponseDTO response, String userName, Integer roomSize) {
		synchronized (newRooms) {
			try {
				List<Room> roomsBySize = newRooms.stream()
					    .filter(p -> p.getSize() == roomSize).collect(Collectors.toList());
				
				if (roomsBySize.isEmpty()) {
					Room newRoom = new Room();
					newRoom.getPlayers().add(userName);
					newRoom.setSize(roomSize);
					newRooms.add(newRoom);
					
				} else {
					for (Room newRoom : roomsBySize) {
						boolean alreadyExists = existsUserInRoom(newRoom, userName);
						if (alreadyExists) {
							fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
									RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
							return false; 
						}
						
						List<String> roomPlayers = newRoom.getPlayers();
						roomPlayers.add(userName);
						if (roomPlayers.size() == newRoom.getSize()) {
							RoomThread rt = new RoomThread(newRoom);
							rt.start();
							startedRooms.add(rt);
							newRooms.remove(newRoom);
						}
					}
				}				
				
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
			return false;
		}	
	}

}
