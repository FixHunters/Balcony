package com.smartHome.flat.balcony.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartHome.flat.balcony.BalconyApplication;
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
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DynamicScheduler dynamicScheduler;
	
    @PostMapping("/restart")
    public void restart() {
    	System.out.println("Show properties, default profiles : "+env.getDefaultProfiles().toString());
    	System.out.println("Show properties, activeProfile : "+env.getActiveProfiles().toString());
    	System.out.println("Show properties, app.run.waterPump : "+env.getProperty("app.run.waterPump"));
        //BalconyApplication.restart();
    } 

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

	@Override
	public Double getAdc() {
		log.debug("Call ADC converter");
		return balconyService.setConverterADC();
	}

	@Override
	public Boolean getPir() {
		log.debug("Call PIR sensor");
		return balconyService.getPIR();
	}
	
    @PostMapping("/python")
    public void executePython() {
    	String mode = request.getHeader("mode");
    	balconyService.runPython(mode);
    } 
    
    @PostMapping("/crone")
    public void setCroneJob() {
    	String croneEx = request.getHeader("croneEx");
    	dynamicScheduler.setActivate(croneEx);
    }   

}
