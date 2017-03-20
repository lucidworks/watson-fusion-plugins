package com.lucidworks.apollo.pipeline.query.stages;


import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.QueryStage;
import com.lucidworks.apollo.pipeline.query.Response;
import com.lucidworks.apollo.solr.response.AppendableResponse;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Converts AUDIO in the WAV format to text using IBM's Speech To Text service.  Learn more at https://www.ibm.com/watson/developercloud/speech-to-text.html
 *
 **/
@AutoDiscover(type = SpeechToTextStageConfig.TYPE)
public class SpeechToTextStage extends QueryStage<SpeechToTextStageConfig> {
  private static Logger log = LoggerFactory.getLogger(SpeechToTextStage.class);


  @Inject
  public SpeechToTextStage(@Assisted StageAssistFactoryParams params) {
    super(params);
  }


  @Override
  public QueryRequestAndResponse process(QueryRequestAndResponse message, Context context) throws Exception {
    byte[] entityBytes = message.request.getEntityBytes();
    if (entityBytes != null) {
      //TODO: is this thread safe?  Can I cache it?
      SpeechToText service = new SpeechToText();
      SpeechToTextStageConfig config = getConfiguration();
      service.setUsernameAndPassword(config.getUsername(), config.getPassword());
      String endpoint = config.getWatsonEndpoint();
      if (endpoint != null && endpoint.isEmpty() == false) {
        service.setEndPoint(endpoint);
      }

      //TODO: check the status of the system
      //
      File audio = writeToFile(entityBytes);//Watson API requires a file to be written
      RecognizeOptions.Builder bldr = new RecognizeOptions.Builder().contentType(HttpMediaType.AUDIO_WAV).continuous(Boolean.TRUE);
      RecognizeOptions options = bldr.build();
      log.info("Transcribing {}", audio);
      SpeechResults transcript = service.recognize(audio, options).execute();
      if (transcript != null) {
        String resultsLocation = config.getResultsLocation();
        final Object result;
        if (config.isExtractString()) {
          result = getBestResult(transcript);
        } else {
          result = transcript;
        }
        if (result != null) {
          final String resultsKey = config.getResultsKey();
          switch (resultsLocation) {
            case SpeechToTextStageConfig.REQUEST:
              message.request.putSingleParam(resultsKey, result.toString());
              break;
            case SpeechToTextStageConfig.RESPONSE:
              // Add a transformer to run after the stages run
              final Function<QueryRequestAndResponse, QueryRequestAndResponse> transformer
                      = context.getProperty(Context.RESPONSE_TRANSFORMER, Function.class);
              context.setProperty("responseTransformer", new Function<QueryRequestAndResponse, QueryRequestAndResponse>() {
                @Override
                public QueryRequestAndResponse apply(QueryRequestAndResponse input) {
                  if (transformer != null) {
                    input = transformer.apply(input);
                  }
                  if (input == null) {
                    return null;
                  }
                  if (input.response.isPresent()) {
                    Response response = input.response.get();
                    if (response.initialEntity instanceof AppendableResponse) {
                      ((AppendableResponse) response.initialEntity).appendString(resultsKey, result.toString());
                    } else {
                      log.error("Could not add '" + resultsKey + "' to the Fusion response since it is not appendable");
                    }
                  }
                  return input;
                }
              });

              break;
            case SpeechToTextStageConfig.CONTEXT:
              context.setProperty(resultsKey, result);
              break;
            default:
              //unsupported
              break;
          }
        } else {
          throw new Exception("No  transcription result available for " + audio.getAbsolutePath());
        }
      } else {
        throw new Exception("Unable to obtain transcription from IBM Watson for " + audio.getAbsolutePath());
      }
      try {
        audio.delete();
      } catch (Exception e) {
        log.warn("Unable to delete temporary audio file: " + audio, e);
      }

    }
    return message;
  }


  @NotNull
  protected File writeToFile(byte[] entityBytes) throws IOException {
    File audio = File.createTempFile("speechToText", ".wav");

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(audio);
      IOUtils.write(entityBytes, fos);
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
    return audio;
  }

  private String getBestResult(SpeechResults transcript) {
    String bestResult = null;
    if (transcript != null) {
      double maxConfidence = Double.MIN_VALUE;
      for (Transcript item : transcript.getResults()) {
        for (SpeechAlternative speechAlternative : item.getAlternatives()) {
          if (speechAlternative.getConfidence() > maxConfidence) {
            maxConfidence = speechAlternative.getConfidence();
            bestResult = speechAlternative.getTranscript();
          }
        }
      }
    } else {
      log.warn("Unable to process transcript: {}", transcript);
    }
    log.info("Transcribed best result: " + bestResult);
    if (bestResult != null) {
      bestResult = bestResult.trim();
    }
    return bestResult;
  }

}