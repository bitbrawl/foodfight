package org.bitbrawl.foodfight.engine.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Player.Hand;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.PlayerUtils;
import org.bitbrawl.foodfight.util.Vector;

public final class FrameGenerator implements Function<FieldState, BufferedImage> {

	private final ImageResources resources = new ImageResources();
	private final BufferedImage background;
	private final Map<Character, Float> playerColors = new HashMap<>();
	private final Map<Character, BufferedImage> playerImages = new HashMap<>();
	private final Map<Character, BufferedImage> profileImages = new HashMap<>();
	// private final FontMetrics metrics;

	public FrameGenerator(FieldState initialState) {

		background = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics = background.createGraphics();
		graphics.setColor(Color.LIGHT_GRAY.brighter());
		graphics.fillRect(0, 0, FIELD_WIDTH, FRAME_HEIGHT);

		List<Integer> imageNumbers = IntStream.range(0, ImageResources.NUM_PLAYER_IMAGES).boxed()
				.collect(Collectors.toList());
		Collections.shuffle(imageNumbers);

		Set<TeamState> teams = initialState.getTeamStates();
		Map<TeamState, Float> teamColors = generateTeamColors(teams);
		for (TeamState team : teams)
			playerColors.putAll(generatePlayerColors(team.getPlayerStates(), teamColors.get(team)));

		int playerBoxStartX = (int) Math.round(Field.WIDTH) + BORDER;
		int playerBoxWidth = FRAME_WIDTH - playerBoxStartX;
		int playerBoxStartY = 0;
		for (Team team : initialState.getTeams()) {
			float teamHue = teamColors.get(team);

			BufferedImage tableImage = tintImage(resources.getTable(), resources.getTable(), teamHue);
			drawObject(team.getTable().getLocation(), Direction.NORTH, tableImage, graphics);

			for (Player player : team.getPlayers()) {

				Color idColor = getColor(getColor(player), 0.5F);
				Color teamColor = getColor(teamHue, 0.9F);

				// player box
				graphics.setColor(teamColor);
				graphics.fillRect(playerBoxStartX, playerBoxStartY, playerBoxWidth, PLAYER_FRAME_HEIGHT);

				// player symbol on black background
				graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
				graphics.setColor(Color.BLACK);
				FontMetrics metrics = graphics.getFontMetrics();
				String playerTeamSymbol = team.getSymbol() + "" + player.getSymbol();
				int characterAscent = metrics.getAscent();
				int fontHeight = characterAscent + metrics.getDescent();
				int characterWidth = metrics.stringWidth(playerTeamSymbol);
				graphics.fillRect(playerBoxStartX, playerBoxStartY, 20 + characterWidth, 20 + fontHeight);
				graphics.setColor(idColor);
				graphics.drawString(playerTeamSymbol, playerBoxStartX + 10, playerBoxStartY + 10 + characterAscent);

				// player info text
				graphics.setColor(Color.BLACK);
				graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
				int smallAscent = graphics.getFontMetrics().getAscent();

				// generating player images
				char playerSymbol = player.getSymbol();
				int imageNumber = imageNumbers.remove(0);
				playerImages.put(playerSymbol, generatePlayerImage(imageNumber, teamHue));
				profileImages.put(playerSymbol, generateProfileImage(imageNumber, teamHue));

				playerBoxStartY += PLAYER_FRAME_HEIGHT + BORDER;

			}
		}

		graphics.dispose();

	}

