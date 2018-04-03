package org.bitbrawl.foodfight.engine.video;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.bitbrawl.foodfight.field.Food;

public final class ImageResources {

	private final BufferedImage table = FrameGenerator.copyImage(staticTable);
	private final Map<Food.Type, BufferedImage> food = new EnumMap<>(Food.Type.class);
	private final BufferedImage collision = FrameGenerator.copyImage(staticCollision);
	private final List<BufferedImage> players;
	private final List<BufferedImage> colors;
	private final List<BufferedImage> profiles;
	private final List<BufferedImage> colorProfiles;

	public ImageResources() {

		for (Entry<Food.Type, BufferedImage> entry : staticFood.entrySet())
			food.put(entry.getKey(), FrameGenerator.copyImage(entry.getValue()));

		BufferedImage[] playerArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] colorArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] profileArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] colorProfileArray = new BufferedImage[NUM_PLAYER_IMAGES];
		for (int i = 0; i < NUM_PLAYER_IMAGES; i++) {
			playerArray[i] = FrameGenerator.copyImage(staticPlayers.get(i));
			colorArray[i] = FrameGenerator.copyImage(staticColors.get(i));
			profileArray[i] = FrameGenerator.copyImage(staticProfiles.get(i));
			colorProfileArray[i] = FrameGenerator.copyImage(staticColorProfiles.get(i));
		}
		players = Arrays.asList(playerArray);
		colors = Arrays.asList(colorArray);
		profiles = Arrays.asList(profileArray);
		colorProfiles = Arrays.asList(colorProfileArray);

	}

	public BufferedImage getTable() {
		return table;
	}

	public BufferedImage getFood(Food.Type type) {
		return food.get(type);
	}

	public BufferedImage getCollision() {
		return collision;
	}

	public BufferedImage getPlayer(int index) {
		return players.get(index);
	}

	public BufferedImage getColor(int index) {
		return colors.get(index);
	}

	public BufferedImage getProfile(int index) {
		return profiles.get(index);
	}

	public BufferedImage getColorProfile(int index) {
		return colorProfiles.get(index);
	}

	private static BufferedImage getImage(String name, boolean transparent) {

		BufferedImage image;
		try (InputStream stream = ImageResources.class.getResourceAsStream("/images/" + name)) {
			if (stream == null)
				throw new IllegalStateException("Unable to get image: " + name);
			image = ImageIO.read(stream);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to get image: " + name, e);
		}

		int type = transparent ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR;
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), type);
		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return result;
	}

	public static final int NUM_PLAYER_IMAGES = 6;
	private static final BufferedImage staticTable = getImage("table.png", true);
	private static final Map<Food.Type, BufferedImage> staticFood = new EnumMap<>(Food.Type.class);
	private static final BufferedImage staticCollision = getImage("collision.png", true);
	private static final List<BufferedImage> staticPlayers;
	private static final List<BufferedImage> staticColors;
	private static final List<BufferedImage> staticProfiles;
	private static final List<BufferedImage> staticColorProfiles;

	static {

		staticFood.put(Food.Type.APPLE, getImage("apple.png", true));
		staticFood.put(Food.Type.BANANA, getImage("banana.png", true));
		staticFood.put(Food.Type.BROCCOLI, getImage("broccoli.png", true));
		staticFood.put(Food.Type.CHOCOLATE, getImage("chocolate.png", true));
		staticFood.put(Food.Type.MILK, getImage("milk.png", true));
		staticFood.put(Food.Type.PIE, getImage("pie.png", true));
		staticFood.put(Food.Type.RASPBERRY, getImage("raspberry.png", true));
		staticFood.put(Food.Type.SANDWICH, getImage("sandwich.png", true));

		BufferedImage[] playerArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] colorArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] profileArray = new BufferedImage[NUM_PLAYER_IMAGES];
		BufferedImage[] colorProfileArray = new BufferedImage[NUM_PLAYER_IMAGES];
		for (int i = 0; i < NUM_PLAYER_IMAGES; i++) {
			playerArray[i] = getImage("player_" + i + ".png", true);
			colorArray[i] = getImage("player_" + i + "_color.png", true);
			profileArray[i] = getImage("player_" + i + "_profile.png", true);
			colorProfileArray[i] = getImage("player_" + i + "_profile_color.png", true);
		}
		staticPlayers = Arrays.asList(playerArray);
		staticColors = Arrays.asList(colorArray);
		staticProfiles = Arrays.asList(profileArray);
		staticColorProfiles = Arrays.asList(colorProfileArray);

	}

}
