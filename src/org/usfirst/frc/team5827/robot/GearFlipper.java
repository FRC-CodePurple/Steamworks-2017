package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;

public class GearFlipper
{
	PID pid;
	AnalogInput location;
	CANTalon motor;
	double target;

	public GearFlipper(double p, double i, double d, int inputPort, int motorID)
	{
		pid = new PID(p, i, d);
		location = new AnalogInput(inputPort);
		motor = new CANTalon(motorID);
		target = 0;
	}

	public void update()
	{
		pid.update(location.getValue(), target);
		motor.set(pid.getPow());
	}

	public void setTarget(double target)
	{
		this.target = target;
	}

}