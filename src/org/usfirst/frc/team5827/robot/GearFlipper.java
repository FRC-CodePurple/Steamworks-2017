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
	AnalogInput pos;

	public GearFlipper(double p, double i, double d, int motorID)
	{
		pid = new PID(p, i, d);
		motor = new CANTalon(motorID);
		target = 2.6;
		pid.multiplier = 1;
		pid.threshold = .6;
		pos = new AnalogInput(0);
		pid.iThresh = .05;
	}

	public void update()
	{
		pid.update(pos.getVoltage(), target);
		motor.set(pid.getPow());
		/*System.out.print("pos - " +pos.getVoltage());
		System.out.print(" targ - " + target);
		System.out.println("power - " + pid.getPow());		*/
	}

	public void setTarget(double target)
	{
		this.target = target;
	}

}