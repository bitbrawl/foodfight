package org.bitbrawl.foodfight.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.engine.match.CharFunction;
import org.bitbrawl.foodfight.engine.match.DefaultTurnRunner;
import org.bitbrawl.foodfight.engine.match.FieldGenerator;
import org.bitbrawl.foodfight.engine.match.JarController;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.field.MatchType;

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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

class Database {

	private final String url;
	private final Path repoFolder;
	private final Path dataFolder;
	private final String insertMatchQuery, insertVersionQuery, insertTeamQuery, insertPlayerQuery, insertPairwiseQuery;
	private final ServerConfig config;

	public Database(String url, Path repoFolder, Path dataFolder) throws IOException {

		this.url = url;
		this.repoFolder = repoFolder;
		this.dataFolder = dataFolder;

		insertMatchQuery = sqlFileToString("insert_match.sql");
		insertVersionQuery = sqlFileToString("insert_version.sql");
		insertTeamQuery = sqlFileToString("insert_team.sql");
		insertPlayerQuery = sqlFileToString("insert_player.sql");
		insertPairwiseQuery = sqlFileToString("insert_pairwise.sql");
		config = ServerConfig.getInstance(Paths.get("config.json"));

	}

	private String sqlFileToString(String sqlFilename) throws IOException {
		try (InputStream stream = getClass().getResourceAsStream("/" + sqlFilename);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);
				Stream<String> lines = reader.lines()) {
			return lines.collect(Collectors.joining(" "));
		}
	}

	public Iterable<Match> createBestMatches() throws SQLException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, MavenInvocationException, CommandLineException {

		Division division = randomElement(Division.values());

		int randomType = ThreadLocalRandom.current().nextInt(7);
		MatchType type;
		if (randomType < 3)
			type = MatchType.DUEL;
		else if (randomType < 6)
			type = MatchType.FREE_FOR_ALL;
		else
			type = MatchType.TEAM;

		List<Competitor> competitors = new ArrayList<>();

		Authentication dbAuth = config.getDatabase();
		try (Connection connection = DriverManager.getConnection(url, dbAuth.getUsername(), dbAuth.getPassword())) {

			String query = "SELECT id, username, repo FROM competitor WHERE division_id <= ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, division.getId());

				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						int id = result.getInt("id");
						String username = result.getString("username");
						competitors.add(new Competitor(id, username));
					}
				}

			}

		}

		competitors = randomSubset(competitors, type.getNumberOfPlayers());

		Map<Competitor, String> versionNames = new LinkedHashMap<>();
		for (Competitor competitor : competitors)
			versionNames.put(competitor, setupController(competitor.getUsername()));

		List<Set<Set<Competitor>>> matchGroupings;

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

		List<Match> matches = new LinkedList<>();

		try (Connection connection = DriverManager.getConnection(url, dbAuth.getUsername(), dbAuth.getPassword())) {

			for (Set<Set<Competitor>> matchGrouping : matchGroupings) {

				FieldState field = new FieldGenerator(type).get();
				Map<Character, Competitor> assignment = assignCompetitors(field, matchGrouping);
				Map<Character, Integer> versionIds = new HashMap<>();
				for (Entry<Character, Competitor> entry : assignment.entrySet()) {
					Competitor competitor = entry.getValue();
					int versionId = getVersionId(connection, competitor.getId(), versionNames.get(competitor));
					versionIds.put(entry.getKey(), versionId);
				}
				int matchId = addPlaceholderMatch(connection, field, versionIds::get);
				String matchName = getMatchName(matchId);
				Path matchFolder = dataFolder.resolve(matchName);
				Map<Character, Controller> controllers = new HashMap<>();
				for (Entry<Character, Competitor> entry : assignment.entrySet())
					controllers.put(entry.getKey(), createController(matchFolder, entry.getValue()));
				matches.add(new Match(matchId, field, controllers::get, new DefaultTurnRunner(), f -> {
				}));

			}

		}

		return matches;

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

		try (PreparedStatement statement = connection.prepareStatement(insertMatchQuery)) {
			statement.setInt(1, field.getMatchType().getNumberOfPlayers());

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

		int teamId;

		try (PreparedStatement statement = connection.prepareStatement(insertTeamQuery)) {
			statement.setInt(1, matchNumber);
			statement.setString(2, Character.toString(team.getSymbol()));
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

		try (PreparedStatement statement = connection.prepareStatement(insertPlayerQuery)) {
			statement.setInt(1, teamId);
			statement.setString(2, Character.toString(symbol));
			statement.setInt(3, versionId);
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}

	}

	private String setupController(String username) throws InvalidRemoteException, TransportException, GitAPIException,
			IOException, MavenInvocationException, CommandLineException {

		Path localRepo = repoFolder.resolve(username);
		Authentication gitAuth = config.getGit();
		String gitUsername = gitAuth.getUsername();
		String gitPassword = gitAuth.getPassword();
		CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(gitUsername, gitPassword);
		if (Files.notExists(localRepo)) {
			CloneCommand clone = Git.cloneRepository();
			String uri = "https://github.com/bitbrawl/foodfighter-" + username;
			clone.setURI(uri).setDirectory(localRepo.toFile());
			clone.setCredentialsProvider(credentials);
			clone.call().close();
		} else {
			PullResult pull;
			try (Git git = new Git(new RepositoryBuilder().setGitDir(localRepo.toFile()).build())) {
				pull = git.pull().setCredentialsProvider(credentials).call();
			}
			if (!pull.isSuccessful())
				throw new IOException("Unsuccessful pull");
		}

		InvocationRequest request = new DefaultInvocationRequest();
		request.setBaseDirectory(localRepo.toFile());
		request.setPomFile(localRepo.resolve("pom.xml").toFile());
		request.setGoals(Collections.singletonList("install"));
		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute(request);
		CommandLineException exception = result.getExecutionException();
		if (exception != null)
			throw exception;
		if (result.getExitCode() != 0)
			throw new IOException("Unsuccessful Maven install");

		Path configPath = localRepo.resolve("config.json");
		CompetitorConfig config = CompetitorConfig.getInstance(configPath);
		return config.getVersion().replaceAll("[^\\d\\.]", "");

	}

	private int getVersionId(Connection connection, int competitorId, String versionName) throws SQLException {

		try (PreparedStatement statement = connection.prepareStatement(insertVersionQuery)) {
			statement.setInt(1, competitorId);
			statement.setString(2, versionName);
			statement.setInt(3, competitorId);
			statement.setString(4, versionName);
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt("id");
			}
		}

	}

	private Controller createController(Path matchFolder, Competitor competitor) throws IOException {

		String username = competitor.getUsername();
		Path localRepo = repoFolder.resolve(competitor.getUsername());
		CompetitorConfig config = CompetitorConfig.getInstance(localRepo.resolve("config.json"));

		Path jar = localRepo.resolve("target").resolve(username + "-1.0.0.jar");
		Path log = Files.createFile(matchFolder.resolve(username + ".log"));
		return new JarController(jar, config.getMainClass(), log);

	}

	public boolean updateMatch(MatchHistory match) throws SQLException, IOException {

		int matchId = match.getMatchNumber();

		uploadToS3(matchId);

		FieldState finalState = match.getFinalState();

		boolean shouldGenerate = false;

		Authentication dbAuth = config.getDatabase();
		try (Connection connection = DriverManager.getConnection(url, dbAuth.getUsername(), dbAuth.getPassword())) {

			Map<TeamState, Integer> teamIds = new HashMap<>();
			for (TeamState team : finalState.getTeamStates())
				teamIds.put(team, updateTeam(connection, matchId, team));

			Map<TeamState, Set<Integer>> versionIds = new HashMap<>();
			for (TeamState team : finalState.getTeamStates())
				versionIds.put(team, selectVersionIds(connection, teamIds.get(team)));

			for (TeamState team : finalState.getTeamStates()) {
				int teamId = teamIds.get(team);
				for (TeamState otherTeam : finalState.getTeamStates()) {
					if (team.equals(otherTeam))
						continue;
					int otherTeamId = teamIds.get(otherTeam);
					Set<Integer> versionsA = selectVersionIds(connection, teamId);
					Set<Integer> versionsB = selectVersionIds(connection, otherTeamId);
					boolean didWin = team.getScore().getTotalPoints() > otherTeam.getScore().getTotalPoints();
					shouldGenerate |= addMatchResult(connection, match, versionsA, versionsB, didWin);
				}
			}

			String videoStatus = shouldGenerate ? "generating" : "done";
			String query = "UPDATE game_match SET is_finished = TRUE, video_status = ? WHERE id = ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, videoStatus);
				statement.setInt(2, matchId);
				statement.execute();
			}

		}

		return shouldGenerate;

	}

	private void uploadToS3(int matchId) throws IOException {

		Authentication awsAuth = config.getAws();
		AWSCredentials credentials = new BasicAWSCredentials(awsAuth.getUsername(), awsAuth.getPassword());
		AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).build();

		String matchName = getMatchName(matchId);
		Path folder = dataFolder.resolve(matchName);
		Path traceFile = folder.resolve("trace.json");
		String location = "foodfight/data";
		uploadFile(s3Client, traceFile, location);
		for (Path file : Files.newDirectoryStream(folder, "*.log"))
			uploadFile(s3Client, file, location);

	}

	private void uploadToYoutube(int matchId, Path videoFile) {

		// TODO upload to YouTube instead of S3

		Authentication awsAuth = config.getAws();
		AWSCredentials credentials = new BasicAWSCredentials(awsAuth.getUsername(), awsAuth.getPassword());
		AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).build();

		uploadFile(s3Client, videoFile, "foodfight/data");

	}

	private void uploadFile(AmazonS3 s3Client, Path file, String location) {
		s3Client.putObject("data.bitbrawl.org", location + "/" + file.getFileName(), file.toFile());
	}

	private boolean addMatchResult(Connection connection, MatchHistory match, Set<Integer> versionsA,
			Set<Integer> versionsB, boolean didWin) throws SQLException {

		int matchId = match.getMatchNumber();
		int typeId = match.getFinalState().getMatchType().getNumberOfPlayers();

		boolean shouldGenerate = false;

		for (int version1 : versionsA)
			for (int version2 : versionsB) {

				int pairwiseId;

				try (PreparedStatement statement = connection.prepareStatement(insertPairwiseQuery)) {
					statement.setInt(1, typeId);
					statement.setInt(2, version1);
					statement.setInt(3, version2);
					statement.setInt(4, typeId);
					statement.setInt(5, version1);
					statement.setInt(6, version2);
					try (ResultSet result = statement.executeQuery()) {
						result.next();
						pairwiseId = result.getInt("id");
					}
				}

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

	public void addVideo(int matchId, Path videoFile) throws SQLException {

		uploadToYoutube(matchId, videoFile);

		Authentication dbAuth = config.getDatabase();
		try (Connection connection = DriverManager.getConnection(url, dbAuth.getUsername(), dbAuth.getPassword())) {

			try (PreparedStatement statement = connection
					.prepareStatement("UPDATE game_match SET video_status = 'done' WHERE id = ?")) {
				statement.setInt(1, matchId);
				statement.execute();
			}

		}
	}

	private String getMatchName(int matchId) {
		return String.format("match-%06x", matchId);
	}

	private static final <E> E randomElement(E[] array) {
		return array[ThreadLocalRandom.current().nextInt(array.length)];
	}

	private static final <E> List<E> randomSubset(List<E> set, int size) {
		int setSize = set.size();
		for (int i = 0; i < size; i++)
			Collections.swap(set, i, ThreadLocalRandom.current().nextInt(i, setSize));
		return set.subList(0, size);
	}

	private static final BinomialTest test = new BinomialTest();
	private static final double ALPHA_LEVEL = 0.1;

}
