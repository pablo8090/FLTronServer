package com.fltron.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.entities.Coords;
import com.fltron.server.entities.Room;
import com.fltron.server.room.RoomThread;
import com.fltron.server.services.RoomService;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

import io.micrometer.common.util.StringUtils;

@Service
public class RoomServiceImpl extends BaseServiceImpl implements RoomService {
	
	
	private ArrayList<Room> newRooms = new ArrayList<>();
	ArrayList<RoomThread> startedRooms = new ArrayList<>();
	private Integer totalRooms = 0;

	@Override
	public void joinRoom(RoomResponseDTO response, String userName, Integer roomSize) {
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
	


	@Override
	public void playerReady(ResponseDTO response, String userName, Integer roomId) {
		try {
			if (StringUtils.isBlank(userName) || roomId < 0) {
				fillResponseRequestError(response);
				return;
			}
						
			
			RoomThread roomTh = getRoomByUser(roomId, userName);
			if (roomTh == null) {
				fillResponse(response, RestConstants.REST_ROOM_USER_NOT_IN_ROOM,
						RestConstants.REST_ROOM_USER_NOT_IN_ROOM_DESC);
				return;
			}
			
			roomTh.userReady(userName);
			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
		
	}
	
	@Override
	public void playerMove(ResponseDTO response, String userName, Integer roomId, 
			Short moveDirection, Short x, Short y) {
		try {
			if (StringUtils.isBlank(userName) || moveDirection < 0) {
				fillResponseRequestError(response);
				return;
			}
						
			
			RoomThread roomTh = getRoomByUser(roomId, userName);
			if (roomTh == null) {
				fillResponse(response, RestConstants.REST_ROOM_USER_NOT_IN_ROOM,
						RestConstants.REST_ROOM_USER_NOT_IN_ROOM_DESC);
				return;
			}
			
			boolean validMove = validateMove(moveDirection);
			if (!validMove) {
				fillResponse(response, RestConstants.REST_ROOM_INVALID_MOVE,
						RestConstants.REST_ROOM_INVALID_MOVE_DESC);
				return;
			}
			Coords coords = new Coords(x, y);
			roomTh.userMove(userName, moveDirection, coords );
			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
		
	}


	
	private boolean validateMove(short moveDirection) {
		boolean valid = false;
		short[] validMoves = GenericUtils.getValidMoves();
		for (int i = 0; i < validMoves.length; i++) {
			if (moveDirection == validMoves[i]) {
				valid = true;
			}
		}
		return valid;
	}



	private RoomThread getRoomByUser(Integer roomId, String userName) {
		
		List<RoomThread> matchedRooms = startedRooms.stream().filter(r -> r.getRoom().getId() == roomId 
				&& r.getRoom().getPlayers().contains(userName)).collect(Collectors.toList());
		if (matchedRooms.size() == 1) {
			return matchedRooms.get(0);
		}
		return null;
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


	private boolean addNewPlayer (RoomResponseDTO response, String userName, Integer roomSize) {
		synchronized (newRooms) {
			try {
				List<Room> roomsBySize = newRooms.stream()
					    .filter(p -> p.getSize() == roomSize).collect(Collectors.toList());
				
				if (roomsBySize.isEmpty()) {
					Room newRoom = new Room();
					newRoom.getPlayers().add(userName);
					newRoom.setSize(roomSize);
					newRoom.setId(totalRooms);
					newRooms.add(newRoom);
					totalRooms++;
					response.setRoomId(newRoom.getId());
					
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
						response.setRoomId(newRoom.getId());
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
