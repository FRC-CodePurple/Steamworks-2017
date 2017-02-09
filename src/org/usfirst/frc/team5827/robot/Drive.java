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

	public Drive(CANTalon left1, CANTalon left2, CANTalon left3, CANTalon right1, CANTalon right2,
			CANTalon right3)
	{
		r1 = right1;
		r2 = right2;
		r3 = right3;
		l1 = left1;
		l2 = left2;
		l3 = left3;
	}

	public void tankDrive(double left, double right)
	{
		r1.set(right);
		r2.set(right);
		r3.set(right);
		l1.set(-left);
		l2.set(-left);
		l3.set(-left);
	}

	public void arcadeDrive(double power, double turn)
	{
		tankDrive(power + turn, power - turn);
	}

}
