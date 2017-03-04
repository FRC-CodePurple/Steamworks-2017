
package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
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
	public Timer autoTime;

	public PID drivePID;

	public CANTalon lift1;
	public CANTalon lift2;

	public boolean gyroOn;
	public boolean gearOut;

	public Timer gearTime;

	public int gearMode;
	public boolean driverControl;
	public int autoSel = 0;
	public Encoder encLeft;
	public Encoder encRight;
	
	NetworkTable table;

	@Override
	public void robotInit()
	{
		autoTime = new Timer();
		lift1 = new CANTalon(16);
		lift2 = new CANTalon(19);

		drivePID = new PID(.01, 0, 0);
		table = NetworkTable.getTable("table");

		gyroOn = false;
		drive = new Drive(new CANTalon(14), new CANTalon(11), new CANTalon(10), new CANTalon(18),
				new CANTalon(13), new CANTalon(15));

		gearTime = new Timer();
		controllers = new JoySticks();
		gearFlipper = new GearFlipper(1.2, .01, 0.25, 17);

		gyro = new Gyro(.01, 0, 0);
		gyro.pid.multiplier = .5;
		gyro.gyro.calibrate();

		shift = new Shifter(2, 1, 0);
		joy = new Joystick(0);

		gearMode = 0;
		driverControl = true;
		gearOut = false;

		encLeft = new Encoder(0, 1);
		encRight = new Encoder(2, 3);
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
		/*
		 * gyro.gyro.reset(); gyro.gyro.calibrate();
		 */
		controllers.reset();
		autoTime.reset();
		autoTime.start();
		gearFlipper.setTarget(2.6);
	}

	@Override
	public void autonomousPeriodic()
	{
		System.out.println(autoTime.get());
		if(autoSel == 0)
		{
			if (autoTime.get() < 5)
			{
				drive.arcadeDrive(.5, 0);
			}
			else drive.arcadeDrive(0, 0);
		}
		if (autoSel == 1)
		{
			if (autoTime.get() < 3)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2, 1.181);
				gyro.update(0);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else if (autoTime.get() < 4)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2, 1.181);
				gyro.update(60);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else if (autoTime.get() < 7)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2,
						1.181 + 1.33858);
				gyro.update(60);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else 
			{
				gearFlipper.setTarget(2.0);
			}

		}
		if (autoSel == 2)
		{
			drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2, 1.181);
			drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			gearFlipper.setTarget(2.0);
		}
		if (autoSel == 3)
		{
			if (autoTime.get() < 3)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2, 1.181);
				gyro.update(0);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else if (autoTime.get() < 4)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2, 1.181);
				gyro.update(-60);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else if (autoTime.get() < 7)
			{
				drivePID.update((encLeft.getDistance() + encRight.getDistance()) / 2,
						1.181 + 1.33858);
				gyro.update(-60);
				drive.arcadeDrive(drivePID.getPow(), gyro.getPow());
			} else
			{
				gearFlipper.setTarget(2.0);
			}
		}
		gearFlipper.update();

	}

	@Override
	public void teleopInit()
	{
		/*
		 * gyro.gyro.reset(); gyro.gyro.calibrate();
		 */
		controllers.reset();
	}

	@Override
	public void teleopPeriodic()
	{
		// displacement, Xdistance, Ydistance, angle
		/*System.out.println(table.getNumber("displacement", 0));
		System.out.println(table.getNumber("X distance", 0));
		System.out.println(table.getNumber("Y distance", 0));
		System.out.println(table.getNumber("angle", 0));*/

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
					gearFlipper.setTarget(2.0);
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
				gearFlipper.setTarget(2.7);
			}

			if (controllers.gearIn)
			{
				gearFlipper.setTarget(4.2);
				System.out.println("Press!");
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
		}

		if (gearOut)
		{

			/*
			 * gearTime.start(); System.out.println(gearTime.get());
			 * driverControl = false; drive.arcadeDrive(0, gyro.getPow());
			 * gearFlipper.setTarget(-175); if (gearTime.get() > 1) {
			 * drive.arcadeDrive(-.5, gyro.getPow()); } if (gearTime.get() >
			 * 1.5) { drive.arcadeDrive(0, gyro.getPow());
			 * gearFlipper.setTarget(2.6); gearTime.reset(); gearTime.stop();
			 * gearOut = false; driverControl = true; }
			 */
		}
		gyro.update(controllers.headingTarget);
		gearFlipper.update();
	}

}
