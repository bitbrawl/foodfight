package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.logging.Level;

import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.engine.field.CollisionState;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.FoodState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.field.Event;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.PlayerComparator;
import org.bitbrawl.foodfight.util.PlayerUtils;
import org.bitbrawl.foodfight.util.Vector;

public final class DefaultTurnRunner implements TurnRunner {

	public DefaultTurnRunner() {
	}

	@Override
	public FieldState runTurn(FieldState field, Map<? extends Character, ? extends Action> actions) {
		assert field != null;
		assert actions != null;

		field = playActions(field, actions);

		field = moveFood(field);

		field = enactCollisions(field);

		field = addFoodPoints(field);

		if (field.getTurnNumber() >= Field.TOTAL_TURNS)
			field = breakTies(field);

		return new FieldState(field.getTurnNumber() + 1, field.getMatchType(), field.getTeamStates(), spawnFood(field),
				field.getCollisionStates());

	}

	private FieldState playActions(FieldState field, Map<? extends Character, ? extends Action> actions) {
		assert field != null;
		assert actions != null;

		List<Character> symbols = new ArrayList<>(actions.keySet());
		Collections.shuffle(symbols);

		for (char symbol : symbols) {
			outer: for (TeamState team : field.getTeamStates()) {
				for (PlayerState player : team.getPlayerStates()) {
					if (symbol == player.getSymbol()) {
						field = playAction(field, team, player, actions.get(symbol));
						break outer;
					}
				}
			}
		}

		return field;

	}

	private FieldState moveFood(FieldState field) {
		assert field != null;

		Set<FoodState> foods = new LinkedHashSet<>();
		Map<TableState, Set<Food.Type>> landedFood = new HashMap<>();
		outer: for (FoodState food : field.getFoodStates()) {

			double height = food.getHeight();
			assert height >= 0.0;
			if (height == 0.0) {
				foods.add(food);
				continue;
			}

			Direction heading = food.getHeading();
			Vector horizontalVelocity = Vector.polar(Food.SPEED.getAsDouble(), heading);
			Vector location = food.getLocation().add(horizontalVelocity);
			double x = location.getX();
			double y = location.getY();

			double halfPi = 0.5 * Math.PI;

			double radius = food.getType().getRadius();
			if (x < radius) {
				x = -x;
				if (Math.abs(Direction.difference(heading, Direction.WEST)) < halfPi)
					heading = heading.reflectAcrossY();
			} else if (x > Field.WIDTH - radius) {
				x = Field.WIDTH * 2.0 - x;
				if (Math.abs(Direction.difference(heading, Direction.EAST)) < halfPi)
					heading = heading.reflectAcrossY();
			}
			if (y < radius) {
				y = -y;
				if (Math.abs(Direction.difference(heading, Direction.SOUTH)) < halfPi)
					heading = heading.reflectAcrossX();
			} else if (y > Field.DEPTH - radius) {
				y = Field.DEPTH * 2.0 - y;
				if (Math.abs(Direction.difference(heading, Direction.NORTH)) < halfPi)
					heading = heading.reflectAcrossX();
			}

			height -= Food.FALL_SPEED.getAsDouble();
			if (height < 0.0)
				height = 0.0;

			for (TeamState team : field.getTeamStates()) {
				TableState table = team.getTable();

				if (table.getEdge(Direction.WEST) <= x && x <= table.getEdge(Direction.EAST)) {
					if (table.getEdge(Direction.SOUTH) <= y && y <= table.getEdge(Direction.NORTH)) {

						landedFood.computeIfAbsent(table, k -> EnumSet.noneOf(Food.Type.class)).add(food.getType());
						continue outer;

					}

				}

			}

			foods.add(new FoodState(food.getType(), location, height, heading));

		}

		Set<TeamState> teams = new LinkedHashSet<>();
		for (TeamState team : field.getTeamStates()) {
			TableState table = team.getTable();

			Set<Food.Type> foodToRemove = landedFood.get(table);
			if (foodToRemove == null) {
				teams.add(team);
			} else {
				Set<Food.Type> foodOnTable = EnumSet.noneOf(Food.Type.class);
				foodOnTable.addAll(table.getFood());
				foodOnTable.addAll(foodToRemove);
				TableState newTable = new TableState(table.getLocation(), foodOnTable);

				teams.add(new TeamState(team.getSymbol(), team.getPlayerStates(), newTable, team.getScore()));

			}

		}

		return new FieldState(field.getTurnNumber(), field.getMatchType(), teams, foods, field.getCollisionStates());

	}

