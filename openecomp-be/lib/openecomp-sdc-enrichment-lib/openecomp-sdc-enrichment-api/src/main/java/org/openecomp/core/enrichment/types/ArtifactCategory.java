/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.core.enrichment.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum ArtifactCategory {

  INFORMATIONAL("Informational"),
  DEPLOYMENT("Deployment");

  private static final Map<String, ArtifactCategory> mMap =
      Collections.unmodifiableMap(initializeMapping());

  @Getter
  private String displayName;

  /**
   * Initialize mapping map.
   *
   * @return the map
   */
  public static Map<String, ArtifactCategory> initializeMapping() {
    Map<String, ArtifactCategory> typeMap = new HashMap<>();
    for (ArtifactCategory v : ArtifactCategory.values()) {
      typeMap.put(v.displayName, v);
    }
    return typeMap;
  }

  /**
   * Gets artifact type by display name.
   *
   * @param displayName the display name
   * @return the artifact type by display name
   */
  public static ArtifactCategory getArtifactTypeByDisplayName(String displayName) {
    if (mMap.containsKey(displayName)) {
      return mMap.get(displayName);
    }
    return null;
  }
}
