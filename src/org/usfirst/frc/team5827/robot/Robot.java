
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
import edu.wpi.first.wpilibj.CameraServer;
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

	public PID leftDrive;
	public PID rightDrive;

	public CANTalon lift1;
	public CANTalon lift2;

	public boolean gyroOn;
	public boolean gearOut;

	public Timer gearTime;
	Boolean gearStop;
	String disFlip;

	public int gearMode;
	public boolean driverControl;
	public int autoSel = 4;

	public int step;
	
	public Climber climb;

	NetworkTable table;

	@Override
	public void robotInit()
	{
		startCamera();
		gearStop = false;

		autoTime = new Timer();
		//lift1 = new CANTalon(16);
		//lift2 = new CANTalon(19);

		leftDrive = new PID(.000007, 0.000001, 0);
		rightDrive = new PID(.000007, 0.000001, 0);

		table = NetworkTable.getTable("table");

		gyroOn = false;
		drive = new Drive(new CANTalon(14), new CANTalon(11), new CANTalon(10), new CANTalon(18),
				new CANTalon(13), new CANTalon(15));

		gearTime = new Timer();
		controllers = new JoySticks();
		gearFlipper = new GearFlipper(1.2, .01, 0.25, 17);

		climb = new Climber(16,19,20,0);
		// gyro = new Gyro(.01, 0, 0);
		// gyro.pid.multiplier = .5;
		// gyro.gyro.calibrate();

		shift = new Shifter(2, 1, 0);
		joy = new Joystick(0);

		gearMode = 0;
		driverControl = true;
		gearOut = false;

		step = 0;

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
		resetEnc();
		/*
		 * gyro.gyro.reset(); gyro.gyro.calibrate();
		 */
		controllers.reset();
		autoTime.reset();
		autoTime.start();
		gearFlipper.setTarget(2.6);
		shift.shiftTo(1);
		drive.setBrake(true);
		// step = 0;
	}

	@Override
	public void autonomousPeriodic()
	{
		gearFlipper.update();

		if (autoSel == 0)
		{
			if (autoTime.get() < 3)
			{
				drive.arcadeDrive(.5, 0);
			} else
				drive.arcadeDrive(0, 0);
		}

		if (autoSel == 3)
		{
			System.out.println(getLeftEnc() + "*****" + getRightEnc() + "\n" + step + "*****"
					+ autoTime.get() + "!");
			if (step == 0)// FWD
			{

				leftDrive.update(getLeftEnc(), 16000);
				rightDrive.update(getRightEnc(), 16000);
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);

				if (Math.abs(leftDrive.errorN) < 500 && Math.abs(rightDrive.errorN) < 500)
				{

					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
				}

			} else if (step == 1 && autoTime.get() > 1)// Out
			{
				gearFlipper.setTarget(2.0);
				leftDrive.update(getLeftEnc(), getLeftEnc());
				rightDrive.update(getRightEnc(), getRightEnc());
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);

				if (Math.abs(leftDrive.errorN) < 500 && Math.abs(rightDrive.errorN) < 500
						&& Math.abs(gearFlipper.pid.errorN) < .1)
				{
					gearFlipper.setTarget(2.0);
					System.out.println("reset");
					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
				}
			} else if (step == 2 && autoTime.get() > 4)// Back
			{
				System.out.println("Time:" + autoTime.get());
				leftDrive.update(getLeftEnc(), 8000);
				rightDrive.update(getRightEnc(), 8000);
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);
			}
		}

		if (autoSel == 4)
		{
			System.out.println(getLeftEnc() + "*****" + getRightEnc() + "\n" + step + "*****"
					+ autoTime.get() + "!");
			if (step == 0)// FWD
			{

				leftDrive.update(getLeftEnc(), 16000);
				rightDrive.update(getRightEnc(), 16000);
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);

				if (leftDrive.check && rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
				}

			} else if (step == 1 && autoTime.get() > 1)// Out
			{
				gearFlipper.setTarget(2.0);
				leftDrive.update(getLeftEnc(), getLeftEnc());
				rightDrive.update(getRightEnc(), getRightEnc());
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);

				if (leftDrive.check && rightDrive.check && Math.abs(gearFlipper.pid.errorN) < .1)
				{
					gearFlipper.setTarget(2.0);
					System.out.println("reset");
					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
				}
			} else if (step == 2 && autoTime.get() > 4)// Back
			{
				System.out.println("Time:" + autoTime.get());
				leftDrive.update(getLeftEnc(), 8000);
				rightDrive.update(getRightEnc(), 8000);
				drive.tankDrive(leftDrive.getPow() / 2, rightDrive.getPow() / 2);
			}
		}

		gearFlipper.update();

	}

	@Override
	public void teleopInit()
	{
		resetEnc();
		/*
		 * gyro.gyro.reset(); gyro.gyro.calibrate();
		 */
		drive.setBrake(false);
		controllers.reset();
	}

	@Override
	public void teleopPeriodic()
	{
		// displacement, Xdistance, Ydistance, angle
		/*
		 * System.out.println(table.getNumber("displacement", 0));
		 * System.out.println(table.getNumber("X distance", 0));
		 * System.out.println(table.getNumber("Y distance", 0));
		 * System.out.println(table.getNumber("angle", 0));
		 */
		disFlip = SmartDashboard.getString("DB/String 0", "myDefaultData");
		if (disFlip.equalsIgnoreCase("N"))
		{
			gearStop = true;
		} else
			gearStop = false;
		System.out.println(gearStop);
		Drive();
	}

	@Override
	public void testPeriodic()
	{
	}

	public double Turn(int turn)
	{
		double Go = (3.14 * 22) / (360 / turn); // Returns Inch Value
		return Go * 97.22;

	}

	public void Drive()
	{
		System.out.println(getRightEnc() + "    " + getLeftEnc());
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

			if (controllers.gearOut)
			{
				gearFlipper.setTarget(1.9);
			}

			if (controllers.gearHold)
			{
				gearFlipper.setTarget(2.7);
			}

			if (controllers.gearIn)
			{
				gearFlipper.setTarget(4.2);
			}
			
			gearFlipper.setTarget(gearFlipper.target + (controllers.gearAdjust/100));
			if(gearFlipper.target > 4.4) gearFlipper.setTarget(4.4);
			if(gearFlipper.target < 1.7) gearFlipper.setTarget(1.7);

			if (joy.getRawButton(4))
			{
				//lift1.set(-1);
				//lift2.set(-1);
				climb.update(.5);
			} else
			{
				climb.update(0);
				//lift1.set(0);
				//lift2.set(0);
			}
			gearFlipper.update();
		}
	}

	public void startCamera()
	{
		try
		{
			CameraServer.getInstance().startAutomaticCapture();
		} catch (Exception e)
		{
			System.out.println("Camera did not start.");
		}
	}

	public double getRightEnc()
	{
		return drive.r2.getEncPosition();
	}

	public double getLeftEnc()
	{
		return -drive.l3.getEncPosition();
	}

	public void resetEnc()
	{
		drive.l3.setEncPosition(0);
		drive.r2.setEncPosition(0);
	}

}
