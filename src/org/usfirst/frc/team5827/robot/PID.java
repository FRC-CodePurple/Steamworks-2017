package org.usfirst.frc.team5827.robot;

class PID
{
	public double kP, kI, kD, P, I, D, errorN, errorL, threshold;
	public double multiplier;
	public boolean check;
	public double errThresh;
	public double checkPowThresh;

	public PID(double kP, double kI, double kD)
	{
		this.kP = kP; // set modifier values
		this.kI = kI;
		this.kD = kD;
		threshold = 1;
		multiplier = 1;
		errThresh = 1;
		checkPowThresh = 0.1;
		check = false;
	}

	public PID(double kP, double kI, double kD, double threshold, double ErrorThreshold,
			double PowerThreshold, double multiplier)
	{
		this.kP = kP; // set modifier values
		this.kI = kI;
		this.kD = kD;
		this.threshold = threshold;
		this.multiplier = multiplier;

		errThresh = ErrorThreshold;
		checkPowThresh = PowerThreshold;
		check = false;
	}

	public PID(double kP, double kI, double kD, double threshold, double errorThreshold,
			double multiplier)
	{
		this.kP = kP; // set modifier values
		this.kI = kI;
		this.kD = kD;
		this.threshold = threshold;
		this.multiplier = multiplier;

		errThresh = errorThreshold;
		checkPowThresh = .1;
		check = false;
	}

	public void update(double current, double target)// should be called once
														// every loop
	{
		errorL = errorN;
		errorN = target - current; // update error values

		P = kP * errorN; // set P value

		I = I + (kI * errorN); // set I value
		if (Math.abs(errorN) < errThresh)
			I = 0; // reset I value within tolerance

		D = kD * (errorN - errorL); // set D value

		if ((Math.abs(errorN) < errThresh) && (Math.abs(getPow()) < checkPowThresh))
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
}