package com.nxtproject.image.path;

public class Coordinate {
	private int x;
	private int y;
	
	public final int[] coordIndicies;
	
	public Coordinate() {
		this(0, 0);
	}

	public Coordinate(int x, int y) {
		this(x, y, null);
	}
	
	public Coordinate(int x, int y, int[] coordIndicies) {
		this.coordIndicies = coordIndicies;
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}