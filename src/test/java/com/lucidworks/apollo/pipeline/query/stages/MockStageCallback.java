package com.lucidworks.apollo.pipeline.query.stages;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.lucidworks.apollo.common.pipeline.PipelineDocument;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageCallback;
import com.lucidworks.apollo.pipeline.StageConfig;

import java.util.Map;
import java.util.Map.Entry;

public class MockStageCallback implements StageCallback<PipelineDocument> {
  public MetricRegistry r;

  public MockStageCallback() {
    r = new MetricRegistry();
  }



  public MetricRegistry getMetricRegistry() {
    return r;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    Map<String, Counter> counters = r.getCounters();
    for (Entry<String, Counter> e : counters.entrySet()) {
      sb.append(e.getKey());
      sb.append('=');
      sb.append(String.valueOf(e.getValue().getCount()));
      sb.append('\n');
    }
    return sb.toString();
  }

  @Override
  public void onStageEmit(PipelineDocument message, Context context, String stageId, StageConfig stageConfig, boolean isLast) {

  }

  @Override
  public void onStageError(PipelineDocument message, Context context, String stageId, StageConfig stageConfig, Throwable t) {

  }

  @Override
  public void onStageEvent(Object event) {

  }
}