	@Override
	public BufferedImage apply(FieldState field) {

		BufferedImage result = copyImage(background);
		Graphics2D graphics = result.createGraphics();

		graphics.setClip(0, 0, FIELD_WIDTH, FRAME_HEIGHT);

		for (Collision collision : field.getCollisions())
			drawObject(collision.getLocation(), Direction.random(), resources.getCollision(), graphics);

		for (Food food : field.getFood()) {
			BufferedImage foodImage = resources.getFood(food.getType());
			drawObject(food.getLocation(), food.getHeading(), foodImage, graphics);
		}

		for (Team team : field.getTeams()) {

			Table table = team.getTable();
			Iterator<Food.Type> it = table.getFood().iterator();
			if (it.hasNext()) {
				BufferedImage foodImage = resources.getFood(it.next());
				Vector location = table.getLocation().add(Vector.cartesian(-50.0, 50.0));
				drawObject(location, Direction.NORTH, foodImage, graphics);
			}
			if (it.hasNext()) {
				BufferedImage foodImage = resources.getFood(it.next());
				Vector location = table.getLocation().add(Vector.cartesian(50.0, 50.0));
				drawObject(location, Direction.NORTH, foodImage, graphics);
			}
			if (it.hasNext()) {
				BufferedImage foodImage = resources.getFood(it.next());
				Vector location = table.getLocation().add(Vector.cartesian(-50.0, -50.0));
				drawObject(location, Direction.NORTH, foodImage, graphics);
			}
			if (it.hasNext()) {
				BufferedImage foodImage = resources.getFood(it.next());
				Vector location = table.getLocation().add(Vector.cartesian(50.0, -50.0));
				drawObject(location, Direction.NORTH, foodImage, graphics);
			}

			for (Player player : team.getPlayers()) {

				drawFoodInHand(player, Hand.LEFT, resources, graphics);
				drawFoodInHand(player, Hand.RIGHT, resources, graphics);

				Vector location = player.getLocation();
				drawObject(location, player.getHeading(), getImage(player), graphics);

				float fontRadius = 10.0F;
				float labelX = (float) location.getX();
				float labelY = (float) (Field.DEPTH - location.getY());
				if (labelX < 2.0F * fontRadius) {
					labelX += 100.0F;
					if (labelY < 2.0F * fontRadius)
						labelY += 100.0F;
					else if (labelY > Field.DEPTH - 2.0F * fontRadius)
						labelY -= 100.0F;
				} else if (labelX > Field.WIDTH - 2.0F * fontRadius) {
					labelX -= 100.0F;
					if (labelY < 2.0F * fontRadius)
						labelY += 100.0F;
					else if (labelY > Field.DEPTH - 2.0F * fontRadius)
						labelY -= 100.0F;
				} else {
					labelY -= 100.0F;
					if (labelY < 2.0F * fontRadius)
						labelY += 200.0F;
				}
				graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
				graphics.setColor(getColor(getColor(player), 0.5F));
				String id = team.getSymbol() + "" + player.getSymbol();
				graphics.drawString(id, labelX - fontRadius, labelY - fontRadius);

			}
		}

		graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 100));
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.drawString(Integer.toString(field.getTurnNumber()), 0, FRAME_HEIGHT);

		int playerBoxStartX = FIELD_WIDTH + BORDER;
		int playerBoxWidth = FRAME_WIDTH - playerBoxStartX;
		int playerBoxStartY = 0;
		for (Team team : field.getTeams())
			for (Player player : team.getPlayers()) {

				graphics.setClip(playerBoxStartX, playerBoxStartY, playerBoxWidth, PLAYER_FRAME_HEIGHT);

				graphics.setColor(Color.BLACK);
				graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
				FontMetrics metrics = graphics.getFontMetrics();
				// TODO figure out these calculations
				graphics.drawString(Integer.toString(team.getScore().getTotalPoints()),
						playerBoxStartX + 80 + metrics.charWidth('A'), playerBoxStartY + 10 + metrics.getAscent());
				// TODO figure out how to get timing over here
				// graphics.drawString(String.format("Time left: %6.3fs",
				// player.getTimeLeft()),
				// scaled(playerBoxStartX + 10),
				// scaled(playerBoxStartY - 10) + PLAYER_FRAME_HEIGHT -
				// graphics.getFontMetrics().getDescent());

				BufferedImage sideImage = getProfile(player);
				int height = playerBoxStartY + PLAYER_FRAME_HEIGHT - sideImage.getHeight()
						+ (int) (Player.HEIGHT - player.getHeight());
				graphics.drawImage(sideImage, FRAME_WIDTH - sideImage.getWidth() - 20, height, null);
				playerBoxStartY += PLAYER_FRAME_HEIGHT + BORDER;
			}

		graphics.dispose();

		return result;

	}

	static BufferedImage copyImage(BufferedImage image) {
		ColorModel model = image.getColorModel();
		boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(model, raster, isAlphaPremultiplied, null);
	}

	private static Map<TeamState, Float> generateTeamColors(Set<? extends TeamState> teams) {

		int numTeams = teams.size();
		List<Float> colors = new ArrayList<>(numTeams);
		for (float color = ThreadLocalRandom.current().nextFloat() / numTeams; color < 1.0; color += 1.0 / numTeams)
			colors.add(color);
		Collections.shuffle(colors, ThreadLocalRandom.current());

		Map<TeamState, Float> result = new HashMap<>(teams.size());
		for (TeamState team : teams)
			result.put(team, colors.remove(colors.size() - 1));

		return result;

	}

	private static Map<Character, Float> generatePlayerColors(Set<? extends PlayerState> players, float teamColor) {

		List<Float> colors = new LinkedList<>();

		switch (players.size()) {
		case 1:
			colors.add(teamColor);
			break;

		case 2:
			float color = teamColor - 0.125F;
			if (color < 0.0)
				color += 1.0F;
			colors.add(color);
			color = teamColor + 0.125F;
			if (color >= 1.0)
				color -= 1.0F;
			colors.add(color);
			break;

		default:
			throw new IllegalArgumentException("Wrong number of players: " + players.size());
		}

		Collections.shuffle(colors, ThreadLocalRandom.current());

		Map<Character, Float> result = new HashMap<>();
		for (PlayerState player : players)
			result.put(player.getSymbol(), colors.get(colors.size() - 1));

		return result;

	}

	/*
	 * Based on calculations at
	 * https://en.wikipedia.org/wiki/HSL_and_HSV#From_luma.2Fchroma.2Fhue
	 */
	private static Color getColor(float hue, float luma) {
		float chroma = 1.0F;

		float hPrime = hue * 6.0F;
		float x2 = 1.0F - Math.abs(hPrime % 2 - 1.0F);
		float r2, g2, b2;
		if (hPrime < 1.0F) {
			r2 = 1.0F;
			g2 = x2;
			b2 = 0.0F;
		} else if (hPrime < 2.0F) {
			r2 = x2;
			g2 = 1.0F;
			b2 = 0.0F;
		} else if (hPrime < 3.0F) {
			r2 = 0.0F;
			g2 = 1.0F;
			b2 = x2;
		} else if (hPrime < 4.0F) {
			r2 = 0.0F;
			g2 = x2;
			b2 = 1.0F;
		} else if (hPrime < 5.0F) {
			r2 = x2;
			g2 = 0.0F;
			b2 = 1.0F;
		} else {
			r2 = 1.0F;
			g2 = 0.0F;
			b2 = x2;
		}
		float y2 = 0.299F * r2 + 0.587F * g2 + 0.114F * b2;
		if (hPrime < 1.0F) {
			chroma = Math.min(chroma, (1.0F - luma) / (r2 - y2));
			chroma = Math.min(chroma, -luma / (b2 - y2));
		} else if (hPrime < 2.0F) {
			chroma = Math.min(chroma, (1.0F - luma) / (g2 - y2));
			chroma = Math.min(chroma, -luma / (b2 - y2));
		} else if (hPrime < 3.0F) {
			chroma = Math.min(chroma, (1.0F - luma) / (g2 - y2));
			chroma = Math.min(chroma, -luma / (r2 - y2));
		} else if (hPrime < 4.0F) {
			chroma = Math.min(chroma, (1.0F - luma) / (b2 - y2));
			chroma = Math.min(chroma, -luma / (r2 - y2));
		} else if (hPrime < 5.0F) {
			chroma = Math.min(chroma, (1.0F - luma) / (b2 - y2));
			chroma = Math.min(chroma, -luma / (g2 - y2));
		} else {
			chroma = Math.min(chroma, (1.0F - luma) / (r2 - y2));
			chroma = Math.min(chroma, -luma / (g2 - y2));
		}

		float r1 = chroma * r2;
		float g1 = chroma * g2;
		float b1 = chroma * b2;
		float m = luma - chroma * y2;
		float r = r1 + m;
		if (-0.0001F < r && r < 0.0F)
			r = 0.0F;
		else if (1.0F < r && r < 1.0001F)
			r = 1.0F;
		float g = g1 + m;
		if (-0.0001F < g && g < 0.0F)
			g = 0.0F;
		else if (1.0F < g && g < 1.0001F)
			g = 1.0F;
		float b = b1 + m;
		if (-0.0001F < b && b < 0.0F)
			b = 0.0F;
		else if (1.0F < b && b < 1.0001F)
			b = 1.0F;
		return new Color(r, g, b);
	}

	private BufferedImage generatePlayerImage(int imageNumber, float hue) {

		BufferedImage playerImage = resources.getPlayer(imageNumber);
		BufferedImage colorImage = resources.getColor(imageNumber);
		return tintImage(playerImage, colorImage, hue);

	}

	private BufferedImage generateProfileImage(int imageNumber, float hue) {

		BufferedImage profileImage = resources.getProfile(imageNumber);
		BufferedImage colorImage = resources.getColorProfile(imageNumber);
		return tintImage(profileImage, colorImage, hue);

	}

	private float getColor(Player player) {
		return playerColors.get(player.getSymbol());
	}

	private BufferedImage getImage(Player player) {
		return playerImages.get(player.getSymbol());
	}

	private BufferedImage getProfile(Player player) {
		return profileImages.get(player.getSymbol());
	}

	private static void drawObject(Vector location, Direction heading, BufferedImage image, Graphics2D graphics) {
		assert location != null;
		assert heading != null;
		assert image != null;
		assert graphics != null;

		AffineTransform cachedTransform = graphics.getTransform();

		graphics.translate(location.getX() - image.getWidth() / 2.0,
				Field.DEPTH - location.getY() - image.getHeight() / 2.0);
		graphics.rotate(Math.PI / 2.0 - heading.get(), image.getWidth() / 2.0, image.getHeight() / 2.0);
		graphics.drawImage(image, 0, 0, null);

		graphics.setTransform(cachedTransform);

	}

	private static void drawFoodInHand(Player player, Hand hand, ImageResources resources, Graphics2D graphics) {
		Food.Type type = player.getInventory().get(hand);
		if (type != null) {
			Direction direction = PlayerUtils.getArmDirection(player, hand);
			Vector location = player.getLocation().add(Vector.polar(Player.REACH_DISTANCE / 2.0, direction));
			drawObject(location, direction, resources.getFood(type), graphics);
		}

	}

	private static BufferedImage tintImage(BufferedImage playerImage, BufferedImage colorImage, float hue) {

		BufferedImage result = copyImage(playerImage);

		for (int x = 0; x < playerImage.getWidth(); x++)
			for (int y = 0; y < playerImage.getHeight(); y++) {

				Color color = new Color(colorImage.getRGB(x, y), true);
				if (color.getAlpha() == 0)
					continue;

				float luma = getLuma(color);
				Color newColor = getColor(hue, luma);
				result.setRGB(x, y, newColor.getRGB());

			}

		return result;

	}

	private static float getLuma(Color color) {
		return (0.299F * color.getRed() + 0.587F * color.getGreen() + 0.114F * color.getBlue()) / 256F;
	}

	private static final int FIELD_WIDTH = (int) Math.round(Field.WIDTH);
	private static final int FRAME_HEIGHT = (int) Math.round(Field.DEPTH), FRAME_WIDTH = FRAME_HEIGHT * 16 / 9;
	private static final int BORDER = 20;
	private static final int PLAYER_FRAME_HEIGHT = (FRAME_HEIGHT - 3 * BORDER) / 4;

}
