package com.fltron.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fltron.server.dto.CoordsDTO;
import com.fltron.server.dto.MoveDTO;
import com.fltron.server.dto.UserDTO;
import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomDetailResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.entities.Room;
import com.fltron.server.entities.User;
import com.fltron.server.room.RoomThread;
import com.fltron.server.services.RoomService;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

import io.micrometer.common.util.StringUtils;

@Service
public class RoomServiceImpl extends BaseServiceImpl implements RoomService {
	
	
	private ArrayList<Room> newRooms = new ArrayList<>();
	ArrayList<RoomThread> startedRooms = new ArrayList<>();
	private Long totalRooms = 0L;

	@Override
	public void joinRoom(RoomResponseDTO response, User user, Integer roomSize) {
		try {
			if (roomSize < 2) {
				fillResponseRequestError(response);
				return;
			}
			
			
			if (existsUser(user.getId())) {
				fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
						RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
				return;
			}
			
			
			boolean addNew = addNewPlayer(response, user, roomSize);
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
	public void playerReady(ResponseDTO response, User user, Long roomId) {
		try {
			if (roomId < 0) {
				fillResponseRequestError(response);
				return;
			}
						
			
			RoomThread roomTh = getRoomByUser(roomId, user);
			if (roomTh == null) {
				fillResponse(response, RestConstants.REST_ROOM_USER_NOT_IN_ROOM,
						RestConstants.REST_ROOM_USER_NOT_IN_ROOM_DESC);
				return;
			}
			
			roomTh.userReady(user.getId());
			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
		
	}
	
	@Override
	public void playerMove(ResponseDTO response, User user, Long roomId, 
			Short moveDirection) {
		try {
			if (moveDirection < 0) {
				fillResponseRequestError(response);
				return;
			}
						
			
			RoomThread roomTh = getRoomByUser(roomId, user);
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
			roomTh.userMove(user.getId(), moveDirection );
			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
		
	}
	

	@Override
	public void getRoomDetail(RoomDetailResponseDTO response, User user, Long roomId) {
		
		try {
			RoomThread roomTh = getRoomByUser(roomId, user);
			Room room = null;
			if (roomTh == null) {
				room = getFromNewsRoomByUser(roomId, user);
				if (room == null) {
					fillResponse(response, RestConstants.REST_ROOM_USER_NOT_IN_ROOM,
							RestConstants.REST_ROOM_USER_NOT_IN_ROOM_DESC);
					return;
				}
			}
			
			
			
			fillRoomDetail(response, roomTh, room, user.getId());			
			fillResponseGenericOk(response);
		} catch (Exception e) {
			fillResponseGenericError(response);
			e.printStackTrace();
			return;
		}
			
	}


	
	private Room getFromNewsRoomByUser(Long roomId, User user) {
		
		List<Room> matchedNewRooms = newRooms.stream().filter(r -> r.getId() == roomId 
				&& r.getPlayers().stream().map(p -> p.getId()).collect(Collectors.toList())
					.contains(user.getId())).collect(Collectors.toList());
		if (matchedNewRooms.size() == 1) {
			return matchedNewRooms.get(0);
		} 
		
		return null;
		
	}



	private void fillRoomDetail(RoomDetailResponseDTO response, RoomThread roomTh, Room room, Long playerId) {
		response.setRoomId(roomTh != null ? roomTh.getRoom().getId() : room.getId());
		response.setRoomSize(roomTh != null ? roomTh.getRoom().getSize() : room.getSize());
		response.setStarted(roomTh != null ? roomTh.isStarted() : false);
		response.setFinished(roomTh != null ? roomTh.isFinished() : false);
		response.setPlayers(roomTh != null ? fillUserDTO(roomTh) : fillUserDTO(room));
		response.setYouReady(roomTh != null ? roomTh.getUsersReady().get(playerId) : false);
		response.setRoomCreated(roomTh != null);
		
	}



	private List<UserDTO> fillUserDTO(Room room) {
		List<UserDTO> players = new ArrayList<>();
		
		for (User player : room.getPlayers()) {
			UserDTO playerDTO = new UserDTO();
			Long playerId = player.getId();
			
			playerDTO.setId(playerId);
			playerDTO.setUsername(player.getUsername());
			players.add(playerDTO);
		}
		return players;
	}



	private List<UserDTO> fillUserDTO(RoomThread roomTh) {
		List<UserDTO> players = new ArrayList<>();
		
		for (User player : roomTh.getRoom().getPlayers()) {
			UserDTO playerDTO = new UserDTO();
			Long playerId = player.getId();
			
			playerDTO.setId(playerId);
			playerDTO.setUsername(player.getUsername());
			playerDTO.setColor(roomTh.getUsersColors().get(playerId));
			playerDTO.setMoves(roomTh.getUsersMoves().get(playerId));
			playerDTO.setCoords(roomTh.getUsersCoords().get(playerId));
			players.add(playerDTO);
		}
		return players;
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



	private RoomThread getRoomByUser(Long roomId, User user) {
		
		List<RoomThread> matchedRooms = startedRooms.stream().filter(r -> r.getRoom().getId() == roomId 
				&& r.getRoom().getPlayers().stream().map(p -> p.getId()).collect(Collectors.toList())
					.contains(user.getId())).collect(Collectors.toList());
		if (matchedRooms.size() == 1) {
			return matchedRooms.get(0);
		}
		return null;
	}
	
	private boolean existsUser(Long userId) {
		synchronized (startedRooms) {
			synchronized (newRooms) {
				for (RoomThread roomTh : startedRooms) {
					Room room = roomTh.getRoom();
					boolean exists = existsUserInRoom(room, userId);
					if (exists) {
						return true;
					}
				}

				for (Room room : newRooms) {
					boolean exists = existsUserInRoom(room, userId);
					if (exists) {
						return true;
					}
				}
			}
		}
		return false;
	}


	private boolean existsUserInRoom(Room room, Long userId) {
		List<User> players = room.getPlayers();
		for (User player : players) {
			if (player.getId() == userId) {
				return true;
			}
		}		
		return false;
	}


	private boolean addNewPlayer (RoomResponseDTO response, User user, Integer roomSize) {
		synchronized (newRooms) {
			try {
				List<Room> roomsBySize = newRooms.stream()
					    .filter(p -> p.getSize() == roomSize).collect(Collectors.toList());
				
				if (roomsBySize.isEmpty()) {
					Room newRoom = new Room();
					newRoom.getPlayers().add(user);
					newRoom.setSize(roomSize);
					newRoom.setId(totalRooms);
					newRooms.add(newRoom);
					totalRooms++;
					response.setRoomId(newRoom.getId());
					
				} else {
					for (Room newRoom : roomsBySize) {
						boolean alreadyExists = existsUserInRoom(newRoom, user.getId());
						if (alreadyExists) {
							fillResponse(response, RestConstants.REST_ROOM_CREATE_EXISTING_USER,
									RestConstants.REST_ROOM_CREATE_EXISTING_USER_DESC);
							return false; 
						}
						
						List<User> roomPlayers = newRoom.getPlayers();
						roomPlayers.add(user);
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
