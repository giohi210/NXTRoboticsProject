package com.nxtproject.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Window extends JFrame {
	private static final long serialVersionUID = -4607715234281903488L;
	
	private JPanel contentPane;
	private Screen grid;
	
	public Window() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.setProperty("sun.awt.noerasebackground", "true");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Grid Power Usage Calculator");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JPanel listPanel = new JPanel();
		BorderLayout lpLayout = new BorderLayout(0, 0);
		listPanel.setLayout(lpLayout);
		
		JPanel drawPanel = new JPanel();
		drawPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		drawPanel.setPreferredSize(new Dimension(150, 170));
		drawPanel.setLayout(null);
		
		grid = new Screen(drawPanel);
		grid.setBounds(0, 1, 974, 508);
		drawPanel.add(grid);
		
		listPanel.add(drawPanel, BorderLayout.CENTER);
		
		contentPane.add(listPanel, BorderLayout.CENTER);
		
		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		outputPanel.setLayout(new GridLayout());
		
		JTextPane consolePane = new JTextPane();
		consolePane.setEditable(false);
		consolePane.setContentType("HTML/plain");
		consolePane.setFont(new Font("Courier New", 0, 13));
		
		JScrollPane scrollPane = new JScrollPane(consolePane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setPreferredSize(new Dimension(100, 120));
	    scrollPane.setMaximumSize(new Dimension(100, 120));
	    scrollPane.setAutoscrolls(true);
		outputPanel.add(scrollPane);
		
		contentPane.add(outputPanel, BorderLayout.SOUTH);
	}
	
	private void start() {
		grid.start();
	}
	
	public void showWindow() {
		EventQueue.invokeLater(() -> {
			Window frame = new Window();
			frame.setVisible(true);
			frame.start();
		});
	}
}