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
package org.apache.skyline.plugin.api;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lijian
 * @since 2022-11-09 16:23
 */
public class CapableSwitch<V> {

    @Getter
    private final String name;

    @Setter
    @Getter
    private String desc;
    @Setter
    private V value;

    public CapableSwitch(String name) {
        this.name = name;
    }

    public V getValue(V defaultValue) {
        if (value != null) {
            return value;
        }
        value = defaultValue;
        return value;
    }

    public static <T> CapableSwitch<T> as(String name) {
        return new CapableSwitch<>(name);
    }

}
