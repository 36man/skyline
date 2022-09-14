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
package org.apache.skyline.engine.api;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.skyline.engine.predicate.PredicateDefinition;
import org.apache.skyline.model.ApiCluster;
import org.apache.skyline.model.ApiGroup;
import org.apache.skyline.plugin.PluginDefinition;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-05 09:53
 */
@Setter
@Getter
@Validated
public class ApiDefinition {

    private int id;

    private int order;

    private int version;

    private String name;

    private String memo;

    private ApiGroup apiGroup;

    private ApiCluster cluster;

    private Date createTime;

    private Date updateTime;

    @NotEmpty
    @Valid
    private List<PredicateDefinition> predicates = new ArrayList<>();

    private List<PluginDefinition> pluginDefinitions = new ArrayList<>();

    private Map<String, Object> metadata = new HashMap<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

}
