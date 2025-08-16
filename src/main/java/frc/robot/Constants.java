package frc.robot;

public class Constants {
    public class DriveConstants {
        public static final int rightFrontID = 31;
        public static final int rightBackID = 32;
        public static final int leftFrontID = 34;
        public static final int leftBackID = 33;

        public static final Boolean rightFrontInverted = false;
        public static final Boolean rightBackInverted = false;
        public static final Boolean leftFrontInverted = true;
        public static final Boolean leftBackInverted = true;
    }

    public class intakeConstants {
        public static final int intakeMotorID = 21;
        public static final Boolean intakeMotorInverted = false;
        public static final double intakeMaxSpeed = 0.4;
    }

    public class ClimberConstants {
        public static final int climberMotorID = 0; // TODO: Change ID
        public static final Boolean climberMotorInverted = false;
        public static final int climberEncoderChannel = 0; // TODO: check

        public static final double climberEncoderFullRange = 360.0; // TODO: check
        public static final double climberEncoderExpectedZero = 0.0; // TODO: check

        public static final double climberPIDKp = 0.1; // TODO: check
        public static final double climberPIDKi = 0.0; // TODO: check
        public static final double climberPIDKd = 0.0; // TODO: check
    }
}
