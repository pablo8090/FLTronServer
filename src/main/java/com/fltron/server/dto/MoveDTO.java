package com.fltron.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MoveDTO {
	short moveDirection;
	CoordsDTO coords;
	
	@Override
	public String toString() {
		return "[" + coords.getX() + ", " + coords.getY() +"] ";
	}
}
