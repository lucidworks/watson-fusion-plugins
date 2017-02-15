package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/

import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * See https://github.com/watson-developer-cloud/java-sdk
 **/
@JsonTypeName(SpeechToTextStageConfig.TYPE)
@Annotations.Schema(
        type = SpeechToTextStageConfig.TYPE,
        title = "IBM Watson Speech to Text",
        description = "This stage forwards Audio content to the IBM Bluemix Speech To Text Service and either converts to text and adds to a parameter on the request or puts the results into the pipeline context for downstream handling."
)
public class SpeechToTextStageConfig extends AbstractWatsonQueryStageConfig {

  public static final String TYPE = "speech-to-text";

  @Annotations.SchemaProperty(title = "Extract Best String", description = "If true, than the best string is extracted and added using the results key, if false, the full Watson response (SpeechResults object) is stored under the results key.  If the results location is REQUEST or RESPONSE, then that SpeechResults object will be converted to a String representation.",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean extractString;


  @JsonCreator
  public SpeechToTextStageConfig(@JsonProperty("id") String id,
                                 @JsonProperty("username") String username,
                                 @JsonProperty("password") String password,
                                 @JsonProperty("resultsLocation") String resultsLocation,
                                 @JsonProperty("resultsKey") String resultsKey,
                                 @JsonProperty("extractString") Boolean extractString,
                                 @JsonProperty("watsonEndpoint") String watsonEndpoint) {
    super(id, username, password, resultsLocation, resultsKey, watsonEndpoint);
    this.extractString = extractString;
  }

  @JsonProperty("extractString")
  public Boolean isExtractString() {
    return extractString;
  }

}