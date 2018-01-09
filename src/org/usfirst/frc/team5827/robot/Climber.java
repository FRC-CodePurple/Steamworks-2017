package org.usfirst.frc.team5827.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Victor;

public class Climber
{

	public CANTalon motor1, motor2, motor3;
public Victor motor4;
	public Climber(int motor1ID, int motor2ID, int motor3ID, int motor4ID)
	{
		motor1 = new CANTalon(motor1ID);
		motor2 = new CANTalon(motor2ID);
		motor3 = new CANTalon(motor3ID);
		motor4 = new Victor(motor4ID);
	}
	
	public void update(double speed)
	{
		speed = -Math.abs(speed);
		motor1.set(speed);
		motor2.set(speed);
		motor3.set(speed);
		motor4.set(speed);
	}

}
