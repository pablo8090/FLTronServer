package com.fltron.server.controllers;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

public class BaseController {
	protected void fillGenericErrorResponse (ResponseDTO response) {
		GenericUtils.fillResponse(response, RestConstants.REST_GENERIC_ERROR, 
				RestConstants.REST_GENERIC_ERROR_DESC);
	}
}
