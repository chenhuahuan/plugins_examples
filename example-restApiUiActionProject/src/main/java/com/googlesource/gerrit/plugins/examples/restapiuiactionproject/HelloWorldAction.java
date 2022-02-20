package com.googlesource.gerrit.plugins.examples.restapiuiactionproject;

import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.webui.UiAction;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.project.ProjectResource;
import com.google.inject.Inject;
import com.google.inject.Provider;

@RequiresCapability("printHello")
class HelloWorldAction implements UiAction<RevisionResource>,
        RestModifyView<RevisionResource, HelloWorldAction.Input> {
  static class Input {
    boolean french;
    String message;
  }

  private Provider<CurrentUser> user;

  @Inject
  HelloWorldAction(Provider<CurrentUser> user) {
    this.user = user;
  }

  @Override
  public String apply(RevisionResource rev, Input input) {
    final String greeting = input.french
            ? "Bonjour"
            : "Hello";
    return String.format("%s %s from change %s, patch set %d!",
            greeting,
            Strings.isNullOrEmpty(input.message)
                    ? Objects.firstNonNull(user.get().getUserName(), "world")
                    : input.message,
            rev.getChange().getId().toString(),
            rev.getPatchSet().getPatchSetId());
  }

  @Override
  public Description getDescription(
          RevisionResource resource) {
    return new Description()
            .setLabel("Say hello")
            .setTitle("Say hello in different languages");
  }
}