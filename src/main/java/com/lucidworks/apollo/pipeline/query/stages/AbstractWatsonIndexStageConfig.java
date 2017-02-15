package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * See https://github.com/watson-developer-cloud/java-sdk
 **/
public abstract class AbstractWatsonIndexStageConfig extends AbstractWatsonStageConfig {

  public static final String DOCUMENT = "Document";

  @Annotations.SchemaProperty(
          title = RESULTS_LOCATION_TITLE,
          description = RESULTS_LOC_DESC,
          name = AbstractWatsonStageConfig.RESULTS_LOCATION,
          hints = {UIHints.ADVANCED},
          defaultValue = DOCUMENT)
  @Annotations.StringProperty(allowedValues = {DOCUMENT, CONTEXT}) //TODO: should this be an enumerated type?
  protected final String resultsLocation;


  public AbstractWatsonIndexStageConfig(String id, @JsonProperty("username") String username,
                                        @JsonProperty("password") String password,
                                        @JsonProperty("resultsLocation") String resultsLocation,
                                        @JsonProperty("resultsKey") String resultsKey,
                                        @JsonProperty("watsonEndpoint") String watsonEndpoint) {
    super(id, username, watsonEndpoint, resultsKey, password);
    this.resultsLocation = resultsLocation;
  }

  @JsonProperty("resultsLocation")
  public String getResultsLocation() {
    return resultsLocation;
  }

}
