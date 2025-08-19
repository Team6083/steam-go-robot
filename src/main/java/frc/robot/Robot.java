// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
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

  enum ClimberState {
    HOLD_POSITION_INIT,
    HOLD_POSITION,
    PULL_BACK,
    CONTINUOUS_UP_DOWN,
    EXTEND,
  }

  ClimberState climberState = ClimberState.HOLD_POSITION_INIT;
  Boolean climberIsPID = true;

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
    SmartDashboard.putString("ClimberState", climberState.toString());
    SmartDashboard.putBoolean("ClimberIsPID", climberIsPID);

    climberEncoder.reset();
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("tankRightSpeed", rightFront.get());
    SmartDashboard.putNumber("tankLeftSpeed", leftFront.get());
    SmartDashboard.putNumber("intakeMotorSpeed", intakeMotor.get());
    SmartDashboard.putNumber("climberEncoder", climberEncoder.get());
    SmartDashboard.putNumber("climberMotorSpeed", climberMotor.get());
    SmartDashboard.putBoolean("LimitSwitchState", limitswitch.get());
    SmartDashboard.putString("ClimberState", climberState.toString());
    SmartDashboard.putBoolean("ClimberIsPID", climberIsPID);
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

    toggleClimberPID();

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

  private void switchClimberState() {
    switch (climberState) {
      case HOLD_POSITION_INIT:
        climberState = ClimberState.HOLD_POSITION;
        break;
      case HOLD_POSITION:
        if (xboxController.getAButtonPressed()) {
          climberState = ClimberState.PULL_BACK;
        } else if (xboxController.getRightBumperButton()
            || xboxController.getRightTriggerAxis() > 0.1) {
          climberState = ClimberState.CONTINUOUS_UP_DOWN;
        } else if (xboxController.getBButton()) {
          climberState = ClimberState.EXTEND;
        }
        break;
      case PULL_BACK:
        if (xboxController.getAButtonPressed()
            || limitswitch.get()) {
          climberState = ClimberState.HOLD_POSITION_INIT;
        }
        break;
      case CONTINUOUS_UP_DOWN:
        if (!xboxController.getRightBumperButton() &&
            !(xboxController.getRightTriggerAxis() > 0.1)) {
          climberState = ClimberState.HOLD_POSITION_INIT;
        }
        break;
      case EXTEND:
        if (climberIsPID) {
          if (xboxController.getBButtonPressed()
              || climberEncoder.get() >= ClimberConstants.climberExtendPosition) {
            climberState = ClimberState.HOLD_POSITION_INIT;
          }
        } else {
          climberState = ClimberState.HOLD_POSITION_INIT;
        }
        break;
    }
  }

  private void climberControl() {
    switchClimberState();
    switch (climberState) {
      case HOLD_POSITION_INIT:
        climberPID.setSetpoint(climberEncoder.get());
        break;
      case HOLD_POSITION:
        if (climberIsPID) {
          climberMotor.set(climberPID.calculate(climberEncoder.get()));
        } else {
          climberMotor.set(0.0);
        }
        break;
      case PULL_BACK:
        if (limitswitch.get() == false) {
          climberMotor.set(-0.6);
        } else {
          climberMotor.set(0.0);
        }
        break;
      case CONTINUOUS_UP_DOWN:
        if (xboxController.getRightBumperButton()) {
          climberMotor.set(-0.6);
        } else if (xboxController.getRightTriggerAxis() > 0.1) {
          climberMotor.set(0.6);
        } else {
          climberMotor.set(0.0);
        }
        break;
      case EXTEND:
        if (climberIsPID) {
          if (climberEncoder.get() < ClimberConstants.climberExtendPosition) {
            climberMotor.set(0.5);
          } else {
            climberMotor.set(0.0);
          }
        }
        break;
    }
  }

  private void toggleClimberPID() {
    if (xboxController.getStartButtonPressed()) {
      climberIsPID = !climberIsPID;
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
