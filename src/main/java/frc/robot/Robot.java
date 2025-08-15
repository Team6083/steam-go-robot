// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.ClimberConstants;
import frc.robot.Constants.intakeConstants;

/**
 * The methods in this class are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {

  XboxController xboxController = new XboxController(0);

  WPI_VictorSPX rightFront = new WPI_VictorSPX(DriveConstants.rightFrontID);
  WPI_VictorSPX rightBack = new WPI_VictorSPX(DriveConstants.rightBackID);
  WPI_VictorSPX leftFront = new WPI_VictorSPX(DriveConstants.leftFrontID);
  WPI_VictorSPX leftBack = new WPI_VictorSPX(DriveConstants.leftBackID);

  WPI_VictorSPX intakeMotor = new WPI_VictorSPX(intakeConstants.intakeMotorID);

  WPI_VictorSPX climberMotor = new WPI_VictorSPX(ClimberConstants.climberMotorID);

  PIDController climberPID = new PIDController(
      ClimberConstants.climberPIDKp,
      ClimberConstants.climberPIDKi,
      ClimberConstants.climberPIDKd);

  DutyCycleEncoder climberEncoder = new DutyCycleEncoder(
      ClimberConstants.climberEncoderChannel,
      ClimberConstants.climberEncoderFullRange,
      ClimberConstants.climberEncoderExpectedZero); // TODO: Change value

  public Robot() {
    rightFront.setInverted(DriveConstants.rightFrontInverted);
    rightBack.setInverted(DriveConstants.rightBackInverted);
    leftFront.setInverted(DriveConstants.leftFrontInverted);
    leftBack.setInverted(DriveConstants.leftBackInverted);

    intakeMotor.setInverted(false);
    climberEncoder.setInverted(false);

    SmartDashboard.putData(climberPID);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("intakeMotorSpeed", intakeMotor.get());

    SmartDashboard.putNumber("climberEncoder", climberEncoder.get());
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    rightFront.set(xboxController.getRightY());
    leftFront.set(xboxController.getLeftY());
    rightBack.follow(rightFront);
    leftBack.follow(leftFront);

    // if (xboxController.getRightTriggerAxis() > 0.1) {
    //   intakeMotor.set(xboxController.getRightTriggerAxis() * intakeConstants.intakeMaxSpeed);
    // } else if (xboxController.getLeftTriggerAxis() > 0.1) {
    //   intakeMotor.set(-xboxController.getLeftTriggerAxis() * intakeConstants.intakeMaxSpeed);
    // } else {
    //   intakeMotor.set(0.0);
    // }

    if (xboxController.getRightTriggerAxis() > 0.1) {
      climberMotor.set(xboxController.getRightTriggerAxis() * 1);
    } else if (xboxController.getLeftTriggerAxis() > 0.1) {
      climberMotor.set(-xboxController.getLeftTriggerAxis() * 1);
    } else {
      climberMotor.set(0.0);
    }

    if (xboxController.getAButton()) { // TODO: Change setpoint to desired angle
      climberMotor.set(climberPID.calculate(climberEncoder.get(), 90));
    }

  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
