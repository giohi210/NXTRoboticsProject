package com.nxtproject.image;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import com.nxtproject.image.path.PathTrack;
import com.nxtproject.image.path.Vector2f;
import com.nxtproject.image.path.PathTrack.PathTrackResult;

public class Screen extends Canvas {
	private static final long serialVersionUID = 1253336345960274707L;
	
	private Thread thread;
	private boolean isRunning;
	
	private ArrayList<Vector2f> verticies;
	
	private Vector2f position;
	private Vector2f direction;
	
	private int vertexIndex;
	private float speed;
	private boolean finished;
	
	public Screen(Component parent) {
		parent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) { fitToParent(e); }
			
			@Override
			public void componentResized(ComponentEvent e) { fitToParent(e); }
			
			@Override
			public void componentMoved(ComponentEvent e) { fitToParent(e); }
			
			@Override
			public void componentHidden(ComponentEvent e) { fitToParent(e); }
		});
		
		thread = new Thread(() -> {
			requestFocus();
			while(isRunning) {
				BufferStrategy bs = getBufferStrategy();
				if(bs == null) {
					createBufferStrategy(3);
					continue;
				}
				
				Graphics2D g = (Graphics2D) bs.getDrawGraphics();
				draw(g);				
				
				g.dispose();
				bs.show();
			}
		});
		
		this.verticies = new ArrayList<Vector2f>();
		this.verticies.add(new Vector2f(100, 330/1.5f));
		this.verticies.add(new Vector2f(220, 350/1.5f));
		this.verticies.add(new Vector2f(340, 480/1.5f));
		this.verticies.add(new Vector2f(430, 380/1.5f));
		this.verticies.add(new Vector2f(400, 570/1.5f));
		
		this.position = verticies.get(0).add(verticies.get(1).sub(verticies.get(0)).normalized().mul(50).rotate(90));
		this.direction = new Vector2f(1, 0);
		
		this.vertexIndex = 0;
		this.finished = false;
		this.speed = 0.1f;
	}
	
	private void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		
		for(int i = 0; i < verticies.size() - 1; i++) {
			g.drawLine((int)verticies.get(i).getX(), (int)verticies.get(i).getY(), (int)verticies.get(i + 1).getX(), (int)verticies.get(i + 1).getY());
		}
		
		if(!finished && vertexIndex < verticies.size() - 1) {
			PathTrackResult result = PathTrack.getAngleofDeviation(verticies, vertexIndex, position, direction);
			vertexIndex = result.getVertexIndex();
			direction.set(direction.rotate(result.getAngle() / 10));
		} else {
			finished = true;
		}
		
		drawCart(g, position, direction);
		if(!finished) {
			position.set(position.add(direction.mul(speed)));
		}
	}
	
	private void drawCart(Graphics2D g1, Vector2f position, Vector2f direction) {
		Graphics2D g = (Graphics2D) g1.create();
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3));
		int ARR_SIZE = 10;
		
		double dx = direction.getX();
		double dy = direction.getY();
		double angle = Math.atan2(dy, dx);
		int len = (int) direction.length();
		AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);
		
		g.fillPolygon(new int[] {len, len - ARR_SIZE, len - ARR_SIZE}, new int[] {0, -ARR_SIZE, ARR_SIZE}, 3);
	}
	
	public void fitToParent(ComponentEvent e) {
		setBounds(1, 1, e.getComponent().getWidth() - 2, e.getComponent().getHeight() - 2);
	}
	
	public void start() {
		isRunning = true;
		thread.start();
	}
}