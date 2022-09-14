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
package org.apache.skyline.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.skyline.model.predicate.AsyncPredicate;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-05 10:49
 */
@Setter
@Getter
public class Api implements Ordered {

    private int id;

    private String identification;

    private int order;

    private int version;

    private String name;

    private String memo;

    private ApiCluster apiCluster;

    private ApiGroup apiGroup;

    private Date createTime;

    private Date updateTime;

    private AsyncPredicate<ServerRequest> predicate;

    private List<SkylinePlugin> plugins = new ArrayList<>();

    private Map<String, Object> metadata = new HashMap<>();

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

}
