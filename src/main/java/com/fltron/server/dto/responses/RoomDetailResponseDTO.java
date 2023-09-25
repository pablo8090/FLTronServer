package com.fltron.server.dto.responses;

import java.util.List;

import com.fltron.server.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoomDetailResponseDTO extends RoomResponseDTO {
	boolean started;
	List<UserDTO> players;
	boolean finished;
	Boolean youReady;
	int roomSize;
	Boolean roomCreated;
}
