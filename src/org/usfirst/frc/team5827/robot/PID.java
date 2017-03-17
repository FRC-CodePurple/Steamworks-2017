package org.usfirst.frc.team5827.robot;

class PID
{
	public double kP, kI, kD, P, I, D, errorN, errorL, threshold;
	public double iThresh;
	public double multiplier;
	public boolean check;
	public double checkErrThresh;
	public double checkPowThresh;
	// P - P value
	// kP - P modifyer
	// I - I value
	// kI - I modifyer
	// D - D value
	// kD - D modifyer
	// errorN - error in this loop
	// errorL - error in last loop

	public PID(double kP, double kI, double kD)
	{
		this.kP = kP; // set modifier values
		this.kI = kI;
		this.kD = kD;
		threshold = 1;
		multiplier = 1;
		iThresh = 1;
		checkErrThresh = 100;
		checkPowThresh = 0.1;
		check = false;
	}

	public void update(double current, double target)// should be called once
														//	 every loop
	{
		errorL = errorN;
		errorN = target - current; // update error values

		P = kP * errorN; // set P value

		I = I + (kI * errorN); // set I value
		if (Math.abs(errorN) < 1)
			I = 0; // reset I value within tolerance

		D = kD * (errorN - errorL); // set D value

		if ((Math.abs(errorN) < checkErrThresh) && (Math.abs(getPow()) < checkPowThresh))
			check = true;
		else
			check = false;

	}

	public double getPow()// get motor power
	{
		if ((P + I + D) * multiplier > threshold)
			return threshold;
		if ((P + I + D) * multiplier < -threshold)
			return -threshold;
		return (P + I + D) * multiplier;// add values
	}

	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}

}