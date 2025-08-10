// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The methods in this class are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {

  XboxController xboxController = new XboxController(0);

  WPI_VictorSPX rightMotor1 = new WPI_VictorSPX(31);
  WPI_VictorSPX rightMotor2 = new WPI_VictorSPX(32);
  WPI_VictorSPX leftMotor1 = new WPI_VictorSPX(34);
  WPI_VictorSPX leftMotor2 = new WPI_VictorSPX(33);

  WPI_VictorSPX intakeMotor = new WPI_VictorSPX(10); // TODO: Change to intake motor ID

  // WPI_VictorSPX climberMotor = new WPI_VictorSPX(11); 

  public Robot() {
    leftMotor1.setInverted(true);
    leftMotor2.setInverted(true);
  }

  @Override
  public void robotPeriodic() {
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
    rightMotor1.set(xboxController.getRightY());
    leftMotor1.set(xboxController.getLeftY());
    rightMotor2.follow(rightMotor1);
    leftMotor2.follow(leftMotor1);

    intakeMotor.set(xboxController.getRightTriggerAxis());
    intakeMotor.set(-xboxController.getLeftTriggerAxis());
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
