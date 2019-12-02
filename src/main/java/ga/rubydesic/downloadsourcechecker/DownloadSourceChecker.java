package ga.rubydesic.downloadsourcechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadSourceChecker {

	private static final Map<Class<?>, String> cache = new HashMap<>();
	private static final Map<String, List<String>> sourceGroups = new HashMap<>();

	public static void registerSourceGroup(SourceGroup group) {
		registerSourceGroup(group.name, group.sources);
	}

	public static void registerSourceGroup(String name, List<String> sources) {
		sourceGroups.put(name, sources);
	}

	/**
	 * Returns the source group of the selected class
	 */
	public static CompletableFuture<Optional<String>> getSourceGroup(Class<?> clazz) {
		return getReferrer(clazz).thenApply(oRef -> oRef.map(referrer -> {
			for (Entry<String, List<String>> entry : sourceGroups.entrySet()) {
				if (entry.getValue().stream().anyMatch(referrer::endsWith)) {
					return entry.getKey();
				}
			}
			return null;
		}));
	}

	private static String getFilePathFromUri(String uri) {
		Pattern filePathPattern = Pattern.compile("jar:file:/(C:.*)!.*");
		Matcher matcher = filePathPattern.matcher(uri);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	public static CompletableFuture<Optional<String>> getReferrer(Class<?> clazz) {
		String cachedReferrer = cache.get(clazz);
		if (cachedReferrer != null)
			return CompletableFuture.completedFuture(Optional.of(cachedReferrer));

		return CompletableFuture.supplyAsync(() -> {
			try {
				String uri = clazz.getProtectionDomain()
						.getCodeSource().getLocation().toURI().toString();
				// Get path of the JAR file the class is running from
				String jarPath = getFilePathFromUri(uri);

				if (jarPath == null) return Optional.empty();

				// Get the ZoneID of the JAR file
				File zoneID = new File(jarPath + ":Zone.Identifier");

				if (!zoneID.exists()) return Optional.empty();

				// Read the ZoneID file
				BufferedReader reader = new BufferedReader(new FileReader(zoneID));
				Pattern pattern = Pattern.compile("ReferrerUrl=(.*)");
				String line, match = null;
				while ((line = reader.readLine()) != null && match == null) {
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) // We've found the referrer URL
						match = matcher.group(1);
				}

				if (match != null) {
					String host = new URI(match).getHost();
					cache.put(clazz, host);
					return Optional.of(host);
				} else {
					return Optional.empty();
				}
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		});
	}
}
