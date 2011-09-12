/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.api.model.transit;

import java.util.List;

public class StopRouteDirectionScheduleV2Bean {

  private static final long serialVersionUID = 1L;

  private String tripHeadsign;

  private List<ScheduleStopTimeInstanceV2Bean> scheduleStopTimes;
  
  private List<ScheduleFrequencyInstanceV2Bean> scheduleFrequencies;

  public String getTripHeadsign() {
    return tripHeadsign;
  }

  public void setTripHeadsign(String tripHeadsign) {
    this.tripHeadsign = tripHeadsign;
  }

  public List<ScheduleStopTimeInstanceV2Bean> getScheduleStopTimes() {
    return scheduleStopTimes;
  }

  public void setScheduleStopTimes(List<ScheduleStopTimeInstanceV2Bean> stopTimes) {
    this.scheduleStopTimes = stopTimes;
  }

  public List<ScheduleFrequencyInstanceV2Bean> getScheduleFrequencies() {
    return scheduleFrequencies;
  }

  public void setScheduleFrequencies(
      List<ScheduleFrequencyInstanceV2Bean> scheduleFrequencies) {
    this.scheduleFrequencies = scheduleFrequencies;
  }
}