	private FieldState enactCollisions(FieldState field) {

		Set<CollisionState> collisions = new HashSet<>();

		Comparator<Player> playerComparator = PlayerComparator.INSTANCE;

		Map<TeamState, ScoreState> scores = new HashMap<>();
		Map<PlayerState, Double> damages = new HashMap<>();

		for (TeamState team : field.getTeamStates())
			for (PlayerState player : team.getPlayerStates()) {

				Vector playerLocation = player.getLocation();

				for (TeamState otherTeam : field.getTeamStates())
					for (PlayerState otherPlayer : otherTeam.getPlayerStates()) {
						if (playerComparator.compare(player, otherPlayer) >= 0)
							continue;

						Vector otherLocation = otherPlayer.getLocation();
						double collisionDistance = Player.COLLISION_RADIUS * 2.0;

						if (playerLocation.subtract(otherLocation).getMagnitude() < collisionDistance) {
							Vector collisionLocation = Vector.average(playerLocation, otherLocation);
							double damage = Player.COLLISION_DAMAGE.getAsDouble();
							collisions.add(new CollisionState(collisionLocation, damage));
							damages.merge(player, damage, Double::sum);
							damages.merge(otherPlayer, damage, Double::sum);
							BiFunction<TeamState, ScoreState, ScoreState> remapping = (k, v) -> {
								if (v == null)
									v = k.getScore();
								return v.addEvent(Event.FIRST_PLAYER_COLLISION).addEvent(Event.EVERY_PLAYER_COLLISION);
							};
							scores.compute(team, remapping);
							scores.compute(otherTeam, remapping);
						}

					}

				for (FoodState food : field.getFoodStates()) {
					if (food.getHeight() <= 0.0)
						continue;

					if (player.getHeight() < food.getHeight())
						continue;

					Vector foodLocation = food.getLocation();
					double foodRadius = food.getType().getRadius();
					double collisionDistance = Player.COLLISION_RADIUS + foodRadius;

					if (playerLocation.subtract(foodLocation).getMagnitude() < collisionDistance) {
						Vector collisionLocation = playerLocation.multiply(foodRadius)
								.add(foodLocation.multiply(Player.COLLISION_RADIUS)).divide(collisionDistance);
						double damage = food.getType().getDamage().getAsDouble();
						collisions.add(new CollisionState(collisionLocation, damage));
						damages.merge(player, damage, Double::sum);
						BiFunction<TeamState, ScoreState, ScoreState> remapping = (k, v) -> {
							if (v == null)
								v = k.getScore();
							return v.addEvent(Event.FIRST_FOOD_COLLISION).addEvent(Event.EVERY_FOOD_COLLISION);
						};
						scores.compute(team, remapping);
					}

				}

			}

		Map<PlayerState, Vector> knockbacks = new HashMap<>();
		Set<FoodState> remainingFood = new LinkedHashSet<>(field.getFoodStates());

		for (CollisionState collision : collisions) {

			Vector collisionLocation = collision.getLocation();

			for (TeamState team : field.getTeamStates()) {

				for (PlayerState player : team.getPlayerStates()) {

					Vector playerLocation = player.getLocation();

					Vector collisionToPlayer = playerLocation.subtract(collisionLocation);
					double distance = collisionToPlayer.getMagnitude();

					double knockback = Player.COLLISION_RADIUS * Math.exp(-distance / Player.COLLISION_RADIUS);
					Vector knockbackVector = Vector.polar(knockback, collisionToPlayer.getDirection());

					knockbacks.merge(player, knockbackVector, Vector::add);

				}

			}

			for (Iterator<FoodState> it = remainingFood.iterator(); it.hasNext();) {
				FoodState food = it.next();

				double distance = food.getLocation().subtract(collisionLocation).getMagnitude();
				if (distance < food.getType().getRadius())
					it.remove();

			}

		}

		Set<TeamState> teams = new LinkedHashSet<>();
		for (TeamState team : field.getTeamStates()) {

			Set<PlayerState> players = new LinkedHashSet<>();
			for (PlayerState player : team.getPlayerStates()) {

				Vector knockback = knockbacks.get(player);
				Double damage = damages.getOrDefault(player, 0.0);

				if (knockback == null && damage == 0.0) {
					players.add(player);
				} else {
					Vector location = movePlayer(field, player.getLocation(), knockback);
					double energy = Math.max(player.getEnergy() - damage, 0);
					players.add(new PlayerState(player.getSymbol(), location, player.getHeight(), player.getHeading(),
							player.getInventory(), energy));
				}

			}

			ScoreState score = scores.getOrDefault(team, team.getScore());

			teams.add(new TeamState(team.getSymbol(), players, team.getTable(), score));

		}

		return new FieldState(field.getTurnNumber(), field.getMatchType(), teams, remainingFood, collisions);

	}

