// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  private VictorSPX rightFront;
  private VictorSPX rightBack;
  private VictorSPX leftFront;
  private VictorSPX leftBack;
  private VictorSPX coralIntakeMotor;
  private XboxController xboxController;

  public Robot() {
    // Constructor for the Robot class
  }

  @Override
  public void robotInit() {
    xboxController = new XboxController(0);
    rightFront = new VictorSPX(1);
    rightBack = new VictorSPX(2);
    leftFront = new VictorSPX(3);
    leftBack = new VictorSPX(4);
    coralIntakeMotor = new VictorSPX(5);
    rightBack.follow(rightFront);
    leftBack.follow(leftFront);
    rightBack.setInverted(true);
    leftBack.setInverted(false);
    rightFront.setInverted(true);
    leftFront.setInverted(false);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Right wheel speed", xboxController.getRightY());
    SmartDashboard.putNumber("Left wheel speed", xboxController.getLeftY());
    SmartDashboard.putNumber("Right Front Speed", rightFront.getMotorOutputPercent());
    SmartDashboard.putNumber("Right Back Speed", rightBack.getMotorOutputPercent());
    SmartDashboard.putNumber("Left Front Speed", leftFront.getMotorOutputPercent());
    SmartDashboard.putNumber("Left Back Speed", leftBack.getMotorOutputPercent());
    SmartDashboard.putBoolean("Right Bumper", xboxController.getRightBumperButton());
    SmartDashboard.putNumber("CoralIntake Speed", coralIntakeMotor.getMotorOutputPercent());
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    
  }

  @Override
  public void teleopPeriodic() {
    rightFront.set(ControlMode.PercentOutput,speedLimit(xboxController.getRightY()));
    leftFront.set(ControlMode.PercentOutput,speedLimit(xboxController.getLeftY()));
    if(xboxController.getRightBumperButton()){
      coralIntakeMotor.set(ControlMode.PercentOutput, 0.5);
    }else{
      coralIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  public static double speedLimit(double speed){
    if (speed > 1.0) {
      speed = 1.0;
    } else if (speed < -1.0) {
      speed = -1.0;
    }
    return speed;
  }  
}


