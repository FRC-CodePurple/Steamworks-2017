package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Shifter
{
	DoubleSolenoid shift;

	public Shifter(int id, int forward, int backward)
	{
		shift = new DoubleSolenoid(id, forward, backward);
	}

	public void shiftTo(int gear)
	{
		if (gear == 1)
			shift.set(DoubleSolenoid.Value.kForward);
		if (gear == 2)
			shift.set(DoubleSolenoid.Value.kReverse);
	}

	public void automatic()
	{

	}
}
