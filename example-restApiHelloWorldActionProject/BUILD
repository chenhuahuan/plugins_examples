load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "example-restApiHelloWorldActionProject",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: example-restApiHelloWorldActionProject",
        "Gerrit-Module: com.googlesource.gerrit.plugins.examples.restapiHelloWorldActionProject.Module",
        "Implementation-Title: Example Rest Api Post Project",
    ],
    resources = glob(["src/main/resources/**/*"]),
)
