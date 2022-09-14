/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.skyline.engine.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-07 15:29
 */
public class PredicateArgsEvent extends ApplicationEvent {
    private final Map<String, Object> args;

    private final String apiIdentity;

    public PredicateArgsEvent(Object source, String apiIdentification, Map<String, Object> args) {
        super(source);
        this.apiIdentity = apiIdentification;
        this.args = args;
    }

    public String getApiIdentity() {
        return apiIdentity;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}