	private FieldState addFoodPoints(FieldState field) {

		Set<TeamState> teams = new LinkedHashSet<>();
		for (TeamState team : field.getTeamStates()) {
			ScoreState score = team.getScore();
			for (int i = 0, n = team.getTable().getFood().size(); i < n; i++)
				score = score.addEvent(Event.FOOD_ON_TABLE);
			teams.add(new TeamState(team.getSymbol(), team.getPlayerStates(), team.getTable(), score));
		}

		return new FieldState(field.getTurnNumber(), field.getMatchType(), teams, field.getFoodStates(),
				field.getCollisionStates());

	}

	private Set<FoodState> spawnFood(FieldState field) {
		Set<FoodState> fieldFood = field.getFoodStates();
		Set<Food.Type> availableTypes = EnumSet.allOf(Food.Type.class);
		for (FoodState food : fieldFood)
			availableTypes.remove(food.getType());
		for (TeamState team : field.getTeamStates()) {
			availableTypes.removeAll(team.getTable().getFood());
			for (PlayerState player : team.getPlayerStates())
				for (Player.Hand hand : Player.Hand.values()) {
					Food.Type type = player.getInventory().get(hand);
					if (type != null)
						availableTypes.remove(type);
				}
		}
		if (availableTypes.size() <= 4 || ThreadLocalRandom.current().nextDouble() >= Food.RESPAWN_RATE)
			return fieldFood;
		Food.Type type = randomElementFrom(availableTypes);
		double radius = type.getRadius();
		outer: while (true) {
			double x = ThreadLocalRandom.current().nextDouble(radius, Field.WIDTH - radius);
			double y = ThreadLocalRandom.current().nextDouble(radius, Field.DEPTH - radius);
			Vector newLocation = Vector.cartesian(x, y);
			for (PlayerState player : field.getPlayerStates()) {
				double distance = player.getLocation().subtract(newLocation).getMagnitude();
				if (distance < Player.COLLISION_RADIUS + radius)
					continue outer;
			}
			for (FoodState food : field.getFoodStates()) {
				double distance = food.getLocation().subtract(newLocation).getMagnitude();
				if (distance < food.getType().getRadius() + radius)
					continue outer;
			}
			for (TeamState team : field.getTeamStates()) {
				TableState table = team.getTable();
				if (table.getEdge(Direction.WEST) < x && x < table.getEdge(Direction.EAST))
					continue outer;
				if (table.getEdge(Direction.SOUTH) < y && y < table.getEdge(Direction.NORTH))
					continue outer;
			}
			Set<FoodState> result = new LinkedHashSet<>(fieldFood);
			result.add(new FoodState(type, newLocation, 0, Direction.NORTH));
			return result;
		}
	}

