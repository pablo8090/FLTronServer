package com.fltron.server.services;

import com.fltron.server.dto.responses.ResponseDTO;

public interface RoomService {

	void joinRoom(ResponseDTO response, String userName);

}
