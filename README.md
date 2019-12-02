# Rubydesic's Download Source Checker

This is a simple library designed to allow you to check the source referrer a JAR file was downloaded from. Designed with Minecraft mods in mind but generally useable anywhere.

## Example Usage

Get the referrer group:

```java
DownloadSourceChecker checker = new DownloadSourceChecker();

SourceGroup officialSources = SourceGroup.builder("official")
    .add("mywebsite.com")
    .add("curseforge.com");

checker.registerSourceGroup(officialSources);
checker.registerSourceGroup(DefaultSourceGroups.STOP_MOD_REPOSTS);

Optional<String> group = checker.getSourceGroup(MyMod.class).get();
group.ifPresent(group -> {
    if (group.equals("stop_mod_reposts")) {
        System.out.println("You've downloaded this from a reposting website!");
    } else if (group.equals("official")) {
        System.out.println("Thanks for downloading this from the official website!")
    }
})
```

Get the referrer directly:

```java
CompletableFuture<Optional<String>> fReferrer =
    DownloadSourceChecker.getReferrer(MyMod.class);

String referrer = fReferrer.get().orElse(null);
```

