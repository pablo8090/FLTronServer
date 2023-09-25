package com.fltron.server.services;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomDetailResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.entities.User;

public interface RoomService {

	void joinRoom(RoomResponseDTO response, User user, Integer roomSize);

	void playerReady(ResponseDTO response, User user, Long roomId);

	void playerMove(ResponseDTO response, User user, Long roomId, Short moveDirection);

	void getRoomDetail(RoomDetailResponseDTO response, User user, Long roomId);

}
