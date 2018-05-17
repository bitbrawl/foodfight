package org.bitbrawl.foodfight.engine.video;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.field.Field;

public final class ImageFrame extends JFrame {

	private final Function<FieldState, BufferedImage> frameGenerator;
	private final ImagePane pane;
	private final JSlider slider = new JSlider(1, 1, 1);
	private final List<FieldState> fields = new ArrayList<>(Field.TOTAL_TURNS + 1);

	public ImageFrame(Function<FieldState, BufferedImage> frameGenerator, FieldState initialField) {
		super("BitBrawl FoodFight Visualizer");
		this.frameGenerator = frameGenerator;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		fields.add(initialField);
		pane = new ImagePane(frameGenerator.apply(initialField));
		add(pane, BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		slider.addChangeListener(this::sliderMove);
		setVisible(true);
	}

	public void updateImage(FieldState field) {
		fields.add(field);
		boolean atMax = slider.getValue() == slider.getMaximum();
		int max = fields.size();
		slider.setMaximum(max);
		if (atMax)
			slider.setValue(max);
	}

	public void sliderMove(ChangeEvent e) {
		if (slider.getValueIsAdjusting())
			return;
		BufferedImage newImage = frameGenerator.apply(fields.get(slider.getValue() - 1));
		pane.updateImage(newImage);
	}

	private static final long serialVersionUID = 1L;

}
