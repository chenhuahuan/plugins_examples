load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "example-restApiUiActionProject",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: example-restApiUiActionProject",
        "Gerrit-Module: com.googlesource.gerrit.plugins.examples.restapiuiactionproject.Module",
        "Implementation-Title: Example Rest Api Ui Action Project",
    ],
    resources = glob(["src/main/resources/**/*"]),
)
