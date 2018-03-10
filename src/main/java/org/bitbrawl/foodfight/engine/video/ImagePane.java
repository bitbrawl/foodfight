package org.bitbrawl.foodfight.engine.video;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

import javax.swing.JPanel;

// inspired by https://stackoverflow.com/a/14553003/3965949
public final class ImagePane extends JPanel {

	private Image image;
	private Image scaled;

	public ImagePane(Image image) {
		this.image = Objects.requireNonNull(image);
	}

	public void updateImage(Image image) {
		this.image = Objects.requireNonNull(image);
		generateScaled();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(image.getWidth(this), image.getHeight(this));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Image draw = scaled == null ? image : scaled;

		int x = (getWidth() - draw.getWidth(this)) / 2;
		int y = (getHeight() - draw.getHeight(this)) / 2;
		g.drawImage(draw, x, y, this);

	}

	@Override
	public void invalidate() {
		generateScaled();
		super.invalidate();
	}

	private void generateScaled() {

		scaled = null;

		Dimension newSize = getSize();
		int newWidth = newSize.width;
		int newHeight = newSize.height;
		if (newWidth == 0 && newHeight == 0)
			return;
		double originalRatio = (double) image.getWidth(this) / image.getHeight(this);
		double panelRatio = (double) newWidth / newHeight;
		if (originalRatio < panelRatio)
			scaled = image.getScaledInstance(-1, newHeight, Image.SCALE_DEFAULT);
		else
			scaled = image.getScaledInstance(newWidth, -1, Image.SCALE_DEFAULT);

	}

	private static final long serialVersionUID = 1L;

}
