package com.lucidworks.apollo.pipeline.query.stages;


import com.codahale.metrics.MetricRegistry;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.StageConfig;
import com.lucidworks.apollo.pipeline.impl.DefaultContext;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 *
 **/
public class TestHelper {
    public static int getRandomPort() {
    ServerSocket server = null;
    try {
      server = new ServerSocket(0);
      return server.getLocalPort();
    } catch (IOException e) {
      throw new Error(e);
    } finally {
      if (server != null) {
        try {
          server.close();
        } catch (IOException ignore) {
          // ignore
        }
      }
    }
  }

  public static <SC extends StageConfig> StageAssistFactoryParams newParams(SC stageConfig) {
    return newParams(stageConfig, null);
  }

  public static <SC extends StageConfig> StageAssistFactoryParams newParams(SC stageConfig, MetricRegistry metricRegistry) {
    return new StageAssistFactoryParams("id", DefaultContext.DEFAULT_CONTEXT_FACTORY,
        (metricRegistry == null)?new MetricRegistry():metricRegistry, stageConfig);
  }
}
