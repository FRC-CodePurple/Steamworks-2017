package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Gyro
{
	public PID pid;
	public ADXRS450_Gyro gyro;

	public Gyro(double p, double i, double d)
	{
		pid = new PID(p, i, d);
		gyro = new ADXRS450_Gyro();
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
