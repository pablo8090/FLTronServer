package com.fltron.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CoordsDTO {
	short x;
	short y;
	
	@Override
	public String toString() {
		return "[" + x + ", " + y +"] ";
	}
}
