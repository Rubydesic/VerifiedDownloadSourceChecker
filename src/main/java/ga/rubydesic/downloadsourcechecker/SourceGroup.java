package ga.rubydesic.downloadsourcechecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SourceGroup {

	public final String name;
	public final List<String> sources;

	public SourceGroup(String name, List<String> sources) {
		this.name = name;
		this.sources = Collections.unmodifiableList(sources);
	}

	public static Builder builder(String name) {
		return new Builder(name);
	}

	public static class Builder {

		String name;
		List<String> sources = new LinkedList<>();

		Builder(String name) {
			this.name = name;
		}

		public Builder add(String source) {
			sources.add(source);
			return this;
		}

		public SourceGroup build() {
			return new SourceGroup(name, new LinkedList<>(sources));
		}

	}

}
