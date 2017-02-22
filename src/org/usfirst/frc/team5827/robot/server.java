package org.usfirst.frc.team5827.robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class server
{
	NetworkTable sd = NetworkTable.getTable("SmartDashboard");
	
	public double getNumber(String label, int value)
	{
		return sd.getNumber(label, value);
	}
	
}
