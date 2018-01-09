
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
	public Shifter shift;

	public Timer autoTime;

	public PID leftDrive;
	public PID rightDrive;

	public boolean gearOut;

	public Timer gearTime;
	Boolean gearStop;
	String disFlip;

	public int gearMode;
	public boolean driverControl;
	public int autoSel = 3; // 0 = baseline 1 = middle 2 left 3 right
	

	public int step;
	public boolean boiler;
	public Gyro gyro;
	public Climber climb;
	public Joystick cypress;
	
	public boolean right = false;
	public boolean left = false;
	public boolean middle = false;
	
	public double leg1 = 0;
	public double turn = 0;
	public double leg2 = 0;
	

	public boolean gear = true;
	@Override
	public void robotInit()
	{
		cypress = new Joystick(1);
		startCamera();
		gearStop = false;

		gyro = new Gyro(.1, 0, 0, .6, 5, 1);
		autoTime = new Timer();
		// lift1 = new CANTalon(16);
		// lift2 = new CANTalon(19);

		leftDrive = new PID(.00001, 0.000001, 0, .5, 500, .1, 1);
		rightDrive = new PID(.001, 0.000000, 0.001, 1, 750, .1, 1);

		drive = new Drive(new CANTalon(14), new CANTalon(11), new CANTalon(10), new CANTalon(18),
				new CANTalon(13), new CANTalon(15));

		gearTime = new Timer();
		controllers = new JoySticks(0);
		gearFlipper = new GearFlipper(1.5, .01, 0.25, .65, .1, .1, 1, 17);

		climb = new Climber(16, 19, 12, 0);

		shift = new Shifter(2, 1, 0);

		gearMode = 0;
		driverControl = true;
		gearOut = false;

		step = 0;
		gyro.gyro.reset();
		gyro.gyro.calibrate();

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
		step = 0;
		controllers.reset();
		autoTime.reset();
		autoTime.start();
		gearFlipper.setTarget(2.6);
		shift.shiftTo(1);
		drive.setBrake(true);
		gyro.gyro.reset();
		if(cypress.getRawButton(1))
		{
			right = false;
			left = true;
			middle = false;
			//autoSel = 2;
		}
		else if(cypress.getRawButton(2))
		{
			right = true;
			left = false;
			middle = false;
			//autoSel = 3;
		}
		else 
		{//autoSel = 1;
			right = false;
			left = false;
			middle = true;
		}
		
		// step = 0;
		if(cypress.getRawButton(10))
		{
			gear = true;
		}
		else gear = false;
		
		if(cypress.getRawButton(9))
			boiler = true;
		else boiler = false;
		
		System.out.println(" Boiler: "+boiler+" Gear: "+gear+" left: "+left+" right:"+right);
		
		if(left && boiler)
		{
			leg1 = 32000;
			turn = -14;
			leg2 = 28100;
		}
		else if(left && !boiler)
		{
			leg1 = 34500;
			turn = -14;
			leg2 = 32000;
		}
		else if(right && boiler)
		{
			leg1 = 37000;
			turn = 14;
			leg2 = 26800;
		}
		else if(right && !boiler)
		{
			leg1 = 28500;
			turn = 14;
			leg2 = 31400;
		}
		
			
	}

	@Override
	public void autonomousPeriodic()
	{

		if (middle)
		{
			System.out.println(getRightEnc() + "*****" + gyro.gyro.getAngle() + "\n" + rightDrive.P
					+ "**" + rightDrive.I + "**" + rightDrive.D);
			if (step == 0)// FWD
			{

				gyro.update(0);
				rightDrive.update(getRightEnc(), 35800);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
					System.out.println("\n \n \n");
				}

			} else if (step == 1 && autoTime.get() > .5)// Out
			{
				if(gear) gearFlipper.setTarget(1.9);

				drive.arcadeDrive(0, 0);

				if (gearFlipper.pid.check && gear)
				{
					gearFlipper.setTarget(1.9);

					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
					System.out.println("\n \n \n");
				}
			} else if (step == 2 && autoTime.get() > 1)// Back
			{
				rightDrive.update(getRightEnc(), -10000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());
				System.out.println("\n \n \n");
			}
		}

		if (left || right)
		{
			
			System.out.println(getRightEnc() + "*****" + gyro.gyro.getAngle() + "\n" + rightDrive.P
					+ "**" + rightDrive.I + "**" + rightDrive.D);
			if (step == 0)// FWD
			{

				gyro.update(0);
				rightDrive.update(getRightEnc(), leg1);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
				}

			} else if (step == 1 && autoTime.get() > .5)// right
			{
				gyro.update(turn);
				drive.arcadeDrive(0, -gyro.getPow());

				if (gyro.pid.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
				}
			} else if (step == 2 && autoTime.get() > .5)// FWD
			{

				gyro.update(turn);
				rightDrive.update(getRightEnc(), leg2);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 3;
					drive.tankDrive(0, 0);
				}

			} else if (step == 3 && autoTime.get() > .25)// Out
			{
				if(gear)gearFlipper.setTarget(1.9);

				drive.arcadeDrive(0, 0);

				if (gearFlipper.pid.check && gear)
				{
					gearFlipper.setTarget(1.9);
					autoTime.reset();
					autoTime.start();
					step = 4;
					drive.tankDrive(0, 0);
				}
			} else if (step == 4 && autoTime.get() > .5)// Back
			{
				System.out.println("Time:" + autoTime.get());
				rightDrive.update(getRightEnc(), -10000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

			}
		}

		/*if (autoSel == 0)
		{
			if (autoTime.get() < 3)
			{
				drive.arcadeDrive(.5, 0);
			} else
				drive.arcadeDrive(0, 0);
		}

		if (autoSel == 1)
		{
			System.out.println(getRightEnc() + "*****" + gyro.gyro.getAngle() + "\n" + rightDrive.P
					+ "**" + rightDrive.I + "**" + rightDrive.D);
			if (step == 0)// FWD
			{

				gyro.update(0);
				rightDrive.update(getRightEnc(), 34000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
					System.out.println("\n \n \n");
				}

			} else if (step == 1 && autoTime.get() > .5)// Out
			{
				if(gear) gearFlipper.setTarget(1.9);

				drive.arcadeDrive(0, 0);

				if (autoTime.get() > 3)
				{
					gearFlipper.setTarget(2.6);
				}

				if (gearFlipper.pid.check && gear)
				{
					gearFlipper.setTarget(1.9);

					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
					System.out.println("\n \n \n");
				}
			} else if (step == 2 && autoTime.get() > 1)// Back
			{
				rightDrive.update(getRightEnc(), -10000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());
				System.out.println("\n \n \n");
			}
		}

		if (autoSel == 2)
		{
			System.out.println(getRightEnc() + "*****" + gyro.gyro.getAngle() + "\n" + rightDrive.P
					+ "**" + rightDrive.I + "**" + rightDrive.D);
			if (step == 0)// FWD
			{

				gyro.update(0);
				rightDrive.update(getRightEnc(), 35500);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
				}

			} else if (step == 1 && autoTime.get() > .5)// right
			{
				gyro.update(-14);
				drive.arcadeDrive(0, -gyro.getPow());

				if (gyro.pid.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
				}
			} else if (step == 2 && autoTime.get() > .5)// FWD
			{

				gyro.update(-14);
				rightDrive.update(getRightEnc(), 32000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 3;
					drive.tankDrive(0, 0);
				}

			} else if (step == 3 && autoTime.get() > .5)// Out
			{
				if(gear)gearFlipper.setTarget(1.9);

				drive.arcadeDrive(0, 0);

				if (gearFlipper.pid.check && gear)
				{
					gearFlipper.setTarget(1.9);

					autoTime.reset();
					autoTime.start();
					step = 4;
					drive.tankDrive(0, 0);
				}
			} else if (step == 4 && autoTime.get() > 1)// Back
			{
				System.out.println("Time:" + autoTime.get());
				rightDrive.update(getRightEnc(), -10000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

			}
		}
		if (autoSel == 3)
		{
			System.out.println(getRightEnc() + "*****" + gyro.gyro.getAngle() + "\n" + rightDrive.P
					+ "**" + rightDrive.I + "**" + rightDrive.D);
			if (step == 0)// FWD
			{

				gyro.update(0);
				rightDrive.update(getRightEnc(), 30000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 1;
					drive.tankDrive(0, 0);
				}

			} else if (step == 1 && autoTime.get() > .5)// left
			{
				gyro.update(14);
				drive.arcadeDrive(0, -gyro.getPow());

				if (gyro.pid.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 2;
					drive.tankDrive(0, 0);
				}
			} else if (step == 2 && autoTime.get() > .5)// FWD
			{

				gyro.update(14);
				rightDrive.update(getRightEnc(), 31000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

				if (rightDrive.check)
				{
					resetEnc();
					autoTime.reset();
					autoTime.start();
					step = 3;
					drive.tankDrive(0, 0);
				}

			} else if (step == 3 && autoTime.get() > .5)// Out
			{
				if(gear)gearFlipper.setTarget(1.9);

				drive.arcadeDrive(0, 0);

				if (gearFlipper.pid.check && gear)
				{
					gearFlipper.setTarget(1.9);

					autoTime.reset();
					autoTime.start();
					step = 4;
					drive.tankDrive(0, 0);
				}
			} else if (step == 4 && autoTime.get() > 1)// Back
			{
				System.out.println("Time:" + autoTime.get());
				rightDrive.update(getRightEnc(), -10000);
				drive.arcadeDrive(rightDrive.getPow() / 2, -gyro.getPow());

			}
		}*/

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
		gyro.gyro.reset();
		controllers.reset();
	}

	@Override
	public void teleopPeriodic()
	{
		// System.out.println(gyro.getAngle());
		disFlip = SmartDashboard.getString("DB/String 0", "myDefaultData");
		if (disFlip.equalsIgnoreCase("N"))
		{
			gearStop = true;
		} else
			gearStop = false;
		System.out.println(gearStop);
		System.out.println(gearFlipper.target);
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
			controllers.UpdateID(0);

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

			gearFlipper.setTarget(gearFlipper.target + (controllers.gearAdjust / 100));
			if (gearFlipper.target > 4.4)
				gearFlipper.setTarget(4.4);
			if (gearFlipper.target < 1.7)
				gearFlipper.setTarget(1.7);

			if (controllers.climbValueFast)
				climb.update(1);
			else if (controllers.climbValueSlow)
				climb.update(.5);
			else
				climb.update(0);

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
