/*
 * Copyright © 2016-2018 European Support Limited
 *
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
 */

package org.onap.sdc.tosca.datatypes.model;

import java.util.List;
import java.util.Map;

/**
 * @author KATYR
 * @since May 13, 2018
 */

public class CapabilityFilter {

    private List<Map<String, List<Constraint>>> properties;

    public List<Map<String, List<Constraint>>> getProperties() {
        return properties;
    }

    public void setProperties(List<Map<String, List<Constraint>>> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "CapabilityFilter{" + "properties=" + properties + '}';
    }

}
