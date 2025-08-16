package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.ClimberConstants;

public class Climber {
  XboxController xboxController;

  WPI_VictorSPX climberMotor = new WPI_VictorSPX(ClimberConstants.climberMotorID);

  PIDController climberPID = new PIDController(
      ClimberConstants.climberPIDKp,
      ClimberConstants.climberPIDKi,
      ClimberConstants.climberPIDKd);

  DutyCycleEncoder climberEncoder = new DutyCycleEncoder(
      ClimberConstants.climberEncoderChannel,
      ClimberConstants.climberEncoderFullRange,
      ClimberConstants.climberEncoderExpectedZero); // TODO: Change value

  public Climber(XboxController xboxController) {
    this.xboxController = xboxController;

    climberEncoder.setInverted(false);

    SmartDashboard.putData(climberPID);
  }

  public void putSmartDashboard() {
    SmartDashboard.putNumber("Climber Encoder", climberEncoder.get());
    SmartDashboard.putNumber("Climber Motor Speed", climberMotor.get());
  }

  public void setClimberSpeed(double speed) {
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

}
