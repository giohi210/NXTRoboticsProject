package com.nxtproject.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.nxtproject.commons.MessageBytes;
import com.nxtproject.commons.NXTInitializationHeader;
import com.nxtproject.commons.NXTInitializationHeader.MotorChoise;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class ServerMain implements Runnable {
	private NXTConnector conn;
	
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	
	private boolean isRunning;
	private Thread listenThread;
	
	private NXTInitializationHeader header;
	
	public ServerMain() {
		this.listenThread = new Thread(this);
		this.isRunning = false;
	}
	
	public void createConnection() {
		System.out.println("Creating NXT Connection... ");
		this.conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){
			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: "+message);
				
			}

			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();
			}
		});
		
		System.out.println("Connecting to bluetooth... ");
		boolean connected = conn.connectTo("btspp://");
		
		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		System.out.println("Openning Streams... ");
		this.outputStream = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
		this.inputStream = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
	}
	
	public void startListenThread() {
		createConnection();
		
		this.isRunning = false;
		listenThread.start();
	}
	
	@Override
	public void run() {
		try {
			send(header.getData());
			
			Thread.sleep(1000);
			
			sendTravelOperation(true);
			
			Thread.sleep(1000);
			
			sendAngle(10);
			
			Thread.sleep(3000);
			
			sendTravelOperation(false);
			send(MessageBytes.TERMINATE_CONNETION);
			
			while(isRunning) {
				byte[] data = read();
				
				System.out.println(new String(data));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
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
	
	public void send(byte data) throws IOException {
		outputStream.writeInt(1);
		outputStream.write(data);
		outputStream.flush();
	}
	
	public void sendAngle(double angle) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(output);
		stream.write(MessageBytes.MOVEMENT_ANGLE);
		stream.writeDouble(angle);
		
		byte[] data = output.toByteArray();
		output.close();
		stream.close();
		
		send(data);
	}
	
	public void sendTravelDistance(double distance) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(output);
		stream.write(MessageBytes.MOVEMENT_TRAVEL);
		stream.write(MessageBytes.TRAVEL_DISTANCE);
		stream.writeDouble(distance);
		
		byte[] data = output.toByteArray();
		output.close();
		stream.close();
		
		send(data);
	}
	
	public void sendTravelOperation(boolean start) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(output);
		stream.write(MessageBytes.MOVEMENT_TRAVEL);
		stream.write(start ? MessageBytes.TRAVEL_START : MessageBytes.TRAVEL_STOP);
		
		byte[] data = output.toByteArray();
		output.close();
		stream.close();
		
		send(data);
	}
	
	public void close() {
		try {
			inputStream.close();
			outputStream.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setHeader(NXTInitializationHeader header) {
		this.header = header;
	}
	
	public static void main(String[] args) {
		ServerMain main = new ServerMain();
		
		NXTInitializationHeader header = new NXTInitializationHeader();
		header.setWheelDiameter(56);
		header.setTrackWidth(185);
		header.setRotateSpeed(250);
		header.setTravelSpeed(250);
		header.setLeftMotor(MotorChoise.MOTOR_A);
		header.setRightMotor(MotorChoise.MOTOR_B);
		
		main.setHeader(header);
		
		main.startListenThread();
	}
}