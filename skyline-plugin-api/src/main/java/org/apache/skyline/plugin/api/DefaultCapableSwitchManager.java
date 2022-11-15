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

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijian
 * @since 2022-11-09 16:28
 */
public class DefaultCapableSwitchManager {

    @Getter
    private final Map<String, Group> groups = new HashMap<>();

    public Group group(String groupName) {
        Group group = groups.get(groupName);
        return group;
    }

    public Group getGroupSwitches(String groupName) {
        Group group = groups.get(groupName);
        if (group == null) {
            group = new Group(groupName);
        }
        return group;
    }

    public static class Group {
        private final String name;

        @Getter
        private final Map<String, CapableSwitch<?>> capableSwitchHashMap = new HashMap<>();

        public Group(String name) {
            this.name = name;
        }

        public <T> CapableSwitch<T> getSwitch(String switchName) {
            CapableSwitch<?> rawCapableSwitch = capableSwitchHashMap.get(switchName);
            CapableSwitch<T> capableSwitch = (CapableSwitch<T>) capableSwitchHashMap.get(switchName);
            if (capableSwitch == null) {
                capableSwitch = new CapableSwitch<>(switchName);
                capableSwitchHashMap.put(switchName, capableSwitch);
            }
            return capableSwitch;
        }
    }

}
