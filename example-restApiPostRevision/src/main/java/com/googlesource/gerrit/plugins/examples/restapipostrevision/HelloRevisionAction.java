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
import com.google.gerrit.exceptions.StorageException;
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
import com.google.inject.assistedinject.Assisted;
import com.google.gerrit.server.update.ChangeContext;
import com.google.gerrit.server.util.time.TimeUtil;
import com.google.gerrit.server.change.ChangeResource;
import com.google.gerrit.server.update.BatchUpdateOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HelloRevisionAction
        implements UiAction<RevisionResource>,
        RestModifyView<RevisionResource, HelloRevisionAction.Input> {

  private static final Logger log = LoggerFactory.getLogger(HelloRevisionAction.class);
  private final BatchUpdate.Factory batchUpdateFactory;
  private final Provider<CurrentUser> userProvider;

  static class Input {
    boolean french;
    String message;
  }

  @Inject
  HelloRevisionAction(Provider<CurrentUser> user,
                      BatchUpdate.Factory batchUpdateFactory
  ) {
    this.userProvider = user;
    this.batchUpdateFactory = batchUpdateFactory;
    log.info("entering HelloRevisionAction() ");
  }

  @Override
  public Response<String> apply(RevisionResource rev, Input input) {
    final String greeting = input.french ? "Bonjour" : "Hello";

    ChangeResource r = rev.getChangeResource();
    Change change = r.getChange();
    log.info("entering apply() ");

    try (BatchUpdate bu = batchUpdateFactory.create(
            change.getProject(), userProvider.get(), TimeUtil.nowTs())) {
      bu.addOp(change.getId(), new BatchUpdateOp() {
        @Override
        public boolean updateChange(ChangeContext ctx) {
          return true;
        }
      });
      bu.execute();
      log.info("executing BatchUpdate.execute().. ");
    } catch (Exception e) {
      log.info("executing BatchUpdate.execute() exception: " + Response.none().toString());
      return  Response.none();
    }
    return Response.ok(
            String.format(
                    "%s %s from change %s, patch set %d!",
                    greeting,
                    Strings.isNullOrEmpty(input.message)
                            ? MoreObjects.firstNonNull(userProvider.get().getUserName(), "world")
                            : input.message,
                    rev.getChange().getId().toString(),
                    rev.getPatchSet().number()));
  }

  @Override
  public Description getDescription(RevisionResource resource) {
    return new Description()
            .setLabel("Say hello---")
            .setTitle("Say hello in uuu different languages")
            .setVisible(userProvider.get() instanceof IdentifiedUser);
  }

}
