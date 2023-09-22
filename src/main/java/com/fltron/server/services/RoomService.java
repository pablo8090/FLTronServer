package com.fltron.server.services;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;

public interface RoomService {

	void joinRoom(RoomResponseDTO response, String userName, Integer roomSize);

	void playerReady(ResponseDTO response, String userName, Integer roomId);

	void playerMove(ResponseDTO response, String userName, Integer roomId, Short moveDirection, Short x, Short y);

}
