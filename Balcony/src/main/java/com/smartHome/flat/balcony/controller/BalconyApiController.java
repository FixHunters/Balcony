package com.smartHome.flat.balcony.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartHome.flat.balcony.model.SensorsResponseEntity;
import com.smartHome.flat.balcony.service.BalconyService;

@Controller
@RestController
public class BalconyApiController implements BalconyApi {

	private static final Logger log = LoggerFactory.getLogger(BalconyApiController.class);

	private final HttpServletRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public BalconyApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.request = request;
	}

	@Autowired
	private BalconyService balconyService;

	@Override
	public ResponseEntity<SensorsResponseEntity> getData() {
		return new ResponseEntity<SensorsResponseEntity>(balconyService.getData(), HttpStatus.OK);
	}

	@Override
	public Boolean patchWaterPump() {
		return balconyService.patchWaterPump(request.getHeader("enabled"));

	}

	@Override
	public void setAutomateWatering() {
		String cycleTime = request.getHeader("cycleTime");
		log.debug("Cycle was set on time" + cycleTime);
		balconyService.setAutomateWatering("true", cycleTime);
		return;

	}

}