	private FieldState breakTies(FieldState field) {

		Set<TeamState> teams = field.getTeamStates();
		List<TeamState> orderedTeams = new ArrayList<>(teams);

		Map<TeamState, Double> distancesToCenter = new HashMap<>();
		double centerX = Field.WIDTH / 2.0, centerY = Field.DEPTH / 2.0;
		for (TeamState team : orderedTeams) {
			double teamDist = 0.0;
			for (PlayerState player : team.getPlayerStates()) {
				Vector location = player.getLocation();
				double dx = location.getX() - centerX;
				double dy = location.getY() - centerY;
				teamDist += dx * dx + dy * dy;
			}
			distancesToCenter.put(team, teamDist);
		}
		Collections.shuffle(orderedTeams, ThreadLocalRandom.current());
		Collections.sort(orderedTeams, (a, b) -> {

			int pointDifference = Integer.compare(a.getScore().getTotalPoints(), b.getScore().getTotalPoints());
			if (pointDifference != 0)
				return pointDifference;

			return -Double.compare(distancesToCenter.get(a), distancesToCenter.get(b));

		});

		Map<TeamState, ScoreState> scoreUpdates = new HashMap<>();
		for (TeamState team : orderedTeams)
			scoreUpdates.put(team, team.getScore());

		Iterator<TeamState> it = orderedTeams.iterator();
		TeamState current = it.next();
		boolean changed = false;
		while (it.hasNext()) {
			TeamState prev = current;
			current = it.next();
			while (scoreUpdates.get(current).getTotalPoints() <= scoreUpdates.get(prev).getTotalPoints()) {
				changed = true;
				scoreUpdates.compute(current, (k, v) -> v.addEvent(Event.TIE_BREAK));
			}
		}

		if (!changed)
			return field;

		Set<TeamState> newTeams = new LinkedHashSet<>();
		for (TeamState team : teams) {
			ScoreState score = scoreUpdates.get(team);
			newTeams.add(new TeamState(team.getSymbol(), team.getPlayerStates(), team.getTable(), score));
		}

		return new FieldState(field.getTurnNumber(), field.getMatchType(), newTeams, field.getFoodStates(),
				field.getCollisionStates());

	}

	private FieldState playAction(FieldState field, TeamState team, PlayerState player, Action action) {
		assert field != null;
		assert player != null;

		if (!PlayerUtils.isValidAction(field, player, action)) {
			EngineLogger.INSTANCE.log(Level.WARNING, "Player {0} is unable to play action {1}",
					new Object[] { player, action });
			action = null;
		}

		ScoreState score = team.getScore();
		Set<FoodState> food = new LinkedHashSet<>(field.getFoodStates());
		InventoryState inventory = player.getInventory();
		double energy = player.getEnergy();
		if (action != null && !action.equals(Action.DUCK) && !action.isEating()) {
			energy -= Player.ENERGY_DECREMENT;
			if (energy < 0.0)
				energy = 0.0;
		}

		Map<Character, TableState> modifiedTables = new HashMap<>();

		if (action != null)
			if (action.isPickingUp()) {

				Player.Hand hand = action.equals(Action.PICKUP_LEFT) ? Player.Hand.LEFT : Player.Hand.RIGHT;

				score = score.addEvent(Event.FIRST_PICKUP);

				FoodState piece = randomFoodPieceWithinReach(field, player, hand);
				if (piece == null) {
					List<TeamState> tableTeams = new ArrayList<>();
					List<Food.Type> types = new ArrayList<>();
					for (TeamState otherTeam : field.getTeamStates()) {
						TableState table = otherTeam.getTable();
						if (PlayerUtils.isAgainstTable(player, table, true)) {
							for (Food.Type type : table.getFood()) {
								tableTeams.add(otherTeam);
								types.add(type);
							}
						}
					}
					int randomIndex = ThreadLocalRandom.current().nextInt(tableTeams.size());
					TeamState tableTeam = tableTeams.get(randomIndex);
					TableState table = tableTeam.getTable();
					Food.Type toRemove = types.get(randomIndex);
					Set<Food.Type> newFood = EnumSet.noneOf(Food.Type.class);
					newFood.addAll(table.getFood());
					newFood.remove(toRemove);
					TableState newTable = new TableState(table.getLocation(), newFood);
					modifiedTables.put(tableTeam.getSymbol(), newTable);
					inventory = inventory.add(hand, toRemove);
				} else {
					food.remove(piece);
					inventory = inventory.add(hand, piece.getType());
				}

			} else if (action.isThrowing()) {

				score = score.addEvent(Event.FIRST_THROW);

				Player.Hand hand = action.equals(Action.THROW_LEFT) ? Player.Hand.LEFT : Player.Hand.RIGHT;
				Food.Type type = inventory.get(hand);
				inventory = inventory.remove(hand);

				Vector location = PlayerUtils.getArmLocation(player, hand);
				// TODO put random variable somewhere
				Direction heading = PlayerUtils.getArmDirection(player, hand);
				double height = Player.THROW_HEIGHT.getAsDouble();

				FoodState piece = new FoodState(type, location, height, heading);

				food.add(piece);

			} else if (action.isEating()) {

				Player.Hand hand = action.equals(Action.EAT_LEFT) ? Player.Hand.LEFT : Player.Hand.RIGHT;

				Food.Type type = inventory.get(hand);
				inventory = inventory.remove(hand);
				energy = Math.min(energy + type.getEnergy().getAsDouble(), Player.MAX_ENERGY);

				score = score.addEvent(Event.FIRST_EAT).addEvent(Event.EVERY_EAT);

			} else if (action.isMoving()) {
				score = score.addEvent(Event.FIRST_MOVE);
			}

		Vector location = calculateLocation(field, player, action);
		double height = calculateHeight(player, action);
		Direction heading = calculateHeading(player, action);

		player = new PlayerState(player.getSymbol(), location, height, heading, inventory, energy);
		Set<PlayerState> players = new LinkedHashSet<>();
		for (PlayerState teamPlayer : team.getPlayerStates())
			if (player.getSymbol() == teamPlayer.getSymbol())
				players.add(player);
			else
				players.add(teamPlayer);

		team = new TeamState(team.getSymbol(), players, team.getTable(), score);

		Set<TeamState> teams = new LinkedHashSet<>();
		for (TeamState fieldTeam : field.getTeamStates()) {
			TeamState teamToAdd;
			if (team.getSymbol() == fieldTeam.getSymbol())
				teamToAdd = team;
			else
				teamToAdd = fieldTeam;
			char symbol = teamToAdd.getSymbol();
			TableState changedTable = modifiedTables.get(symbol);
			if (changedTable == null)
				teams.add(teamToAdd);
			else
				teams.add(new TeamState(symbol, teamToAdd.getPlayerStates(), changedTable, teamToAdd.getScore()));
		}

		return new FieldState(field.getTurnNumber(), field.getMatchType(), teams, food, field.getCollisionStates());

	}

