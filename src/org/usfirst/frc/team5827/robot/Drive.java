package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Drive
{
	public CANTalon r1;
	public CANTalon r2;
	public CANTalon r3;
	public CANTalon l1;
	public CANTalon l2;
	public CANTalon l3;
	public boolean enabled;

	public Drive(CANTalon left1, CANTalon left2, CANTalon left3, CANTalon right1, CANTalon right2,
			CANTalon right3)
	{
		r1 = right1;
		r2 = right2;
		r3 = right3;
		l1 = left1;
		l2 = left2;
		l3 = left3;
		enabled = true;
	}

	public void tankDrive(double left, double right)
	{
		if (enabled)
		{
			r1.set(right);
			r2.set(right);
			r3.set(right);
			l1.set(-left);
			l2.set(-left);
			l3.set(-left);
		}
		else
		{
			r1.set(0);
			r2.set(0);
			r3.set(0);
			l1.set(0);
			l2.set(0);
			l3.set(0);
		}
	}

	public void arcadeDrive(double power, double turn)
	{
		tankDrive(power + turn, power - turn);
	}

	public void powerManagment()
	{
		double vr1 = r1.getOutputCurrent();
		double vr2 = r2.getOutputCurrent();
		double vr3 = r3.getOutputCurrent();
		double vl4 = l1.getOutputCurrent();
		double vl5 = l2.getOutputCurrent();
		double vl6 = l3.getOutputCurrent();

	}

	public void disable()
	{
		enabled = false;
	}

	public void enable()
	{
		enabled = true;
	}
}
