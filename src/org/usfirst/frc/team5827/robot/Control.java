package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;

public class Control
{

	public Drive drive;
	public JoySticks controllers;
	public GearFlipper gearFlipper;
	public Gyro gyro;
	public Shifter shift;
	public Joystick joy;

	public Victor lift1;
	public Victor lift2;

	public boolean gyroOn;

	public Timer gearTime;

	public int gearMode;

	public Control()
	{
		lift1 = new Victor(0);
		lift2 = new Victor(2);

		gyroOn = true;
		drive = new Drive(new CANTalon(21), new CANTalon(22), new CANTalon(23), new CANTalon(31),
				new CANTalon(32), new CANTalon(33));

		gearTime = new Timer();
		controllers = new JoySticks();
		gearFlipper = new GearFlipper(.001, 0.0, 0.002, 0, 1);

		gyro = new Gyro(.01, 0, 0);
		gyro.gyro.reset();
		gyro.gyro.calibrate();

		shift = new Shifter(11, 1, 0);
		joy = new Joystick(0);

		gearMode = 1;
		/*
		 * 0 = manual contron 1 = auto without vision 2 = auto with vision
		 */

	}

	public void Drive()
	{
		if (gearTime.get() > 1)
		{
			gearTime.reset();
			gearTime.stop();
			drive.enable();
		}

		controllers.UpdateID(joy, 0);

		gyro.update(controllers.headingTarget);
		if (gyroOn)
			drive.arcadeDrive(controllers.speedValue, gyro.getPow());
		else
			drive.arcadeDrive(controllers.speedValue, controllers.turningValue);

		if (controllers.shiftUp)
			shift.shiftTo(2);
		if (controllers.shiftDown)
			shift.shiftTo(1);

		gearFlipper.update();

		if (gearMode == 0)
		{
			if (controllers.gearOut)
			{
				gearFlipper.setTarget(3400);
			}
		} else if (gearMode == 1)
		{
			if (controllers.gearOut)
			{
				gearOut();
			}
		} else
		{
			// gearInCam();
		}

		if (controllers.gearHold)
		{
			gearFlipper.setTarget(2615);
		}

		if (controllers.gearIn)
		{
			gearFlipper.setTarget(1600);
		}

		if (joy.getRawButton(4))
		{
			lift1.set(-1);
			lift2.set(-1);
		} else
		{
			lift1.set(0);
			lift2.set(0);
		}
		System.out.println(gearFlipper.target + "-");
	}

	public void gearOut()
	{
		gearFlipper.setTarget(3400);
		gearTime.start();
		drive.disable();
	}

}
