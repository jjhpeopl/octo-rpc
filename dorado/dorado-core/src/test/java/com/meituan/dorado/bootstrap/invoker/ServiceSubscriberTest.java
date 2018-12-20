/*
 * Copyright 2018 Meituan Dianping. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meituan.dorado.bootstrap.invoker;

import com.meituan.dorado.MockUtil;
import com.meituan.dorado.cluster.ClusterHandler;
import com.meituan.dorado.common.exception.RpcException;
import com.meituan.dorado.config.service.ReferenceConfig;
import com.meituan.dorado.mock.MockCluster;
import com.meituan.dorado.registry.support.FailbackRegistry;
import org.junit.Assert;
import org.junit.Test;

public class ServiceSubscriberTest {

    @Test
    public void testSubscribeService() {
        ReferenceConfig config = MockUtil.getReferenceConfig();

        ClusterHandler clusterHandler = ServiceSubscriber.subscribeService(config);
        Assert.assertTrue(clusterHandler instanceof MockCluster.MockClusterHandler);
        Assert.assertNull(clusterHandler.getRepository().getRegistry());

        // empty registry
        config.setRegistry(null);
        clusterHandler = ServiceSubscriber.subscribeService(config);
        Assert.assertNull(clusterHandler.getRepository().getRegistry());

        // direct conn
        config.setDirectConnAddress("localhost:9001,localhost:9002");
        clusterHandler = ServiceSubscriber.subscribeService(config);
        Assert.assertTrue(clusterHandler.getRepository().isDirectConn());

        // wrong cluster policy
        config.setClusterPolicy("error");
        try {
            ServiceSubscriber.subscribeService(config);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RpcException);
        }
    }
}
