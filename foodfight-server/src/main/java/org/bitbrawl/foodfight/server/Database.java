package org.bitbrawl.foodfight.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.engine.match.CharFunction;
import org.bitbrawl.foodfight.engine.match.FieldGenerator;
import org.bitbrawl.foodfight.engine.match.JarController;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import org.apache.commons.math3.stat.inference.AlternativeHypothesis;
import org.apache.commons.math3.stat.inference.BinomialTest;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Database {

	private final String insertVersionQuery, insertPairwiseQuery, selectPairingResultsQuery, selectPairingsQuery,
			selectPairwiseQuery, selectResultsQuery, updateScoreQuery;
	private final ServerConfig config;

	public Database(ServerConfig config) throws IOException {

		insertVersionQuery = sqlFileToString("insert_version.sql");
		insertPairwiseQuery = sqlFileToString("insert_pairwise.sql");
		selectPairingResultsQuery = sqlFileToString("select_pairing_results.sql");
		selectPairingsQuery = sqlFileToString("select_pairings.sql");
		selectPairwiseQuery = sqlFileToString("select_pairwise.sql");
		selectResultsQuery = sqlFileToString("select_results.sql");
		updateScoreQuery = sqlFileToString("update_score.sql");
		this.config = config;

	}

	private String sqlFileToString(String sqlFilename) throws IOException {
		try (InputStream stream = getClass().getResourceAsStream("/" + sqlFilename);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);
				Stream<String> lines = reader.lines()) {
			return lines.collect(Collectors.joining(" "));
		}
	}

	public Iterable<MatchTemplate> createBestMatches() throws SQLException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, MavenInvocationException, CommandLineException {

		logger.setLevel(Level.ALL);
		logger.info("Creating next matches");

		Division division = randomElement(Division.values());

		int randomType = ThreadLocalRandom.current().nextInt(17);
		MatchType type;
		if (randomType < 12)
			type = MatchType.DUEL;
		else if (randomType < 16)
			type = MatchType.FREE_FOR_ALL;
		else
			type = MatchType.TEAM;

		logger.log(Level.INFO, "division: {0}; type: {1}", new Object[] { division, type });

		List<Competitor> competitors = new ArrayList<>();
		CompetitorPair selectedPair;
		Map<Integer, Competitor> pairCompetitors = new HashMap<>();

		try (Connection connection = connect()) {

			List<CompetitorPair> pairs = new LinkedList<>();

			try (PreparedStatement statement = connection.prepareStatement(selectPairingsQuery)) {
				statement.setInt(1, type.getNumberOfPlayers());
				statement.setInt(2, division.getId());
				statement.setInt(3, division.getId());
				statement.setInt(4, division.getId());
				statement.setInt(5, division.getId());
				statement.setInt(6, type.getNumberOfPlayers());
				statement.setInt(7, division.getId());
				statement.setInt(8, division.getId());
				statement.setInt(9, division.getId());
				statement.setInt(10, division.getId());
				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						pairs.add(new CompetitorPair(result.getInt("first_competitor"),
								result.getInt("second_competitor")));
					}
				}
			}

			if (pairs.isEmpty())
				return Collections.emptyList();

			selectedPair = randomElement(pairs);
			logger.log(Level.INFO, "selectedPair: {0}", selectedPair);
			String query = "SELECT id, username FROM competitor WHERE id = ? or id = ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, selectedPair.getFirstId());
				statement.setInt(2, selectedPair.getSecondId());
				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						int id = result.getInt("id");
						String username = result.getString("username");
						Competitor competitor = new Competitor(id, username);
						pairCompetitors.put(id, competitor);
						competitors.add(competitor);
					}
				}
			}

			if (!type.equals(MatchType.DUEL)) {

				List<Competitor> allCompetitors = new ArrayList<>();

				query = "SELECT id, username FROM competitor WHERE division_id <= ? AND id NOT IN (?, ?)";
				try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setInt(1, division.getId());
					statement.setInt(2, selectedPair.getFirstId());
					statement.setInt(3, selectedPair.getSecondId());

					try (ResultSet result = statement.executeQuery()) {
						while (result.next()) {
							int id = result.getInt("id");
							String username = result.getString("username");
							allCompetitors.add(new Competitor(id, username));
						}
					}

				}

				logger.log(Level.INFO, "allCompetitors: {0}", allCompetitors);

				List<Competitor> subset = randomSubset(allCompetitors, type.getNumberOfPlayers() - 2);
				logger.log(Level.INFO, "subset: {0}", subset);
				competitors.addAll(subset);
				Collections.shuffle(competitors);

			}

		}

		Map<Competitor, String> versionNames = new LinkedHashMap<>();
		for (Competitor competitor : competitors) {
			String versionName = setupController(competitor.getUsername());
			if (versionName.equals("0.0.0"))
				return Collections.emptyList();
			versionNames.put(competitor, versionName);
		}

		List<Set<Set<Competitor>>> matchGroupings;
		logger.log(Level.INFO, "competitors: {0}", competitors);

		if (type.equals(MatchType.TEAM)) {

			matchGroupings = new LinkedList<>();

			Set<Competitor> match1Team1 = new LinkedHashSet<>(Arrays.asList(competitors.get(0), competitors.get(1)));
			Set<Competitor> match1Team2 = new LinkedHashSet<>(Arrays.asList(competitors.get(2), competitors.get(3)));
			matchGroupings.add(new LinkedHashSet<>(Arrays.asList(match1Team1, match1Team2)));

			Set<Competitor> match2Team1 = new LinkedHashSet<>(Arrays.asList(competitors.get(0), competitors.get(2)));
			Set<Competitor> match2Team2 = new LinkedHashSet<>(Arrays.asList(competitors.get(1), competitors.get(3)));
			matchGroupings.add(new LinkedHashSet<>(Arrays.asList(match2Team1, match2Team2)));

			Set<Competitor> match3Team1 = new LinkedHashSet<>(Arrays.asList(competitors.get(0), competitors.get(3)));
			Set<Competitor> match3Team2 = new LinkedHashSet<>(Arrays.asList(competitors.get(1), competitors.get(2)));
			matchGroupings.add(new LinkedHashSet<>(Arrays.asList(match3Team1, match3Team2)));

		} else {

			Set<Set<Competitor>> teams = new LinkedHashSet<>();
			for (Competitor competitor : competitors)
				teams.add(Collections.singleton(competitor));
			matchGroupings = Collections.singletonList(teams);

		}

		Map<Competitor, Integer> versionIds = new HashMap<>();
		List<MatchTemplate> templates = new LinkedList<>();

		try (Connection connection = connect()) {

			for (Competitor competitor : competitors)
				versionIds.put(competitor, getVersionId(connection, competitor.getId(), versionNames.get(competitor)));

			int firstVersionId = versionIds.get(pairCompetitors.get(selectedPair.getFirstId()));
			int secondVersionId = versionIds.get(pairCompetitors.get(selectedPair.getSecondId()));
			int pairingId = selectPairwise(connection, type, firstVersionId, secondVersionId);
			String update = "UPDATE pairwise_result SET focus_count = focus_count + 1 WHERE id = ?";
			try (PreparedStatement statement = connection.prepareStatement(update)) {
				statement.setInt(1, pairingId);
				statement.executeUpdate();
			}

			for (Set<Set<Competitor>> matchGrouping : matchGroupings) {

				FieldState field = new FieldGenerator(type).get();
				Map<Character, Competitor> assignment = assignCompetitors(field, matchGrouping);
				logger.log(Level.INFO, "versionIds: {0}", versionIds);
				int matchId = addPlaceholderMatch(connection, field, c -> versionIds.get(assignment.get(c)));
				MatchTemplate template = new MatchTemplate(matchId, field, assignment::get);
				templates.add(template);

			}

		}

		return templates;

	}

	private Map<Character, Competitor> assignCompetitors(FieldState field, Set<Set<Competitor>> competitors) {
		Map<Character, Competitor> result = new LinkedHashMap<>();
		Iterator<TeamState> teamIt = field.getTeamStates().iterator();
		for (Set<Competitor> teamCompetitors : competitors) {
			TeamState team = teamIt.next();
			Iterator<PlayerState> playerIt = team.getPlayerStates().iterator();
			for (Competitor competitor : teamCompetitors)
				result.put(playerIt.next().getSymbol(), competitor);
		}
		return result;
	}

	private int addPlaceholderMatch(Connection connection, FieldState field, CharFunction<Integer> versionIds)
			throws SQLException {

		int matchId;

		String update = "INSERT INTO game_match (type_id, is_finished) VALUES (?, false)";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, field.getMatchType().getNumberOfPlayers());
			statement.executeUpdate();
		}

		try (PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID()")) {
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				matchId = result.getInt(1);
			}
		}

		for (TeamState team : field.getTeamStates())
			addPlaceholderTeam(connection, matchId, team, versionIds);

		return matchId;

	}

	private void addPlaceholderTeam(Connection connection, int matchNumber, TeamState team,
			CharFunction<Integer> versionIds) throws SQLException {

		String update = "INSERT INTO team (match_id, symbol) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, matchNumber);
			statement.setString(2, Character.toString(team.getSymbol()));
			statement.executeUpdate();
		}

		int teamId;

		try (PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID()")) {
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				teamId = result.getInt(1);
			}
		}

		for (PlayerState player : team.getPlayerStates()) {
			char symbol = player.getSymbol();
			addPlaceholderPlayer(connection, teamId, symbol, versionIds.apply(symbol));
		}

	}

	private int addPlaceholderPlayer(Connection connection, int teamId, char symbol, int versionId)
			throws SQLException {

		String update = "INSERT INTO player (team_id, symbol, version_id) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, teamId);
			statement.setString(2, Character.toString(symbol));
			statement.setInt(3, versionId);
			statement.executeUpdate();
		}

		try (PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID()")) {
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}

	}

	private String setupController(String username) throws InvalidRemoteException, TransportException, GitAPIException,
			IOException, MavenInvocationException, CommandLineException {

		Path localRepo = config.getRepoFolder().resolve(username);
		Authentication gitAuth = config.getGit();
		String gitUsername = gitAuth.getUsername();
		String gitPassword = gitAuth.getPassword();
		CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(gitUsername, gitPassword);
		if (Files.notExists(localRepo)) {
			logger.log(Level.INFO, "Cloning repo: {0}", username);
			CloneCommand clone = Git.cloneRepository();
			String uri = "https://github.com/bitbrawl/foodfighter-" + username;
			clone.setURI(uri).setDirectory(localRepo.toFile());
			clone.setCredentialsProvider(credentials);
			clone.call().close();
		} else {
			logger.log(Level.INFO, "Pulling repo: {0}", username);
			PullResult pull;
			try (Git git = new Git(
					new RepositoryBuilder().setWorkTree(localRepo.toFile()).setMustExist(true).setup().build())) {
				pull = git.pull().setCredentialsProvider(credentials).setRemoteBranchName("master").call();
			}
			if (!pull.isSuccessful())
				throw new IOException("Unsuccessful pull");
		}

		logger.log(Level.INFO, "Building project: {0}", username);
		InvocationRequest request = new DefaultInvocationRequest();
		request.setBaseDirectory(localRepo.toFile());
		request.setPomFile(localRepo.resolve("pom.xml").toFile());
		request.setGoals(Collections.singletonList("install"));
		Invoker invoker = new DefaultInvoker();
		invoker.setLogger(ServerInvokerLogger.INSTANCE);
		invoker.setMavenHome(config.getMavenFolder().toFile());
		InvocationResult result = invoker.execute(request);
		CommandLineException exception = result.getExecutionException();
		if (exception != null)
			throw exception;
		if (result.getExitCode() != 0)
			throw new IOException("Unsuccessful Maven install");

		Path configPath = localRepo.resolve("src").resolve("main").resolve("resources").resolve("config.json");
		CompetitorConfig config = CompetitorConfig.getInstance(configPath);
		return config.getVersion().replaceAll("[^\\d\\.]", "");

	}

	private int getVersionId(Connection connection, int competitorId, String versionName) throws SQLException {

		int versionId = -1;

		String query = "SELECT id FROM competitor_version WHERE competitor_id = ? AND version_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, competitorId);
			statement.setString(2, versionName);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next())
					versionId = result.getInt("id");
			}
		}

		if (versionId < 0) {

			try (PreparedStatement statement = connection.prepareStatement(insertVersionQuery)) {
				statement.setInt(1, competitorId);
				statement.setString(2, versionName);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, competitorId);
				statement.setString(2, versionName);
				try (ResultSet result = statement.executeQuery()) {
					result.next();
					versionId = result.getInt("id");
				}
			}

		}

		String update = "UPDATE competitor SET version_id = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, versionId);
			statement.setInt(2, competitorId);
			statement.executeUpdate();
		}

		return versionId;

	}

	public JarController createController(Path matchFolder, Competitor competitor) throws IOException {

		String username = competitor.getUsername();
		Path localRepo = config.getRepoFolder().resolve(competitor.getUsername());
		Path configPath = localRepo.resolve("src").resolve("main").resolve("resources").resolve("config.json");
		CompetitorConfig competitorConfig = CompetitorConfig.getInstance(configPath);

		Path jar = localRepo.resolve("target").resolve("foodfighter-" + username + "-1.0.0.jar");
		Path log = matchFolder.resolve(username + ".log");
		return new JarController(jar, competitorConfig.getMainClass(), log);

	}

	public Path getMatchFolder(int matchNumber) {
		return config.getDataFolder().resolve(Match.getMatchName(matchNumber));
	}

	public boolean updateMatch(MatchHistory match) throws SQLException, IOException {

		int matchId = match.getMatchNumber();
		String matchName = Match.getMatchName(matchId);
		logger.log(Level.INFO, "Updating {0}", matchName);

		Path traceFile = getMatchFolder(matchId).resolve("trace.json");
		writeTraceFile(match, traceFile);

		uploadToS3(matchId);

		FieldState finalState = match.getFinalState();

		boolean shouldGenerate = false;

		logger.info("Updating database");
		try (Connection connection = connect()) {

			Map<TeamState, Integer> teamIds = new HashMap<>();
			for (TeamState team : finalState.getTeamStates())
				teamIds.put(team, updateTeam(connection, matchId, team));

			Map<TeamState, Set<Integer>> versionIds = new HashMap<>();
			for (TeamState team : finalState.getTeamStates())
				versionIds.put(team, selectVersionIds(connection, teamIds.get(team)));

			for (TeamState team : finalState.getTeamStates()) {
				for (TeamState otherTeam : finalState.getTeamStates()) {
					if (team.equals(otherTeam))
						continue;
					Set<Integer> versionsA = versionIds.get(team);
					Set<Integer> versionsB = versionIds.get(otherTeam);
					boolean didWin = team.getScore().getTotalPoints() > otherTeam.getScore().getTotalPoints();
					shouldGenerate |= addMatchResult(connection, match, versionsA, versionsB, didWin);
				}
			}

			List<Integer> competitorIds = new LinkedList<>();
			for (Set<Integer> teamVersionIds : versionIds.values()) {
				for (int versionId : teamVersionIds) {
					String query = "SELECT id FROM competitor WHERE version_id = ?";
					try (PreparedStatement statement = connection.prepareStatement(query)) {
						statement.setInt(1, versionId);
						try (ResultSet result = statement.executeQuery()) {
							result.next();
							competitorIds.add(result.getInt("id"));
						}
					}
				}
			}

			MatchType type = finalState.getMatchType();
			for (int id : competitorIds)
				updateScore(connection, id, type);

			String videoStatus = shouldGenerate ? "generating" : "none";
			String update = "UPDATE game_match SET is_finished = TRUE, video_status = ? WHERE id = ?";
			try (PreparedStatement statement = connection.prepareStatement(update)) {
				statement.setString(1, videoStatus);
				statement.setInt(2, matchId);
				statement.executeUpdate();
			}

		}

		return shouldGenerate;

	}

	private void writeTraceFile(MatchHistory history, Path traceFile) {
		try (Writer writer = Files.newBufferedWriter(traceFile, StandardOpenOption.CREATE_NEW)) {
			gson.toJson(history, writer);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to create trace file", e);
		}
	}

	private void uploadToS3(int matchId) throws IOException {

		String matchName = Match.getMatchName(matchId);
		logger.log(Level.INFO, "Uploading to S3", matchName);

		Path folder = config.getDataFolder().resolve(matchName);
		Path traceFile = folder.resolve("trace.json");
		Path zipPath = config.getDataFolder().resolve(matchName + ".zip");
		URI uri = URI.create("jar:" + zipPath.toUri().toString());
		Map<String, String> env = Collections.singletonMap("create", "true");
		try (FileSystem zipSystem = FileSystems.newFileSystem(uri, env)) {
			Path jarTrace = zipSystem.getPath("trace.json");
			Files.copy(traceFile, jarTrace);
			for (Path file : Files.newDirectoryStream(folder, "*.log"))
				Files.copy(file, zipSystem.getPath(file.getFileName().toString()));
		}
		String location = "foodfight/data/" + matchName + ".zip";
		uploadFile(zipPath, location);
		Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw e;
				}
			}
		});
		Files.delete(zipPath);

	}

	private void uploadVideo(int matchId, Path videoFile) throws IOException {

		String matchName = Match.getMatchName(matchId);
		String location = "foodfight/data/" + matchName + ".mp4";
		logger.log(Level.INFO, "Uploading video", matchName);

		// TODO upload to YouTube instead of S3

		uploadFile(videoFile, location);

		Files.delete(videoFile);

	}

	private void uploadFile(Path file, String location) {
		Authentication awsAuth = config.getAws();
		AWSCredentials credentials = new BasicAWSCredentials(awsAuth.getUsername(), awsAuth.getPassword());
		AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(Regions.US_EAST_2)
				.build();
		PutObjectRequest request = new PutObjectRequest("data.bitbrawl.org", location, file.toFile())
				.withCannedAcl(CannedAccessControlList.PublicRead);
		s3Client.putObject(request);
	}

	private boolean addMatchResult(Connection connection, MatchHistory match, Set<Integer> versionsA,
			Set<Integer> versionsB, boolean didWin) throws SQLException {

		int matchId = match.getMatchNumber();

		boolean shouldGenerate = false;

		for (int version1 : versionsA)
			for (int version2 : versionsB) {

				int pairwiseId = selectPairwise(connection, match.getFinalState().getMatchType(), version1, version2);

				String sql = "INSERT INTO match_result (match_id, pairwise_result_id, did_win) VALUES (?, ?, ?)";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setInt(1, matchId);
					statement.setInt(2, pairwiseId);
					statement.setBoolean(3, didWin);
					statement.execute();
				}

				int totalMatches;
				String query = "SELECT COUNT(*) FROM match_result WHERE pairwise_result_id = ?";
				try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setInt(1, pairwiseId);
					try (ResultSet result = statement.executeQuery()) {
						result.next();
						totalMatches = result.getInt(1);
					}
				}

				int wins;
				query = "SELECT COUNT(*) FROM match_result WHERE pairwise_result_id = ? AND did_win = TRUE";
				try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setInt(1, pairwiseId);
					try (ResultSet result = statement.executeQuery()) {
						result.next();
						wins = result.getInt(1);
					}
				}

				double pValue = test.binomialTest(totalMatches, wins, 0.5, AlternativeHypothesis.GREATER_THAN);
				double oppositePValue = test.binomialTest(totalMatches, wins, 0.5, AlternativeHypothesis.LESS_THAN);
				String winOrLoss;
				if (pValue < ALPHA_LEVEL)
					winOrLoss = "win";
				else if (oppositePValue < ALPHA_LEVEL)
					winOrLoss = "loss";
				else
					winOrLoss = "tie";

				try (PreparedStatement statement = connection.prepareStatement(
						"UPDATE pairwise_result SET total_matches = ?, wins = ?, result = ? WHERE id = ?")) {
					statement.setInt(1, totalMatches);
					statement.setInt(2, wins);
					statement.setString(3, winOrLoss);
					statement.setInt(4, pairwiseId);
					statement.execute();
				}

				shouldGenerate |= (didWin && wins == 1);

			}

		return shouldGenerate;

	}

	private int selectPairwise(Connection connection, MatchType type, int version1, int version2) throws SQLException {

		int pairwiseId = -1;

		try (PreparedStatement statement = connection.prepareStatement(selectPairwiseQuery)) {
			statement.setInt(1, type.getNumberOfPlayers());
			statement.setInt(2, version1);
			statement.setInt(3, version2);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next())
					pairwiseId = result.getInt("id");
			}
		}

		if (pairwiseId < 0) {

			try (PreparedStatement statement = connection.prepareStatement(insertPairwiseQuery)) {
				statement.setInt(1, type.getNumberOfPlayers());
				statement.setInt(2, version1);
				statement.setInt(3, version2);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = connection.prepareStatement(selectPairwiseQuery)) {
				statement.setInt(1, type.getNumberOfPlayers());
				statement.setInt(2, version1);
				statement.setInt(3, version2);
				try (ResultSet result = statement.executeQuery()) {
					result.next();
					return result.getInt("id");
				}
			}

		} else {
			return pairwiseId;
		}

	}

	private int updateTeam(Connection connection, int matchId, TeamState team) throws SQLException {

		int teamId;

		String query = "SELECT id FROM team WHERE match_id = ? AND symbol = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, matchId);
			statement.setString(2, Character.toString(team.getSymbol()));
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				teamId = result.getInt("id");
			}
		}

		try (PreparedStatement statement = connection.prepareStatement("UPDATE team SET points = ? WHERE id = ?")) {
			statement.setInt(1, team.getScore().getTotalPoints());
			statement.setInt(2, teamId);
			statement.execute();
		}

		return teamId;

	}

	private Set<Integer> selectVersionIds(Connection connection, int teamId) throws SQLException {

		Set<Integer> versionIds = new LinkedHashSet<>();

		String query = "SELECT version_id FROM player WHERE team_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, teamId);
			try (ResultSet result = statement.executeQuery()) {
				while (result.next())
					versionIds.add(result.getInt("version_id"));
			}
		}

		return versionIds;

	}

	private void updateScore(Connection connection, int competitorId, MatchType type) throws SQLException {

		int competitorDivisionId;
		String query = "SELECT division_id FROM competitor WHERE competitor.id = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, competitorId);
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				competitorDivisionId = result.getInt("division_id");
			}
		}

		int typeId = type.getNumberOfPlayers();

		Map<Division, CompetitorScore> scores = new EnumMap<>(Division.class);

		try (PreparedStatement statement = connection.prepareStatement(selectResultsQuery)) {
			statement.setInt(1, competitorId);
			statement.setInt(2, typeId);

			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					Division division = Division.byId(result.getInt("division_id"));
					CompetitorScore score = scores.computeIfAbsent(division, CompetitorScore::new);
					score.addResult(MatchResult.byName(result.getString("result")), result.getInt(3));
				}
			}

		}

		for (CompetitorScore score : scores.values()) {
			try (PreparedStatement statement = connection.prepareStatement(updateScoreQuery)) {
				statement.setInt(1, score.getDivision().getId());
				statement.setInt(2, typeId);
				statement.setInt(3, competitorId);
				statement.setInt(4, score.getCount(MatchResult.WIN));
				statement.setInt(5, score.getCount(MatchResult.TIE));
				statement.setInt(6, score.getCount(MatchResult.LOSS));
				statement.setInt(7, score.getScore());
				statement.executeUpdate();
			}
		}

		for (int competingDivisionId = competitorDivisionId; competingDivisionId <= 3; competingDivisionId++) {

			Map<Integer, Map<MatchResult, Integer>> resultCounts = new HashMap<>();

			try (PreparedStatement statement = connection.prepareStatement(selectPairingResultsQuery)) {
				statement.setInt(1, competitorId);
				statement.setInt(2, competingDivisionId);

				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						resultCounts.computeIfAbsent(result.getInt("second_competitor_id"), i -> {
							return new EnumMap<>(MatchResult.class);
						}).put(MatchResult.byName(result.getString("result")), result.getInt(3));
					}
				}

			}

			CompetitorScore score = new CompetitorScore(Division.byId(competingDivisionId));
			for (Map<MatchResult, Integer> resultCount : resultCounts.values()) {
				int wins = resultCount.getOrDefault(MatchResult.WIN, 0);
				int winsMinusLosses = wins - resultCount.getOrDefault(MatchResult.LOSS, 0);
				if (winsMinusLosses > 0)
					score.addResult(MatchResult.WIN, 1);
				else if (winsMinusLosses < 0)
					score.addResult(MatchResult.LOSS, 1);
				else
					score.addResult(MatchResult.TIE, 1);
			}

			try (PreparedStatement statement = connection.prepareStatement(updateScoreQuery)) {
				statement.setInt(1, competingDivisionId);
				statement.setInt(2, 0);
				statement.setInt(3, competitorId);
				statement.setInt(4, score.getCount(MatchResult.WIN));
				statement.setInt(5, score.getCount(MatchResult.TIE));
				statement.setInt(6, score.getCount(MatchResult.LOSS));
				statement.setInt(7, score.getScore());
				statement.execute();
			}
		}

	}

	public void addVideo(int matchId) throws IOException, SQLException {

		Path videoFile = config.getDataFolder().resolve(Match.getMatchName(matchId) + ".mp4");

		uploadVideo(matchId, videoFile);

		try (Connection connection = connect()) {

			String update = "UPDATE game_match SET video_status = 'done' WHERE id = ?";
			try (PreparedStatement statement = connection.prepareStatement(update)) {
				statement.setInt(1, matchId);
				statement.executeUpdate();
			}

		}
	}

	private Connection connect() throws SQLException {
		Authentication dbAuth = config.getDatabase();
		return DriverManager.getConnection(config.getDatabaseUrl(), dbAuth.getUsername(), dbAuth.getPassword());
	}

	private static final <E> E randomElement(E[] array) {
		return array[ThreadLocalRandom.current().nextInt(array.length)];
	}

	private static final <E> E randomElement(List<E> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	private static final <E> List<E> randomSubset(List<E> set, int size) {
		int setSize = set.size();
		for (int i = 0; i < size; i++)
			Collections.swap(set, i, ThreadLocalRandom.current().nextInt(i, setSize));
		return set.subList(0, size);
	}

	private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
			.registerTypeAdapter(ScoreState.class, ScoreState.Serializer.INSTANCE)
			.registerTypeAdapter(InventoryState.class, InventoryState.Serializer.INSTANCE)
			.registerTypeAdapter(Vector.class, Vector.Serializer.INSTANCE)
			.registerTypeAdapter(Direction.class, Direction.Serializer.INSTANCE).setPrettyPrinting().create();
	private static final Logger logger = EngineLogger.INSTANCE;
	private static final BinomialTest test = new BinomialTest();
	private static final double ALPHA_LEVEL = 0.1;

}
