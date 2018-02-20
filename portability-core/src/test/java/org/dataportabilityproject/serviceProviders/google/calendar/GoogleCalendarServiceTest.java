/*
 * Copyright 2018 The Data-Portability Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataportabilityproject.serviceProviders.google.calendar;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.CalendarList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.dataportabilityproject.cloud.interfaces.JobDataCache;
import org.dataportabilityproject.dataModels.ExportInformation;
import org.dataportabilityproject.dataModels.calendar.CalendarEventModel;
import org.dataportabilityproject.dataModels.calendar.CalendarModel;
import org.dataportabilityproject.dataModels.calendar.CalendarModelWrapper;
import org.dataportabilityproject.shared.StringPaginationToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class GoogleCalendarServiceTest {
  private static final String CALENDAR_ID = "calendar_id";
  private static final String CALENDAR_SUMMARY = "calendar summary";
  private static final String CALENDAR_DESCRIPTION = "calendar description";
  private static final CalendarListEntry CALENDAR_LIST_ENTRY = new CalendarListEntry().setId
      (CALENDAR_ID).setSummary(CALENDAR_SUMMARY).setDescription(CALENDAR_DESCRIPTION);

  private static final String NEXT_TOKEN = "next_token";

  private GoogleCalendarService calendarService;

  private Calendar calendarClient;
  private JobDataCache jobDataCache;
  private Calendar.CalendarList calendarCalendarList;
  private Calendar.CalendarList.List listRequest;
  private CalendarList listResponse;

  @Before
  public void setup() throws IOException {
    calendarCalendarList = mock(Calendar.CalendarList.class);
    listRequest = mock(Calendar.CalendarList.List.class);

    calendarClient = mock(Calendar.class);
    jobDataCache = mock(JobDataCache.class);

    calendarService = new GoogleCalendarService(calendarClient, jobDataCache);

    when(calendarClient.calendarList()).thenReturn(calendarCalendarList);
    when(calendarCalendarList.list()).thenReturn(listRequest);
  }

  private void setUpSingleCalendarResponse() throws IOException {
    listResponse = new CalendarList().setItems(Collections.singletonList(CALENDAR_LIST_ENTRY));

    when(listRequest.execute()).thenReturn(listResponse);
  }

  @Test
  public void testExportFirstSet() throws IOException {
    setUpSingleCalendarResponse();

    // Looking at first page, with at least one page after it
    ExportInformation emptyExportInformation = new ExportInformation(Optional.empty(),
        Optional.empty());
    listResponse.setNextPageToken(NEXT_TOKEN);

    // Run test
    CalendarModelWrapper wrapper = calendarService.export(emptyExportInformation);

    // Check results
    // Verify correct methods were called
    verify(calendarClient).calendarList();
    verify(calendarCalendarList).list();
    verify(listRequest).execute();

    // Check pagination token
    StringPaginationToken paginationToken = (StringPaginationToken) wrapper
        .getContinuationInformation().getPaginationInformation();
    assertThat(paginationToken.getId()).isEqualTo(NEXT_TOKEN);

    // Check calendars
    Collection<CalendarModel> calendars = wrapper.getCalendars();
    assertThat(calendars.stream().map(CalendarModel::getId).collect(Collectors.toList()))
        .containsExactly(CALENDAR_ID);

    // Check events (should be empty)
    Collection<CalendarEventModel> events = wrapper.getEvents();
    assertThat(events).isEmpty();
  }

  @Test
  public void testExportSubsequentSet() throws IOException {
    setUpSingleCalendarResponse();

    // Looking at subsequent page, with no page after it
    ExportInformation nextPageExportInformation = new ExportInformation(Optional.empty(),
        Optional.of(new StringPaginationToken(NEXT_TOKEN)));
    listResponse.setNextPageToken(null);

    // Run test
    CalendarModelWrapper wrapper = calendarService.export(nextPageExportInformation);

    // Check results
    // Verify correct calls were made
    InOrder inOrder = Mockito.inOrder(listRequest);
    inOrder.verify(listRequest).setPageToken(NEXT_TOKEN);
    inOrder.verify(listRequest).execute();

    // Check pagination token
    StringPaginationToken paginationToken = (StringPaginationToken) wrapper
        .getContinuationInformation().getPaginationInformation();
    assertThat(paginationToken).isNull();
  }
}
