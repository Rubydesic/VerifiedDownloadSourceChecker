package ga.rubydesic.downloadsourcechecker.defaults;

import ga.rubydesic.downloadsourcechecker.SourceGroup;

public class DefaultSourceGroups {

	public static final SourceGroup STOP_MOD_REPOSTERS =
			SourceGroup.builder("stop_mod_reposts")
					.add("9minecraft.com")
					.build();

}