	private Vector calculateLocation(FieldState field, PlayerState player, Action action) {
		assert field != null;
		assert player != null;

		Vector location = player.getLocation();

		double multiplier = PlayerUtils.getMoveMultiplier(player.getEnergy());
		Vector velocity;
		if (action == null) {
			return location;
		} else if (action.equals(Action.MOVE_FORWARD)) {
			double speed = Player.FORWARD_MOVEMENT_SPEED.getAsDouble() * multiplier;
			velocity = Vector.polar(speed, player.getHeading());
		} else if (action.equals(Action.MOVE_BACKWARD)) {
			double speed = Player.REVERSE_MOVEMENT_SPEED.getAsDouble() * multiplier;
			velocity = Vector.polar(speed, player.getHeading().getOpposite());
		} else {
			return location;
		}

		return movePlayer(field, location, velocity);

	}

	private double calculateHeight(PlayerState player, Action action) {
		assert player != null;

		if (action != null && action.equals(Action.DUCK))
			return Math.max(player.getHeight() - Player.DUCK_SPEED.getAsDouble(), Player.MIN_HEIGHT);
		else
			return Player.HEIGHT;

	}

	private Direction calculateHeading(PlayerState player, Action action) {
		assert player != null;

		Direction heading = player.getHeading();

		double angularVelocity;
		if (action == null)
			return heading;
		else if (action.equals(Action.TURN_LEFT))
			angularVelocity = Player.TURN_SPEED.getAsDouble();
		else if (action.equals(Action.TURN_RIGHT))
			angularVelocity = -Player.TURN_SPEED.getAsDouble();
		else
			return heading;

		return heading.add(angularVelocity);

	}

