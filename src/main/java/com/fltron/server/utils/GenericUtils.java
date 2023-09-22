package com.fltron.server.utils;

import com.fltron.server.dto.responses.ResponseDTO;

public class GenericUtils {
	public static final void fillResponse(ResponseDTO response, String code, String description) {
		response.setResultCode(code);
		response.setResultDescription(description);;
	}
}
