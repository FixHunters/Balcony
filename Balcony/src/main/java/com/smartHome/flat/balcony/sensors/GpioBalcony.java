package com.smartHome.flat.balcony.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * GPIO functions for Balcony Raspberry Pi Zero W
 *
 * @author Jan Pojezdala
 */
public class GpioBalcony {
	
	private static final Logger log = LoggerFactory.getLogger(GpioBalcony.class);
	
	Pin pinWaterPump = RaspiPin.GPIO_25; //TODO actually is it LED (GPIO_05) for testing, rele is GPIO_25 
	Pin pinRainSensor = RaspiPin.GPIO_27;	
	Pin pinRainSensorVcc = RaspiPin.GPIO_23;
	Pin pinSoilSensor = RaspiPin.GPIO_29;	
	
	 public PinState waterCheck() throws InterruptedException {
	    // create gpio controller
	    GpioController gpio = GpioFactory.getInstance();

	    // provision gpio pin as an input pin with its internal pull down resistor enabled
	    GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(pinSoilSensor, PinPullResistance.PULL_DOWN);
        log.info("<GpioBalcony> GPIO check state water on pinSoilSensor pin: " + pinSoilSensor + " State: " + inputPin.getState().toString());
        inputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        gpio.shutdown(); 
        gpio.unprovisionPin(inputPin);	      
		return inputPin.getState();
	 }
	 
	 public PinState rainDropsCheck() throws InterruptedException {
	    // create gpio controller
	    GpioController gpio = GpioFactory.getInstance();
	    
	    //Enable Vcc pin
	    GpioPinDigitalOutput inputPinOutputVccOut = gpio.provisionDigitalOutputPin(pinRainSensorVcc, PinState.HIGH);
        log.info("<GpioBalcony> Enable Vcc pin for RainDrops senzor, Pin: " + pinRainSensorVcc + " State: " + inputPinOutputVccOut.getState().toString());
	    
	    // provision gpio pin as an input pin with its internal pull down resistor enabled
	    GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(pinRainSensor, PinPullResistance.PULL_DOWN);
        log.info("<GpioBalcony> GPIO check state RainDrops sensor, Pin: " + pinRainSensor + " State: " + inputPin.getState().toString());
        inputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        inputPinOutputVccOut.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        gpio.shutdown(); 
        gpio.unprovisionPin(inputPin);	
        gpio.unprovisionPin(inputPinOutputVccOut);	
		return inputPin.getState();
	 }
	 
	 public PinState waterPumpCheck() throws InterruptedException {
	    // create gpio controller
	    GpioController gpio = GpioFactory.getInstance();

	    // provision gpio pin as an input pin with its internal pull down resistor enabled
	    GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(pinWaterPump);
        log.info("<GpioBalcony> GPIO check state checkWaterPump, Pin: " + pinWaterPump + " State: " + inputPin.getState().toString());
        PinState pinState = inputPin.getState();
        inputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        gpio.shutdown(); 
        gpio.unprovisionPin(inputPin);	      
		return pinState;
	 }
	 
	 public void waterPumpStart() throws InterruptedException {
	    // create gpio controller
	    GpioController gpio = GpioFactory.getInstance();
	    
	    // provision gpio pin as an output pin with PinState HIGH
	    GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(pinWaterPump, PinState.HIGH);
        log.info("<GpioBalcony> GPIO StartWaterPump, Pin: " + pinWaterPump + " State: " + outputPin.getState().toString());

        outputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        gpio.shutdown(); 
        gpio.unprovisionPin(outputPin);	     
	 }
	 
	 public void waterPumpStop() throws InterruptedException {
	    // create gpio controller
	    GpioController gpio = GpioFactory.getInstance();

	 // provision gpio pin as an output pin with PinState LOW
	    GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(pinWaterPump, PinState.LOW);
        log.info("<GpioBalcony> GPIO StopWaterPump, Pin: " + pinWaterPump + " State: " + outputPin.getState().toString());

        outputPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        gpio.shutdown(); 
        gpio.unprovisionPin(outputPin);     
	 }
	 
	public void waterPumpAutomat(int workTime) throws InterruptedException {
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput inputPinWaterCheck = gpio.provisionDigitalInputPin(pinSoilSensor,
				PinPullResistance.PULL_DOWN);
		GpioPinDigitalOutput outputPinWaterPump = gpio.provisionDigitalOutputPin(pinWaterPump);
		
		String lineSeparator = System.getProperty("line.separator");
		log.info("<GpioBalcony> GPIO WaterPumpAutomat, Pin: " + pinWaterPump + " State: "
				+ outputPinWaterPump.getState().toString() + lineSeparator + "PinWaterCheck, Pin: " + pinSoilSensor + " State: "
				+ inputPinWaterCheck.getState().toString());

		automatWaterPumpListener(inputPinWaterCheck, outputPinWaterPump, workTime);

		outputPinWaterPump.setState(PinState.LOW);
		inputPinWaterCheck.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		outputPinWaterPump.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		gpio.shutdown();
		gpio.unprovisionPin(inputPinWaterCheck);
		gpio.unprovisionPin(outputPinWaterPump);
	}

	public void automatWaterPumpListener(GpioPinDigitalInput inputPin, GpioPinDigitalOutput outputPin, int workTime)
			throws InterruptedException {

		log.info("<GpioBalcony> I turn on the water pump started as long as the soilSensor registers water on pin: " + inputPin
				+ "or does not set the watering time: " + workTime+"s");
		do {

			if (inputPin.getState().equals(PinState.HIGH))
				outputPin.setState(PinState.HIGH);
			Thread.sleep(1000);
			workTime--;
		} while (inputPin.getState().equals(PinState.HIGH) && workTime > 0);
		log.info("<GpioBalcony> I turn on the water pump finished");
	}		
	 
}