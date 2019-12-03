# Rubydesic's Download Source Checker

This is a simple library designed to allow you to check the source referrer a JAR file was downloaded from. Designed with Minecraft mods in mind but generally useable anywhere.

## Installation

Add this to your dependencies in build.gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.Rubydesic:VerifiedDownloadSourceChecker:master-SNAPSHOT'
}
```

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

## How does it work?

It's very simple and unintrusive. The library figures out the JAR file location (if applicable) and then reads the `Zone.Info` ADS file to find out if a referrer is known. In the future, this method will be extended to parse xattr data as well for MacOS/Linux clients. If the referrer for the download of the JAR file is unable to be found, then the `Optional` will be returned empty. 
