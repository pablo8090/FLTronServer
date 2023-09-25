package com.fltron.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomDetailResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.entities.User;
import com.fltron.server.services.RoomService;

@RestController
@RequestMapping("/room")
public class RoomController extends BaseController {
	
	@Autowired
	RoomService roomService;

	@GetMapping("/joinRoom")
	public RoomResponseDTO joinRoom (@RequestParam String email, 
			@RequestParam(defaultValue="2") Integer roomSize) {
		RoomResponseDTO response = new RoomResponseDTO();
		try {
			User user = getSessionUser(email);
			if (user == null) {
				fillAuthErrorResponse(response);
				return response;
			}
			roomService.joinRoom(response, user, roomSize);
			
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
	
	@GetMapping("/getRoomDetail")
	public RoomDetailResponseDTO getRoomDetail (@RequestParam String email, 
			@RequestParam Long roomId) {
		RoomDetailResponseDTO response = new RoomDetailResponseDTO();
		try {
			User user = getSessionUser(email);
			if (user == null) {
				fillAuthErrorResponse(response);
				return response;
			}
			roomService.getRoomDetail(response, user, roomId);
			
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
	
	@PostMapping("/playerReady")
	public ResponseDTO playerReady (@RequestParam String email, 
			@RequestParam Long roomId) {
		ResponseDTO response = new ResponseDTO();
		try {
			User user = getSessionUser(email);
			if (user == null) {
				fillAuthErrorResponse(response);
				return response;
			}
			roomService.playerReady(response, user, roomId);
			
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
	
	@PostMapping("/playerMove")
	public ResponseDTO playerMove (@RequestParam String email, 
			@RequestParam Long roomId,
			@RequestParam Short moveDirection) {
		ResponseDTO response = new ResponseDTO();
		try {
			User user = getSessionUser(email);
			if (user == null) {
				fillAuthErrorResponse(response);
				return response;
			}
			roomService.playerMove(response, user, roomId, moveDirection);
			
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
}
