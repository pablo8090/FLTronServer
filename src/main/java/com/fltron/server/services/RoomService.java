package com.fltron.server.services;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;

public interface RoomService {

	void joinRoom(ResponseDTO response, String userName, Integer roomSize);

}
