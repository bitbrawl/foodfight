package org.bitbrawl.foodfight.engine.video;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public final class ImageFrame extends JFrame {

	private final ImagePane pane;

	public ImageFrame(BufferedImage image) {
		super("BitBrawl FoodFight Visualizer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		pane = new ImagePane(image);
		add(pane);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void updateImage(BufferedImage image) {
		pane.updateImage(image);
	}

	private static final long serialVersionUID = 1L;

}