	private FoodState randomFoodPieceWithinReach(FieldState field, PlayerState player, Player.Hand hand) {
		assert player != null;
		assert hand != null;

		List<FoodState> options = new LinkedList<>();

		for (FoodState food : field.getFoodStates())
			if (PlayerUtils.canPickup(player, food, hand))
				options.add(food);

		int size = options.size();
		if (size == 0)
			return null;

		return options.get(ThreadLocalRandom.current().nextInt(size));

	}

	private Vector movePlayer(FieldState field, Vector location, Vector displacement) {

		double originalX = location.getX();
		double originalY = location.getY();
		Vector result = location.add(displacement);

		for (TeamState team : field.getTeamStates()) {
			TableState table = team.getTable();

			if (!isInsideTable(result, table))
				continue;

			double northEdgeY = table.getEdge(Direction.NORTH);
			if (originalY - Player.COLLISION_RADIUS >= northEdgeY)
				if (result.getY() - Player.COLLISION_RADIUS < northEdgeY)
					result = hitWallY(location, displacement, northEdgeY);

			double southEdgeY = table.getEdge(Direction.SOUTH);
			if (originalY + Player.COLLISION_RADIUS <= southEdgeY)
				if (result.getY() + Player.COLLISION_RADIUS > southEdgeY)
					result = hitWallY(location, displacement, southEdgeY);

			double eastEdgeX = table.getEdge(Direction.EAST);
			if (originalX - Player.COLLISION_RADIUS >= eastEdgeX)
				if (result.getX() - Player.COLLISION_RADIUS < eastEdgeX)
					result = hitWallX(location, displacement, eastEdgeX);

			double westEdgeX = table.getEdge(Direction.WEST);
			if (originalX + Player.COLLISION_RADIUS <= westEdgeX)
				if (result.getX() + Player.COLLISION_RADIUS > westEdgeX)
					result = hitWallX(location, displacement, westEdgeX);

		}

		if (result.getX() < Player.COLLISION_RADIUS)
			result = hitWallX(location, displacement, 0.0);
		else if (result.getX() > Field.WIDTH - Player.COLLISION_RADIUS)
			result = hitWallX(location, displacement, Field.WIDTH);

		if (result.getY() < Player.COLLISION_RADIUS)
			result = hitWallY(location, displacement, 0.0);
		else if (result.getY() > Field.DEPTH - Player.COLLISION_RADIUS)
			result = hitWallY(location, displacement, Field.DEPTH);

		return result;

	}

	private boolean isInsideTable(Vector playerLocation, TableState table) {

		double playerX = playerLocation.getX();
		double playerY = playerLocation.getY();

		if (playerY - Player.COLLISION_RADIUS >= table.getEdge(Direction.NORTH))
			return false;
		if (playerY + Player.COLLISION_RADIUS <= table.getEdge(Direction.SOUTH))
			return false;
		if (playerX - Player.COLLISION_RADIUS >= table.getEdge(Direction.EAST))
			return false;
		if (playerX + Player.COLLISION_RADIUS <= table.getEdge(Direction.WEST))
			return false;
		return true;

	}

	private Vector hitWallX(Vector location, Vector displacement, double wallX) {

		double playerX = location.getX();
		double newX;
		if (playerX < wallX)
			newX = wallX - Player.COLLISION_RADIUS;
		else
			newX = wallX + Player.COLLISION_RADIUS;

		double proportionOfVector = (newX - playerX) / displacement.getX();
		double newY = location.getY() + displacement.getY() * proportionOfVector;
		return Vector.cartesian(newX, newY);

	}

	private Vector hitWallY(Vector location, Vector displacement, double wallY) {

		double playerY = location.getY();
		double newY;
		if (playerY < wallY)
			newY = wallY - Player.COLLISION_RADIUS;
		else
			newY = wallY + Player.COLLISION_RADIUS;

		double proportionOfVector = (newY - playerY) / displacement.getY();
		double newX = location.getX() + displacement.getX() * proportionOfVector;
		return Vector.cartesian(newX, newY);

	}

	private static <E> E randomElementFrom(Collection<E> collection) {
		assert collection != null;
		assert !collection.isEmpty();
		int index = ThreadLocalRandom.current().nextInt(collection.size());
		int i = 0;
		for (E element : collection)
			if (i++ == index)
				return element;
		throw new AssertionError();
	}

}
