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
package org.apache.skyline.engine.api.locator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.commons.exception.PluginClassNotFoundException;
import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.api.ApiDefinition;
import org.apache.skyline.engine.config.SkylineProperties;
import org.apache.skyline.engine.loader.SkylineClassLoader;
import org.apache.skyline.engine.predicate.PredicateDefinition;
import org.apache.skyline.engine.support.SkylinePackagePath;
import org.apache.skyline.model.ApiCluster;
import org.apache.skyline.model.ApiGroup;
import org.apache.skyline.model.enums.ApiServerQuota;
import org.apache.skyline.plugin.PluginDefinition;
import org.apache.skyline.plugin.PluginDefinitionManager;
import org.apache.skyline.plugin.PluginResourcesResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-07 16:54
 */
public class TestApiDefinitionLocator implements ApiDefinitionLocator, InitializingBean, ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    private SkylineProperties skylineProperties;

    private PluginDefinitionManager pluginDefinitionManager;

    public TestApiDefinitionLocator(SkylineProperties skylineProperties, PluginDefinitionManager pluginDefinitionManager) {
        this.pluginDefinitionManager = pluginDefinitionManager;
        this.skylineProperties = skylineProperties;
    }

    @Override
    public Flux<ApiDefinition> getApiDefinitions() {
        return Flux.defer(() -> {
            ApiDefinition apiDefinition = new ApiDefinition();
            apiDefinition.setId(1);
            apiDefinition.setName("testApi_1");
            apiDefinition.setMemo("testApi_memo_1");
            ApiGroup apiGroup = createGroup();
            ApiCluster apiCluster = createCluster();
            List<PredicateDefinition> predicateDefinitions = createPredicateDefinitions();
            apiDefinition.setApiGroup(apiGroup);
            apiDefinition.setCluster(apiCluster);
            apiDefinition.setPredicates(predicateDefinitions);
            List<PluginDefinition> pluginDefinitions = createPluginDefinitions();
            apiDefinition.setPluginDefinitions(pluginDefinitions);

            // when all of api definition iterate done, then populate class field of it
            PluginResourcesResolver pluginResourcesResolver = new PluginResourcesResolver();
            List<URL> resources = pluginResourcesResolver.getResources(new SkylineClassLoader(PluginResourcesResolver.class.getClassLoader(), skylineProperties.getPluginPath()));
            pluginDefinitionManager.load(resources);
            for (PluginDefinition pluginDefinition : pluginDefinitions) {
                String defineClass = pluginDefinitionManager.getDefineClass(pluginDefinition.getName());
                if (StringUtils.isEmpty(defineClass)) {
                    throw new PluginClassNotFoundException(pluginDefinition.getName());
                }
                pluginDefinition.setDefineClass(defineClass);
            }

            return Flux.fromIterable(List.of(apiDefinition));
        });
    }

    private List<PluginDefinition> createPluginDefinitions() {
        PluginDefinition pluginDefinition1 = new PluginDefinition();
        pluginDefinition1.setName("AddRequestHeader");
        //language=JSON
        String jsonConf = "{\n" +
                "  \"name\": \"full-name\",\n" +
                "  \"value\": \"alex\"\n" +
                "}";
        pluginDefinition1.setConfig(jsonConf);
        pluginDefinition1.setJarUrl("http://localhost:9898/testPlugin.jar");
        pluginDefinition1.setJarName("testPlugin.jar");

        PluginDefinition pluginDefinition2 = new PluginDefinition();
        pluginDefinition2.setName("AddResponseHeader");
        //language=JSON
        String jsonConf2 = "{\n" +
                "  \"name\": \"my-header\",\n" +
                "  \"value\": \"alex\"\n" +
                "}";
        pluginDefinition2.setConfig(jsonConf2);
        pluginDefinition2.setJarUrl("http://localhost:9898/testPlugin2.jar");
        pluginDefinition2.setJarName("testPlugin2.jar");

        // download jar to local disk
        String path = skylineProperties.getPluginPath().split(",")[0];
        File pluginDir = new File(SkylinePackagePath.getPath(), path);
        if (!pluginDir.exists()) {
            boolean mkdirs = pluginDir.mkdirs();
            if (!mkdirs) {
                throw new SkylineException("make plugin dir error");
            }
        }
        try (InputStream input = new URL(pluginDefinition1.getJarUrl()).openStream();
             OutputStream output = new FileOutputStream(new File(pluginDir, pluginDefinition1.getJarName()));) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream input = new URL(pluginDefinition2.getJarUrl()).openStream();
             OutputStream output = new FileOutputStream(new File(pluginDir, pluginDefinition2.getJarName()));) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return List.of(pluginDefinition2, pluginDefinition1);
    }

    private List<PredicateDefinition> createPredicateDefinitions() {
        String text = "Path=/welcome";
        return List.of(new PredicateDefinition(text));
    }

    private static ApiCluster createCluster() {
        ApiCluster apiCluster = new ApiCluster();
        apiCluster.setId(1);
        apiCluster.setName("cluster_1");
        apiCluster.setDomain("www.apache.org");
        apiCluster.setPort(8888);
        apiCluster.setInstanceCount(10);
        Map<ApiServerQuota, Integer> instanceQuotas = new HashMap<>();
        instanceQuotas.put(ApiServerQuota.Q_4C_4G, 5);
        instanceQuotas.put(ApiServerQuota.Q_8C_16G, 5);
        apiCluster.setInstanceQuotas(instanceQuotas);
        apiCluster.setCreateTime(new Date());
        apiCluster.setUpdateTime(new Date());
        apiCluster.setDbUrl("cluster_db_url:jdbc://");
        apiCluster.setRedisUrl("localhost:6379");
        apiCluster.setNacosUrl("localhost:7878");
        return apiCluster;
    }

    private static ApiGroup createGroup() {
        ApiGroup apiGroup = new ApiGroup();
        apiGroup.setId(1);
        apiGroup.setName("group1");
        apiGroup.setMemo("groupMemo1");
        apiGroup.setCreateTime(new Date());
        apiGroup.setUpdateTime(new Date());
        return apiGroup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //todo: nacos add listener
//        List<ApiDefinition> apiDefinitions = new ArrayList<>();
//        publisher.publishEvent(new RefreshApiDefinitionEvent("cluster id", apiDefinitions));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
}
