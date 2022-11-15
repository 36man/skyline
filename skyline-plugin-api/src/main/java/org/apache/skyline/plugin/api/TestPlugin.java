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

import lombok.Data;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lijian
 * @since 2022-11-09 16:15
 */
public class TestPlugin implements SkylinePlugin<TestPlugin.Config> {

    private static final CapableSwitch<String> SWITCH_NO1_NAME = CapableSwitch.as("NO1");
    private static final CapableSwitch<Long> SWITCH_NO1_AGE = CapableSwitch.as("age");

    public List<CapableSwitch<?>> exportCapableSwitches() {
        return List.of(SWITCH_NO1_NAME, SWITCH_NO1_AGE);
    }

    @Override
    public List<PerpetualResource> exportPerpetualObjs() {
        return List.of(PerpetualResource.as("1", "dubboClient"));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, SkylinePluginChain chain) {
        Config config = chain.getConfig();

        String fileIndex = config.getFileIndex();
        String fileName = config.getFileName();


        CapableSwitch<String> dubboConfig =  chain.getCapableSwitchManager().group("xxxxxxx.xxx").getSwitch("dubboConfigSwitch");



        int i = 0;
        int j = 1;
        String ii = "1";
        long iii = 1L;

        CapableSwitch<String> no1Switch = chain.getCapableSwitchManager()
                .getGroupSwitches(TestPlugin.class.getName()).getSwitch(SWITCH_NO1_NAME.getName());

        CapableSwitch<Long> ageSwitch = chain.getCapableSwitchManager()
                .getGroupSwitches(TestPlugin.class.getName()).getSwitch(SWITCH_NO1_AGE.getName());
        String no1Value = no1Switch.getValue("1");
        Long ageValue = ageSwitch.getValue(1L);

        if (no1Value.equals("??")) {

        } else if (no1Value.equals("???") && ageValue == 2L) {

        }

//        if (i == 0) { // logic 1
//            dubboConfig.set(???)
//            dubboClient.send(???)
//        } else if (i == 1) { // logic 2
//
//        } else { // logic
//
//        }

        CapableSwitch<Integer> capacitySwitch = chain.getCapableSwitchManager().group(TestPlugin.class.getName()).getSwitch(SWITCH_NO1_NAME.getName());
        return null;
    }

    @Override
    public Class<Config> getConfigClass() {
        return null;
    }

    @Data
    public static class Config {
        private String fileIndex;
        private String fileName;
    }

}
