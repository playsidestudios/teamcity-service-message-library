/**
 * @type {import('semantic-release').GlobalConfig}
 */
module.exports = {
  branches: ["trunk"],
  plugins: [
    "@semantic-release/commit-analyzer",
    [
      "@semantic-release/release-notes-generator",
      { preset: "conventionalcommits" },
    ],
    [
      "@semantic-release/changelog",
      {
        changelogFile: "CHANGELOG.md",
      },
    ],
    "gradle-semantic-release-plugin",
    [
      "@semantic-release/git",
      {
        assets: ["gradle.properties", "CHANGELOG.md"],
      },
    ],
  ],
  repositoryUrl:
    "https://github.com/playsidestudios/teamcity-service-message-library.git",
};
