package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;

public class Gyro
{
	public PID pid;
	public AnalogGyro gyro;

	public Gyro(double p, double i, double d, double threshold, double ErrorThreshold, double multiplier)
	{
		pid = new PID(p, i, d, threshold, ErrorThreshold, multiplier);
		gyro = new AnalogGyro(1);
	}

	public void update(double target)
	{
		pid.update(gyro.getAngle(), target);
	}

	public double getPow()
	{
		double pow = pid.getPow();
		return pow;
	}
}
