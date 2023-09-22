package com.fltron.server.services.impl;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.services.BaseService;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

public class BaseServiceImpl implements BaseService {
	protected void fillResponse(ResponseDTO response, String code, String description) {
		GenericUtils.fillResponse(response, code, description);
	}
	
	protected void fillResponseGenericError(ResponseDTO response) {
		fillResponse(response, RestConstants.REST_GENERIC_SERVICE_ERROR, 
				RestConstants.REST_GENERIC_SERVICE_ERROR_DESC);
	}
	
	protected void fillResponseRequestError (ResponseDTO response) {
		fillResponse(response, RestConstants.REST_GENERIC_ERROR_PARAM, 
				RestConstants.REST_GENERIC_ERROR_PARAM_DESC);		
	}
	
	protected void fillResponseGenericOk(ResponseDTO response) {
		fillResponse(response, RestConstants.REST_GENERIC_SERVICE_OK, 
				RestConstants.REST_GENERIC_SERVICE_OK_DESC);
	}
}
