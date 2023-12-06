/**
 * @type {import('semantic-release').GlobalConfig}
 */
module.exports = {
    branches: ["trunk"],
    plugins: ["gradle-semantic-release-plugin",
        [
            "@semantic-release/git",
            {
                "assets": [
                    "gradle.properties"
                ]
            }
        ]
        , '@semantic-release/commit-analyzer', '@semantic-release/release-notes-generator'],
    repositoryUrl: "https://github.com/playsidestudios/teamcity-service-message-library.git"
};