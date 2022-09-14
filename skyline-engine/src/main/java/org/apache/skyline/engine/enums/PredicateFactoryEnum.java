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
package org.apache.skyline.engine.enums;

import java.util.Arrays;

/**
 * @author lijian
 * @since time: 2022-09-06 17:20
 */
public enum PredicateFactoryEnum {
    AFTER("After"), BEFORE("Before"), BETWEEN("Between"), COOKIE("Cookie"),
    HEADER("Header"), HOST("Host"), METHOD("Method"), PATH("Path"),
    QUERY("Query"), REMOTE_ADDR("RemoteAddr"), X_FORWARDED_REMOTE_ADDR("XForwardedRemoteAddr");

    private String name;

    PredicateFactoryEnum(String name) {
        this.name = name;
    }

    public static PredicateFactoryEnum getPredicateFactoryEnum(String name) {
        return Arrays.stream(values()).filter(f -> f.name.equals(name)).findAny().orElse(null);
    }
}
