// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.ClimberConstants;
import frc.robot.Constants.DriveConstants;
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
  Encoder climberEncoder = new Encoder(9, 8);
  DigitalInput limitswitch = new DigitalInput(1);

  public Robot() {
    rightFront.setInverted(DriveConstants.rightFrontInverted);
    rightBack.setInverted(DriveConstants.rightBackInverted);
    leftFront.setInverted(DriveConstants.leftFrontInverted);
    leftBack.setInverted(DriveConstants.leftBackInverted);

    intakeMotor.setInverted(intakeConstants.intakeMotorInverted);

    climberMotor.setInverted(ClimberConstants.climberMotorInverted);

    SmartDashboard.putData(climberPID);
    SmartDashboard.putNumber("tankRightSpeed", rightFront.get());
    SmartDashboard.putNumber("tankLeftSpeed", leftFront.get());
    SmartDashboard.putNumber("intakeMotorSpeed", intakeMotor.get());
    SmartDashboard.putNumber("climberEncoder", climberEncoder.get());
    SmartDashboard.putNumber("climberMotorSpeed", climberMotor.get());
    SmartDashboard.putBoolean("LimitSwitchState", limitswitch.get());
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("tankRightSpeed", rightFront.get());
    SmartDashboard.putNumber("tankLeftSpeed", leftFront.get());
    SmartDashboard.putNumber("intakeMotorSpeed", intakeMotor.get());
    SmartDashboard.putNumber("climberEncoder", climberEncoder.get());
    SmartDashboard.putNumber("climberMotorSpeed", climberMotor.get());
    SmartDashboard.putBoolean("LimitSwitchState", limitswitch.get());
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
    tankControl();

    intakeControl();

    climberControl();

    resetClimberEncoder();
  }

  private void tankControl() {
    if (!xboxController.getAButton()) {
      rightFront.set(xboxController.getRightY());
      leftFront.set(xboxController.getLeftY());
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
    }

  }

  private void intakeControl() {
    if (xboxController.getLeftTriggerAxis() > 0.1) {
      intakeMotor.set(xboxController.getLeftTriggerAxis() * intakeConstants.intakeMaxSpeed);
    } else {
      intakeMotor.set(0.0);
    }
    if (xboxController.getLeftBumperButton()) {
      intakeMotor.set(-intakeConstants.intakeMaxSpeed);
    }
  }

  private void climberControl() {
    if (!limitswitch.get()) {
      if (xboxController.getRightBumperButton()) {
        climberMotor.set(-xboxController.getRightTriggerAxis());
      } else if (xboxController.getRightTriggerAxis() > 0.1) {
        climberMotor.set(xboxController.getLeftTriggerAxis());
      } else if (xboxController.getAButton()) {
        climberMotor.set(climberPID.calculate(climberEncoder.get(), 0));
      } else if (xboxController.getBButton()) {
        climberMotor.set(climberPID.calculate(climberEncoder.get(), 1836));
      } else {
        climberMotor.set(0);
      }
    } else {
      climberMotor.set(0.1);
    }
  }

  private void resetClimberEncoder() {
    if (xboxController.getBackButton()) {
      climberEncoder.reset();
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
