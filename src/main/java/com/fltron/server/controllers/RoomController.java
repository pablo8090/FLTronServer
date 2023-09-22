package com.fltron.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.services.RoomService;

@RestController
@RequestMapping("/room")
public class RoomController extends BaseController {
	
	@Autowired
	RoomService roomService;

	@GetMapping("/joinRoom")
	public RoomResponseDTO joinRoom (@RequestParam String userName, 
			@RequestParam(defaultValue="2") Integer roomSize) {
		RoomResponseDTO response = new RoomResponseDTO();
		try {
			
			roomService.joinRoom(response, userName, roomSize);
			
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
}
