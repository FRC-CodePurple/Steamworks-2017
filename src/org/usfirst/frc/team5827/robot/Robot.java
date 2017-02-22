
package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;

public class Robot extends IterativeRobot
{
	public Drive drive;
	public JoySticks controllers;
	public GearFlipper gearFlipper;
	public Gyro gyro;
	public Shifter shift;
	public Joystick joy;

	public CANTalon lift1;
	public CANTalon lift2;

	public boolean gyroOn;
	public boolean gearOut;

	public Timer gearTime;

	public int gearMode;
	public boolean driverControl;
	
	NetworkTable table; 

	@Override
	public void robotInit()
	{
		lift1 = new CANTalon(16);
		lift2 = new CANTalon(19);

		table = NetworkTable.getTable("table");
		
		gyroOn = false;
		drive = new Drive(new CANTalon(14), new CANTalon(11), new CANTalon(10), new CANTalon(18),
				new CANTalon(13), new CANTalon(15));

		gearTime = new Timer();
		controllers = new JoySticks();
		gearFlipper = new GearFlipper(.001, 0.0, 0.002, 17);

		gyro = new Gyro(.01, 0, 0);
		gyro.gyro.calibrate();

		shift = new Shifter(2, 1, 0);
		joy = new Joystick(0);

		gearMode = 0;
		driverControl = true;
		gearOut = false;
	}

	@Override
	public void disabledInit()
	{

	}

	@Override
	public void disabledPeriodic()
	{

	}

	@Override
	public void autonomousInit()
	{
		gyro.gyro.reset();
		gyro.gyro.calibrate();
		controllers.reset();
	}

	@Override
	public void autonomousPeriodic()
	{

	}

	@Override
	public void teleopInit()
	{
		gyro.gyro.reset();
		gyro.gyro.calibrate();
		controllers.reset();
	}

	@Override
	public void teleopPeriodic()
	{
		//displacement, Xdistance, Ydistance, angle
		System.out.println(table.getNumber("displacement",0));
		System.out.println(table.getNumber("X distance",0));  
		System.out.println(table.getNumber("Y distance",0));  
		System.out.println(table.getNumber("angle",0));       
		
		Drive();
	}

	@Override
	public void testPeriodic()
	{
	}

	public void Drive()
	{
		if (driverControl)
		{
			controllers.UpdateID(joy, 0);

			if (gyroOn)
				drive.arcadeDrive(controllers.speedValue, gyro.getPow());
			else
				drive.arcadeDrive(controllers.speedValue, controllers.turningValue);

			if (controllers.shiftUp)
				shift.shiftTo(2);
			if (controllers.shiftDown)
				shift.shiftTo(1);

			if (gearMode == 0)
			{
				if (controllers.gearOut)
				{
					gearFlipper.setTarget(-175);
				}
			} else if (gearMode == 1)
			{
				if (controllers.gearOut)
				{
					gearOut = true;
				}
			} else
			{
				// gearInCam();
			}

			if (controllers.gearHold)
			{
				gearFlipper.setTarget(0);
			}

			if (controllers.gearIn)
				gearFlipper.setTarget(700);

			if (joy.getRawButton(4))
			{
				lift1.set(-1);
				lift2.set(-1);
			} else
			{
				lift1.set(0);
				lift2.set(0);
			}
		}
		

		if (gearOut)
		{
			
			gearTime.start();
			System.out.println(gearTime.get());
			driverControl = false;
			drive.arcadeDrive(0, gyro.getPow());
			gearFlipper.setTarget(-175);
			if (gearTime.get() > 1)
			{
				drive.arcadeDrive(-.5, gyro.getPow());
			}
			if (gearTime.get() > 1.5)
			{
				drive.arcadeDrive(0, gyro.getPow());
				gearFlipper.setTarget(0);
				gearTime.reset();
				gearTime.stop();
				gearOut = false;
				driverControl = true;
			}
		}
		gyro.update(controllers.headingTarget);
		gearFlipper.update();
	}

}
