package com.lucidworks.apollo.pipeline.query.stages;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.lucidworks.apollo.common.pipeline.PipelineDocument;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.index.SimpleIndexStage;

/**
 *
 *
 **/
@AutoDiscover(type = AlchemyLanguageStageConfig.TYPE)
public class AlchemyLanguageStage extends SimpleIndexStage<AlchemyLanguageStageConfig> {


  @Inject
  public AlchemyLanguageStage(@Assisted StageAssistFactoryParams params) {
    super(params);
  }

  @Override
  public PipelineDocument process(PipelineDocument pipelineDocument, Context context) throws Exception {
    PipelineDocument result = null;
    AlchemyLanguageStageConfig config = getConfiguration();
    AlchemyLanguage service = new AlchemyLanguage();
    service.setApiKey(config.getPassword());

    return result;
  }


}
