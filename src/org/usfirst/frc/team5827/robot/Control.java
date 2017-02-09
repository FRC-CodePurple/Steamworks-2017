package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;

public class Control
{

	public Drive drive;
	public JoySticks controllers;
	public GearFlipper gearFlipper;
	public Gyro gyro;
	public Shifter shift;
	public Joystick joy;

	public boolean gyroOn;

	public Control()
	{
		gyroOn = true;
		drive = new Drive(new CANTalon(21), new CANTalon(22), new CANTalon(23), new CANTalon(31),
				new CANTalon(32), new CANTalon(33));

		controllers = new JoySticks();
		gearFlipper = new GearFlipper(.001, .00001, 0.0, 0, 41);
		
		gyro = new Gyro(.01, 0, 0);
		gyro.gyro.reset();
		gyro.gyro.calibrate();
		
		shift = new Shifter(11, 1, 0);
		joy = new Joystick(0);
	}

	public void Drive()
	{
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

		if (controllers.gearHold)
			gearFlipper.setTarget(1111);
		if (controllers.gearIn)
			gearFlipper.setTarget(1111);
		if (controllers.gearOut)
			gearFlipper.setTarget(1111);
	}

}
