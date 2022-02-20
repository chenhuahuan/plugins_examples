// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.examples.restapipostrevision;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.gerrit.entities.Change;
import com.google.gerrit.entities.PatchSet;
import com.google.gerrit.entities.Project;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.webui.UiAction;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gerrit.server.notedb.ChangeNotes;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.google.gerrit.server.update.BatchUpdate;
import com.google.gerrit.server.project.ProjectState;
import com.google.gerrit.entities.Project;
import com.google.inject.assistedinject.Assisted;
import com.google.gerrit.server.update.ChangeContext;
import com.google.gerrit.server.util.time.TimeUtil;
import com.google.gerrit.server.notedb.ChangeNotes;

class HelloRevisionAction
    implements UiAction<RevisionResource>,
        RestModifyView<RevisionResource, HelloRevisionAction.Input> {

  private final BatchUpdate.Factory batchUpdateFactory;
  private final ProjectState projectState;
  private final Project project;
  private final Provider<CurrentUser> userProvider;
  private ChangeNotes.Factory notesFactory;

  static class Input {
    boolean french;
    String message;
  }

  @Inject
  HelloRevisionAction(Provider<CurrentUser> user,
                      BatchUpdate.Factory batchUpdateFactory,
                      @Assisted ProjectState projectState
  ) {
    this.userProvider = user;

    this.batchUpdateFactory = batchUpdateFactory;
    this.projectState = projectState;
    project = projectState.getProject();
  }

  @Override
  public Response<String> apply(RevisionResource rev, Input input) {
    final String greeting = input.french ? "Bonjour" : "Hello";


    PatchSet.Id psId = ChangeNotes.ChangeNotes().getChange().currentPatchSetId();
    ChangeNotes notes = notesFactory.create(project.getNameKey(), psId.changeId());
    Change change = notes.getChange();
    try (BatchUpdate bu = batchUpdateFactory.create(
            project.getNameKey(), userProvider.get(), TimeUtil.nowTs())) {
      bu.addOp(change.getId(), new BatchUpdate.Op() {
        @Override
        public boolean updateChange(ChangeContext ctx) {
          return true;
        }
      });
      bu.execute();
    }
    return Response.ok(
        String.format(
            "%s %s from change %s, patch set %d!",
            greeting,
            Strings.isNullOrEmpty(input.message)
                ? MoreObjects.firstNonNull(user.get().getUserName(), "world")
                : input.message,
            rev.getChange().getId().toString(),
            rev.getPatchSet().number()));
  }

  @Override
  public Description getDescription(RevisionResource resource) {
    return new Description()
        .setLabel("Say hello")
        .setTitle("Say hello in different languages")
        .setVisible(user.get() instanceof IdentifiedUser);
  }

}
