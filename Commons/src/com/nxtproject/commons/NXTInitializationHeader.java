package com.nxtproject.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NXTInitializationHeader {
	private double wheelDiameter;
	private double trackWidth;
	
	private MotorChoise leftMotor;
	private MotorChoise rightMotor;
	
	private double rotateSpeed;
	private double travelSpeed;
	
	private double rotateCalibrate;
	private double travelCalibrate;
	
	public NXTInitializationHeader() {
		
	}
	
	public byte[] getData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(baos);
		
		stream.writeDouble(wheelDiameter);
		stream.writeDouble(trackWidth);
		
		stream.writeChar(leftMotor.getId());
		stream.writeChar(rightMotor.getId());
		
		stream.writeDouble(rotateSpeed);
		stream.writeDouble(travelSpeed);
		
		stream.writeDouble(rotateCalibrate);
		stream.writeDouble(travelCalibrate);
		
		byte[] data = baos.toByteArray();
		
		baos.close();
		stream.close();
		
		return data;
	}
	
	public void constructHeader(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream stream = new DataInputStream(bais);
		
		wheelDiameter = stream.readDouble();
		trackWidth = stream.readDouble();
		leftMotor = MotorChoise.getChoise(stream.readChar());
		rightMotor = MotorChoise.getChoise(stream.readChar());
		rotateSpeed = stream.readDouble();
		travelSpeed = stream.readDouble();
		rotateCalibrate = stream.readDouble();
		travelCalibrate = stream.readDouble();
		
		bais.close();
		stream.close();
	}
	
	public static enum MotorChoise {
		MOTOR_A('A'),
		MOTOR_B('B'),
		MOTOR_C('C');
		
		private final char id;
		
		private MotorChoise(char id) {
			this.id = id;
		}
		
		public static MotorChoise getChoise(char c) {
			if(c == MOTOR_A.getId()) return MOTOR_A;
			else if(c == MOTOR_B.getId()) return MOTOR_B;
			else if(c == MOTOR_C.getId()) return MOTOR_C;
			else return null;
		}
		
		public char getId() {
			return id;
		}
	}

	public double getWheelDiameter() {
		return wheelDiameter;
	}

	public void setWheelDiameter(double wheelDiameter) {
		this.wheelDiameter = wheelDiameter;
	}

	public double getTrackWidth() {
		return trackWidth;
	}

	public void setTrackWidth(double trackWidth) {
		this.trackWidth = trackWidth;
	}

	public MotorChoise getLeftMotor() {
		return leftMotor;
	}

	public void setLeftMotor(MotorChoise leftMotor) {
		this.leftMotor = leftMotor;
	}

	public MotorChoise getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(MotorChoise rightMotor) {
		this.rightMotor = rightMotor;
	}

	public double getRotateSpeed() {
		return rotateSpeed;
	}

	public void setRotateSpeed(double rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
	}

	public double getTravelSpeed() {
		return travelSpeed;
	}

	public void setTravelSpeed(double travelSpeed) {
		this.travelSpeed = travelSpeed;
	}

	public double getRotateCalibrate() {
		return rotateCalibrate;
	}

	public void setRotateCalibrate(double rotateCalibrate) {
		this.rotateCalibrate = rotateCalibrate;
	}

	public double getTravelCalibrate() {
		return travelCalibrate;
	}

	public void setTravelCalibrate(double travelCalibrate) {
		this.travelCalibrate = travelCalibrate;
	}
}