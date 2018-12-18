/**
 * Phoenix Software License Agreement
 *
 * Copyright (C) Cross The Road Electronics.  All rights
 * reserved.
 * 
 * Cross The Road Electronics (CTRE) licenses to you the right to 
 * use, publish, and distribute copies of CRF (Cross The Road) firmware files (*.crf) and 
 * Phoenix Software API Libraries ONLY when in use with CTR Electronics hardware products
 * as well as the FRC roboRIO when in use in FRC Competition.
 * 
 * THE SOFTWARE AND DOCUMENTATION ARE PROVIDED "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * CROSS THE ROAD ELECTRONICS BE LIABLE FOR ANY INCIDENTAL, SPECIAL, 
 * INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF
 * PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR SERVICES, ANY CLAIMS
 * BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE
 * THEREOF), ANY CLAIMS FOR INDEMNITY OR CONTRIBUTION, OR OTHER
 * SIMILAR COSTS, WHETHER ASSERTED ON THE BASIS OF CONTRACT, TORT
 * (INCLUDING NEGLIGENCE), BREACH OF WARRANTY, OR OTHERWISE
 */

/**
 * Description:
 * The CurrentClosedLoop example demonstrates the Talon's ability to perform a closed loop
 * on current. The Talon adjusts motor output to achieve desired current.
 *
 * This example is configured to have current closed loop target between [-40, 40] Amps
 * with the left joystick Y Axis.
 * 
 * Controls:
 * Button 1: When held, enable Current Closed Loop. To be used with Left Joystick Y-Axis.
 * Left Joystick Y-Axis: Throttle Talon in forward and reverse direction.
 *  + Current Closed Loop [-40, 40]Amps when Button 1 held, Percent Output all other times.
 * 
 * Gains for Current Closed Loop may need to be adjusted in Constants.Java
 */
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class Robot extends TimedRobot {
    /* Hardware */
	TalonSRX _talon = new TalonSRX(3);
    Joystick _joy = new Joystick(0);
    /* Used to build string throughout loop */
    StringBuilder _sb = new StringBuilder();
    /* Used to track number of loops */
	int _loops = 0;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
        /* Factory default hardware to prevent unexpected behaviour */
        _talon.configFactoryDefault();

		/* Config the peak and nominal outputs ([-1, 1] represents [-100, 100]%) */
		_talon.configNominalOutputForward(0, Constants.kTimeoutMs);
		_talon.configNominalOutputReverse(0, Constants.kTimeoutMs);
		_talon.configPeakOutputForward(1, Constants.kTimeoutMs);
        _talon.configPeakOutputReverse(-1, Constants.kTimeoutMs);

		/* Config the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table here for units to use: 
         * https://github.com/CrossTheRoadElec/Phoenix-Documentation#what-are-the-units-of-my-sensor
		 */
		_talon.configAllowableClosedloopError(0, Constants.kPIDLoopIdx,
				Constants.kTimeoutMs);
		/* set closed loop gains in slot0 */
		_talon.config_kP(Constants.kPIDLoopIdx, Constants.kGains.kP, Constants.kTimeoutMs);
		_talon.config_kI(Constants.kPIDLoopIdx, Constants.kGains.kI, Constants.kTimeoutMs);
        _talon.config_kD(Constants.kPIDLoopIdx, Constants.kGains.kD, Constants.kTimeoutMs);
        _talon.config_kF(Constants.kPIDLoopIdx, Constants.kGains.kF, Constants.kTimeoutMs);
	}

	@Override
	public void teleopPeriodic() {
        /* Get Joystick values */
		double leftYstick = _joy.getY();
		double motorOutput = _talon.getMotorOutputPercent();
		boolean button1 = _joy.getRawButton(1);

		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
		_sb.append("\tcur:");
		_sb.append(_talon.getOutputCurrent());

		if (button1) {
			_talon.set(ControlMode.Current, leftYstick * 40); // Scale to 40A
		} else {
			_talon.set(ControlMode.PercentOutput, leftYstick);
		}
		/* if Talon is in position closed-loop, print some more info */
		if (_talon.getControlMode() == ControlMode.Current) {
			/* append more signals to print when in speed mode. */
			_sb.append("\terrNative:");
			_sb.append(_talon.getClosedLoopError(0));
			_sb.append("\ttrg:");
			_sb.append(leftYstick * 40);
		}

        /* Print every ten loops, 
         * printing too much too fast is generally bad for performance
		 */
		if (++_loops >= 10) {
			_loops = 0;
			System.out.println(_sb.toString());
        }
        /* Reset built string for next print */
		_sb.setLength(0);
	}
}
