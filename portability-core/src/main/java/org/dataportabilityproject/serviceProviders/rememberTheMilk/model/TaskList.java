/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataportabilityproject.serviceProviders.rememberTheMilk.model;

import com.google.api.client.util.Key;
import com.google.common.base.Joiner;
import java.util.List;

/**
 * A list of one or more {@link Task} contained in a {@link TaskSeries}.
 */
public class TaskList {

  @Key("@id")
  public int id;

  @Key("taskseries")
  public List<TaskSeries> taskSeriesList;

  @Override
  public String toString() {
    return String.format("(list id=%d children:[%s])", id,
        (taskSeriesList == null || taskSeriesList.isEmpty()) ? ""
            : Joiner.on("\n").join(taskSeriesList));
  }
}
