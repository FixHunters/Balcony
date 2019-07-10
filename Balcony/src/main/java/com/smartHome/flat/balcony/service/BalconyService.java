/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartHome.flat.balcony.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smartHome.flat.balcony.model.DataResponse;
import com.smartHome.flat.balcony.model.SensorsResponseEntity;
import com.smartHome.flat.balcony.sensors.GpioBalcony;
import com.smartHome.flat.balcony.sensors.TemperatureHumidity;

/**
 * Methods for control Balcony Raspberry Pi Zero W module.
 *
 * @author Jan Pojezdala
 */

@Component
public class BalconyService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	SensorsResponseEntity sensorsResponseEntity = new SensorsResponseEntity();
	GpioBalcony gpioBalcony = new GpioBalcony();
	TemperatureHumidity temperatureHumidity = new TemperatureHumidity();

	/**
	 * method for get BMP180 values
	 *
	 * 
	 * @return {@link SensorsResponseEntityReply} instance
	 */
	public SensorsResponseEntity getData() {

		try {
			DataResponse dataResponse = temperatureHumidity.main();
			dataResponse.setSoilStatus(gpioBalcony.waterCheck().isHigh());
			dataResponse.setRainStatus(gpioBalcony.rainDropsCheck().isHigh());
			dataResponse.setWaterPumpStatus(gpioBalcony.waterPumpCheck().isHigh());
			sensorsResponseEntity.setDataResponse(dataResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sensorsResponseEntity;
	}

	/**
	 * method for patch enable/disble waterPump
	 *
	 * 
	 * @return {@link SensorsResponseEntityReply} instance
	 */
	public Boolean patchWaterPump(String enabled) {
		Boolean value = null;
		try {
			if (enabled.equals("true")) {
				value = true;
				gpioBalcony.waterPumpStart();
			} else {
				value = false;
				gpioBalcony.waterPumpStop();
				;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * method for setup waterPumpAutomat
	 *
	 * 
	 * @return {@link SensorsResponseEntityReply} instance
	 */
	public void setAutomateWatering(String enable, String cycleTime) {
		try {
			gpioBalcony.waterPumpAutomat(Integer.valueOf(cycleTime));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	/**
	 * method for start automated watering job
	 *
	 * 
	 * @return
	 */
	 @Scheduled(cron = "${app.run.waterPump}")
	public void runCroneJob() {
		logger.info("Automated cron job started");
		try {
			gpioBalcony.waterPumpAutomat(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Automated cron finished");
	}

}
