// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.studica.frc.AHRS;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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

  private double magnification;

  enum ClimberState {
    HOLD_POSITION_INIT,
    HOLD_POSITION,
    PULL_BACK,
    CONTINUOUS_UP_DOWN,
    EXTEND,
  }

  ClimberState climberState = ClimberState.HOLD_POSITION_INIT;
  Boolean climberIsPID = true;

  Timer timer = new Timer();

  AHRS gyro = new AHRS(AHRS.NavXComType.kMXP_SPI);

  final String kDefaultAuto = "Default";
  final String kForward = "Forward";
  final String kMOneCoral = "M - one coral";
  final String kback = "M one coral and back";
  final String kROneCoral = "R - one coral";
  SendableChooser<String> autoChooser = new SendableChooser<>();
  String autoSelected;

  final boolean saveLog = true;

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
    SmartDashboard.putNumber("Timer", timer.get());
    SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());

    climberEncoder.reset();
    climberPID.setSetpoint(0);

    autoChooser.setDefaultOption("Default Auto", kDefaultAuto);
    autoChooser.addOption("Forward", kForward);
    autoChooser.addOption("M - one Coral", kMOneCoral);
    autoChooser.addOption("R - one coral", kROneCoral);
    autoChooser.addOption("M one coral and back", kback);
    SmartDashboard.putData("Auto chooser", autoChooser);
    SmartDashboard.putNumber("Timer", timer.get());

    if (saveLog) {
      DataLogManager.start();
      DriverStation.startDataLog(DataLogManager.getLog());
    }
  }

  @Override
  public void robotInit() {
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/BuildDate").publish()
        .set(BuildConstants.BUILD_DATE);
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/GitBranch").publish()
        .set(BuildConstants.GIT_BRANCH);
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/GitDate").publish()
        .set(BuildConstants.GIT_DATE);
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/GitDirty").publish()
        .set(BuildConstants.DIRTY == 1 ? "Dirty!" : "Clean! Good job!");
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/GitSHA").publish()
        .set(BuildConstants.GIT_SHA);
    NetworkTableInstance.getDefault().getStringTopic("/Metadata/GitBranch").publish()
        .set(BuildConstants.GIT_BRANCH);

    SmartDashboard.putString("GitInfo", String.format("%s (%s), %s",
        BuildConstants.GIT_SHA,
        BuildConstants.GIT_BRANCH,
        BuildConstants.DIRTY == 1 ? "Dirty" : "Clean"));
    SmartDashboard.putString("BuildDate", BuildConstants.BUILD_DATE);
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
    SmartDashboard.putNumber("Timer", timer.get());
    SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
  }

  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
    autoSelected = autoChooser.getSelected();
  }

  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putString("Auto selected", autoSelected);

    switch (autoSelected) {
      case kDefaultAuto:
        // Do nothing in default auto
        break;
      case kForward:
        forward();
        break;
      case kMOneCoral:
        MOneCoral();
        break;
      case kROneCoral:
        ROneCoral();
        break;
      case kback:
        MOneCoralAndBack();
      default:
        System.out.println("Unknown auto selected: " + autoSelected);
        break;
    }
  }

  private void forward() {
    if (timer.get() < 2) {
      rightFront.set(-0.5);
      leftFront.set(-0.5);
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
    } else {
      rightFront.set(0.0);
      leftFront.set(0.0);
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
    }
  }

  private void MOneCoral() {
    if (timer.get() < 2.0) {
      rightFront.set(-0.5);
      leftFront.set(-0.5);
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
    } else {
      rightFront.set(0.0);
      leftFront.set(0.0);
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
      intakeMotor.set(0.4);
    }
  }

  private void MOneCoralAndBack() {
    if (timer.get() < 2.0) {
      rightFront.set(-0.5);
      leftFront.set(-0.5);
    } else if (timer.get() < 3.0) {
      rightFront.set(0.0);
      leftFront.set(0.0);

      intakeMotor.set(0.4);
    } else if (timer.get() < 4.5) {
      rightFront.set(0.2);
      leftFront.set(0.2);

      intakeMotor.set(0.0);
    } else if (gyro.getAngle() > -120) {
      rightFront.set(-0.2);
      leftFront.set(0.2);
    } else {
      rightFront.set(0.0);
      leftFront.set(0.0);
    }

    rightBack.follow(rightFront);
    leftBack.follow(leftFront);
  }

  private void ROneCoral() {
    if (timer.get() < 2.0) {
      rightFront.set(0.1);
      leftFront.set(0.2);
      rightBack.follow(rightFront);
      leftBack.follow(leftFront);
    } else {
      rightFront.set(0);
      leftFront.set(0);
      rightBack.follow(rightFront);
      rightBack.follow(leftFront);
    }
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
    if (xboxController.getYButton()) {
      magnification = 0.75;
    } else if (climberEncoder.get() >= 1700) {
      magnification = 0.3;
    } else {
      magnification = 0.5;
    }

    rightFront.set(xboxController.getRightY() * magnification);
    leftFront.set(xboxController.getLeftY() * magnification);
    rightBack.follow(rightFront);
    leftBack.follow(leftFront);

  }

  private void intakeControl() {
    if (xboxController.getLeftTriggerAxis() > 0.1) {
      intakeMotor.set(xboxController.getLeftTriggerAxis() * intakeConstants.intakeMaxSpeed);
    } else if (xboxController.getLeftBumperButton()) {
      intakeMotor.set(-intakeConstants.intakeMaxSpeed);
    } else {
      intakeMotor.set(0.0);
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
