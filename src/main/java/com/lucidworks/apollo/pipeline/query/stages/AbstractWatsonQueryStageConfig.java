package com.lucidworks.apollo.pipeline.query.stages;


import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Base class with some helpful attributes for query stages
 *
 **/
public abstract class AbstractWatsonQueryStageConfig extends AbstractWatsonStageConfig {

  public static final String REQUEST = "Request";
  public static final String RESPONSE = "Response";
  @Annotations.SchemaProperty(
          title = RESULTS_LOCATION_TITLE,
          description = RESULTS_LOC_DESC,
          name = RESULTS_LOCATION,
          hints = {UIHints.ADVANCED},
          defaultValue = REQUEST)
  @Annotations.StringProperty(allowedValues = {REQUEST, RESPONSE, CONTEXT}) //TODO: should this be an enumerated type?
  protected final String resultsLocation;


  public AbstractWatsonQueryStageConfig(String id, @JsonProperty("username") String username,
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