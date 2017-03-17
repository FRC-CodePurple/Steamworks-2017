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
	public double climbValue;
	public double leftDrive;
	public double rightDrive;
	public double headingTarget;
	public double speedNow;
	public boolean slowMode;
	public double gearAdjust;

	public JoySticks()
	{
		speedValue = 0;
		turningValue = 0;
		shiftUp = false;
		shiftDown = false;
		gearIn = false;
		gearOut = false;
		gearHold = false;
		gearMove = false;
		climbValue = 0;
		leftDrive = 0;
		rightDrive = 0;
		headingTarget = 0;
		speedNow = 0;
		slowMode = false;
		gearAdjust = 0;

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
		climbValue = 0;
		leftDrive = 0;
		rightDrive = 0;
		headingTarget = 0;
		speedNow = 0;
		slowMode = false;
		gearAdjust = 0;
	}

	public void UpdateID(Joystick joy, int driveType)
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
			
			slowMode = joy.getRawButton(8);
			gearAdjust = joy.getRawAxis(5);
			
			
			

		} else if (name.equals(joyStick))
		{

			speedValue = joy.getRawAxis(1);
			turningValue = joy.getRawAxis(2);
			headingTarget += joy.getRawAxis(2);

			shiftUp = joy.getRawButton(5);
			shiftDown = joy.getRawButton(6);

		} else if (name.equals(SteeringWheel))
		{
			speedValue = joy.getRawAxis(2) - joy.getRawAxis(3);
			turningValue = joy.getRawAxis(0);
			headingTarget += joy.getRawAxis(0);

			shiftUp = joy.getRawButton(2);
			shiftDown = joy.getRawButton(1);

		}
		
		if(slowMode)
		{
			speedValue /= 2;
		}
		
		if(Math.abs(gearAdjust) < .1)
		{
			gearAdjust = 0;
		}
	}
	
	
}
