package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.engine.match.CharFunction;
import org.bitbrawl.foodfight.engine.match.DefaultTurnRunner;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.engine.video.ImageEncoder;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public enum ServerEngine {
	INSTANCE;

	public void runMatches() throws InterruptedException {

		ServerConfig config;
		Database database;
		try {
			config = ServerConfig.getInstance(Paths.get("config.json"));
			database = new Database(config);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to create config and database", e);
			return;
		}

		while (true) {

			try {

				for (MatchTemplate template : database.createBestMatches()) {

					int matchId = template.getMatchId();
					String matchName = Match.getMatchName(matchId);
					FieldState field = template.getField();
					Path matchFolder = config.getDataFolder().resolve(matchName);
					Files.createDirectories(matchFolder);
					CharFunction<Competitor> competitors = template.getCompetitors();
					Map<Character, Controller> controllers = new HashMap<>();
					for (PlayerState player : field.getPlayerStates()) {
						char symbol = player.getSymbol();
						controllers.put(symbol, database.createController(matchFolder, competitors.apply(symbol)));
					}
					Match match = new Match.Builder(matchId, field, controllers::get, new DefaultTurnRunner()).build();
					logger.log(Level.INFO, "Running {0}", matchName);
					MatchHistory history = match.run();

					if (database.updateMatch(history)) {
						logger.info("Generating video");
						CharFunction<String> names = c -> competitors.apply(c).getUsername();
						ImageEncoder.encode(history, names, matchFolder.resolve("video.mp4"));
						database.addVideo(matchId);
					}

					Thread.sleep(1000);

				}

			} catch (IOException | TransportException e) {
				logger.log(Level.SEVERE, "I/O problem", e);
			} catch (GitAPIException e) {
				logger.log(Level.SEVERE, "Git problem", e);
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "SQL problem", e);
			} catch (MavenInvocationException | CommandLineException e) {
				logger.log(Level.SEVERE, "Maven problem", e);
			} finally {
				Thread.sleep(1000);
			}

		}

	}

	public static void main(String[] args) throws InvalidRemoteException, TransportException, SQLException, IOException,
			GitAPIException, MavenInvocationException, CommandLineException, InterruptedException {
		Thread.setDefaultUncaughtExceptionHandler(
				(t, e) -> EngineLogger.INSTANCE.log(Level.SEVERE, "Problem running server engine", e));
		INSTANCE.runMatches();
	}

	private static final Logger logger = EngineLogger.INSTANCE;

}
