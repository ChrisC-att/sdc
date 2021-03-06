/*
 * Copyright © 2016-2017 European Support Limited
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

package org.openecomp.sdc.logging.slf4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;

class ContextPropagationTestHelper {

    // Set to "false" if an old version of logback implementation is being used.
    // Explicit context propagation should be used when the context is not propagated to child threads.
    // See https://jira.qos.ch/browse/LOGBACK-422 and https://jira.qos.ch/browse/LOGBACK-624
    static final boolean IS_SUITABLE_LOGBACK_VERSION = true;

    static final String EXPECT_PROPAGATED_TO_CHILD = "Expected the data to be propagated to the child thread's context";
    static final String EXPECT_RETAINED_IN_CURRENT = "Expected the data to be retained in this thread";
    static final String EXPECT_REPLACED_WITH_STORED = "Expected context data to be replaced with stored data";
    static final String EXPECT_INNER_RUN = "Expected the inner thread to run";
    static final String EXPECT_OUTER_RUN = "Expected the outer thread to run";
    static final String EXPECT_NOT_COPIED = "Expected context data not to be copied to this thread";
    static final String EXPECT_RETAINED_IN_PARENT = "Expected context data to be retained in parent thread";
    static final String EXPECT_POPULATED = "Expected context data to be populated in this thread";
    static final String EXPECT_EMPTY = "Expected context data to be empty";
    static final String EXPECT_REMAIN_EMPTY = "Expected context data to remain empty in this thread";
    static final String EXPECT_REVERTED_ON_EXCEPTION = "Expected context data to be reverted even in case of exception";
    static final String EXPECT_EXCEPTION_FROM_INNER = "Expected the inner class to throw exception";

    private ContextPropagationTestHelper() {
        // prevent instantiation
    }

    static Map<ContextField, String> putUniqueValues() {

        Map<ContextField, String> values = new EnumMap<>(ContextField.class);

        String random = UUID.randomUUID().toString();

        for (ContextField key : ContextField.values()) {
            String value = random + "-" + key.name();
            values.put(key, value);
            MDC.put(key.asKey(), value);
        }

        return values;
    }

    static void assertContextFields(String error, Map<ContextField, String> values) {

        for (ContextField f : ContextField.values()) {
            assertEquals(error, MDC.get(f.asKey()), values.get(f));
        }
    }

    static void assertContextEmpty(String error) {

        for (ContextField key : ContextField.values()) {
            assertNull(error, MDC.get(key.asKey()));
        }
    }
}
