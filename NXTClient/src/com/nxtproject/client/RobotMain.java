package com.nxtproject.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.nxtproject.commons.MessageBytes;
import com.nxtproject.commons.NXTInitializationHeader;
import com.nxtproject.commons.NXTInitializationHeader.MotorChoise;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class RobotMain implements Runnable {
	private DifferentialPilot pilot;
	
	private BTConnection btc;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private boolean isRunning;
	private Thread listenThread;
	
	public static void main(String[] args) {
		RobotMain main = new RobotMain();
		main.start();
	}
	
	public RobotMain() {
		this.listenThread = new Thread(this);
		this.isRunning = false;
	}
	
	public void connect() {
		this.btc = Bluetooth.waitForConnection();
		
		this.inputStream = new DataInputStream(new BufferedInputStream(btc.openInputStream()));
		this.outputStream = new DataOutputStream(new BufferedOutputStream(btc.openOutputStream()));
	}
	
	public void start() {
		this.isRunning = true;
		listenThread.start();
	}
	
	@Override
	public void run() {
		connect();
		try {
			byte[] headerData = read();
			NXTInitializationHeader header = new NXTInitializationHeader();
			header.constructHeader(headerData);
			createPilot(header);
			
			while(isRunning) {
				byte[] data = read();
				
				if(data[0] == MessageBytes.MOVEMENT_ANGLE) {
					DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data, 1, data.length - 1));
					double angle = stream.readDouble();
					stream.close();
					
					pilot.rotate(angle, true);
				} else if(data[0] == MessageBytes.MOVEMENT_TRAVEL) {
					if(data[1] == MessageBytes.TRAVEL_DISTANCE) {
						DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data, 2, data.length - 2));
						double distance = stream.readDouble();
						stream.close();
						
						pilot.travel(distance, true);
						LCD.clear();
						LCD.drawString("Moving: " + distance, 0, 0);
						LCD.refresh();
					} else if(data[1] == MessageBytes.TRAVEL_START) {
						pilot.forward();
					} else if(data[1] == MessageBytes.TRAVEL_STOP) {
						pilot.stop();
					}
					
				} else if(data[0] == MessageBytes.TERMINATE_CONNETION) {
					isRunning = false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public void createPilot(NXTInitializationHeader header) {
		this.pilot = new DifferentialPilot(header.getWheelDiameter(), header.getTrackWidth(), getMotor(header.getLeftMotor()), getMotor(header.getRightMotor()));
		pilot.setRotateSpeed(header.getRotateSpeed());
		pilot.setTravelSpeed(header.getTravelSpeed());
	}
	
	public RegulatedMotor getMotor(MotorChoise choise) {
		if(choise == MotorChoise.MOTOR_A) return Motor.A;
		else if(choise == MotorChoise.MOTOR_B) return Motor.B;
		else if(choise == MotorChoise.MOTOR_C) return Motor.C;
		else return null;
	}
	
	public byte[] read() throws IOException {
		int length = inputStream.readInt();
		byte[] data = new byte[length];
		inputStream.readFully(data);
		return data;
	}
	
	public void send(byte[] data) throws IOException {
		outputStream.writeInt(data.length);
		outputStream.write(data, 0, data.length);
		outputStream.flush();
	}
	
	public void close() {
		try {
			inputStream.close();
			outputStream.close();
			btc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double calibrateAngle(double angle) {
		return angle * 0.81;
	}
}