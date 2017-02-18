package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Victor;

public class GearFlipper
{
	PID pid;
	AnalogInput location;
	Victor motor;
	double target;

	public GearFlipper(double p, double i, double d, int inputPort, int motorID)
	{
		pid = new PID(p, i, d);
		location = new AnalogInput(inputPort);
		motor = new Victor(motorID);
		target = 2560;
		pid.multiplier = .5;
	}

	public void update()
	{
		pid.update(location.getValue(), target);
		motor.set(-pid.getPow());
		System.out.println(location.getValue());;
	}

	public void setTarget(double target)
	{
		this.target = target;
	}

}