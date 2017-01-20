
package org.usfirst.frc.team5827.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team5827.robot.commands.ExampleCommand;
import org.usfirst.frc.team5827.robot.subsystems.ExampleSubsystem;

import com.ctre.CANTalon;

public class Robot extends IterativeRobot {

	public static OI oi;
	CANTalon r1;
	CANTalon r2;
	CANTalon r3;
	CANTalon l1;
	CANTalon l2;
	CANTalon l3;
	Joystick joy;
	double speed;
	Command autonomousCommand;
	
	
	@Override
	public void robotInit() {
		oi = new OI();
		r1 = new CANTalon(21);
		r2 = new CANTalon(22);
		r3 = new CANTalon(23);
		l1 = new CANTalon(31);
		l2 = new CANTalon(32);
		l3 = new CANTalon(33);
		joy = new Joystick(0);
		
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
	
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		r1 = new CANTalon(21);
		r2 = new CANTalon(22);
		r3 = new CANTalon(23);
		l1 = new CANTalon(31);
		l2 = new CANTalon(32);
		l3 = new CANTalon(33);
		joy = new Joystick(0);
	}

	@Override
	public void teleopPeriodic() {
		speed=(joy.getRawAxis(3)+1)/2;
		drive(joy.getRawAxis(1)*speed,joy.getRawAxis(0));
	}

	@Override
	public void testPeriodic() {
	}
	
	public void drive(double power, double turn){
		r1.set(power+turn);
		r2.set(power+turn);
		r3.set(power+turn);
		l1.set(power-turn);
		l2.set(power-turn);
		l3.set(power-turn);
	}
}
