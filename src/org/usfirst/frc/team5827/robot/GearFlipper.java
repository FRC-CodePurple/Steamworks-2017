package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;

public class GearFlipper
{
	PID pid;
	CANTalon motor;
	double target;

	public GearFlipper(double p, double i, double d, int motorID)
	{
		pid = new PID(p, i, d);
		motor = new CANTalon(motorID);
		target = 0;
		pid.multiplier = .30;
		
	}

	public void update()
	{
		pid.update(motor.getEncPosition(), target);
		motor.set(pid.getPow());
		System.out.println(motor.getEncPosition());
	}

	public void setTarget(double target)
	{
		this.target = target;
	}

}