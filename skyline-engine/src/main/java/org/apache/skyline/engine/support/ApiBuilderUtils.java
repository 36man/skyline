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
package org.apache.skyline.engine.support;

import org.apache.skyline.engine.api.ApiDefinition;
import org.apache.skyline.engine.api.builder.AsyncBuilder;
import org.apache.skyline.engine.api.builder.Builder;

/**
 * @author lijian
 * @since time: 2022-09-13 20:03
 */
public final class ApiBuilderUtils {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApiDefinition apiDefinition) {
        // @formatter:off
        return new Builder().id(apiDefinition.getId())
                .name(apiDefinition.getName())
                .memo(apiDefinition.getMemo())
                .order(apiDefinition.getOrder())
                .createTime(apiDefinition.getCreateTime())
                .updateTime(apiDefinition.getUpdateTime())
                .apiGroup(apiDefinition.getApiGroup())
                .apiCluster(apiDefinition.getCluster())
                .pluginDefinitions(apiDefinition.getPluginDefinitions())
                .metadata(apiDefinition.getMetadata());

        // @formatter:on
    }

    public static AsyncBuilder async() {
        return new AsyncBuilder();
    }

    public static AsyncBuilder async(ApiDefinition apiDefinition) {
        // @formatter:off
        return new AsyncBuilder().id(apiDefinition.getId())
                .name(apiDefinition.getName())
                .memo(apiDefinition.getMemo())
                .order(apiDefinition.getOrder())
                .createTime(apiDefinition.getCreateTime())
                .updateTime(apiDefinition.getUpdateTime())
                .apiGroup(apiDefinition.getApiGroup())
                .apiCluster(apiDefinition.getCluster())
                .pluginDefinitions(apiDefinition.getPluginDefinitions())
                .metadata(apiDefinition.getMetadata());
        // @formatter:on
    }
}
