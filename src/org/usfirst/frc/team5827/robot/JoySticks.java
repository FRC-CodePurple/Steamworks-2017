package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.Joystick;

public class JoySticks
{

	String xbox = "xbox Controller";
	String logiGamePad = "Controller (Gamepad F310)";
	String joyStick = "Logitech Extreme 3D";
	String SteeringWheel = "Wheel (XBOX 360 For Windows)";
	String GuitarHero = "xbox guitar";

	public double speedValue;
	public double turningValue;
	public boolean shiftUp;
	public boolean shiftDown;
	public boolean gearIn;
	public boolean gearOut;
	public boolean gearHold;
	public boolean gearMove;
	public boolean climbValueFast;
	public boolean climbValueSlow;
	public double leftDrive;
	public double rightDrive;
	public double headingTarget;
	public double speedNow;
	public boolean slowMode;
	public double gearAdjust;
	public Joystick joy;

	public JoySticks(int port)
	{
		speedValue = 0;
		turningValue = 0;
		shiftUp = false;
		shiftDown = false;
		gearIn = false;
		gearOut = false;
		gearHold = false;
		gearMove = false;
		climbValueFast = false;
		climbValueSlow = false;
		leftDrive = 0;
		rightDrive = 0;
		headingTarget = 0;
		speedNow = 0;
		slowMode = false;
		gearAdjust = 0;
		joy = new Joystick(port);

	}

	public void reset()
	{
		speedValue = 0;
		turningValue = 0;
		shiftUp = false;
		shiftDown = false;
		gearIn = false;
		gearOut = false;
		gearHold = false;
		gearMove = false;
		climbValueFast = false;
		climbValueSlow = false;
		leftDrive = 0;
		rightDrive = 0;
		headingTarget = 0;
		speedNow = 0;
		slowMode = false;
		gearAdjust = 0;
	}

	public void UpdateID(int driveType)
	{
		String name = joy.getName();
		if (name.equals(xbox) || name.equals(logiGamePad))
		{
			if (driveType == 0)
				speedValue = joy.getRawAxis(3) - joy.getRawAxis(2);
			else
				speedValue = joy.getRawAxis(1);

			leftDrive = joy.getRawAxis(1);
			rightDrive = joy.getRawAxis(5);

			turningValue = joy.getRawAxis(0);
			headingTarget += joy.getRawAxis(0) * 2;

			shiftUp = joy.getRawButton(5);
			shiftDown = joy.getRawButton(6);

			gearHold = joy.getRawButton(1);
			gearOut = joy.getRawButton(2);
			gearIn = joy.getRawButton(3);

			slowMode = joy.getRawButton(4);
			gearAdjust = joy.getRawAxis(5);

			climbValueFast = joy.getRawButton(8);
			climbValueSlow = joy.getRawButton(7);

		}

		if (slowMode)
		{
			speedValue /= 2;
			turningValue/= 2;
		}

		if (Math.abs(gearAdjust) < .1)
		{
			gearAdjust = 0;
		}
	}

}
