package com.lucidworks.apollo.pipeline.query.stages;


import com.lucidworks.apollo.pipeline.StageConfig;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 *
 **/
public abstract class AbstractWatsonStageConfig extends StageConfig {

  public static final String CONTEXT = "Context";
  public static final String INPUT_LOCATION = "inputLocation";
  public static final String RESULTS_LOCATION = "resultsLocation";
  public static final String RESULTS_LOCATION_TITLE = "Results Location";
  public static final String RESULTS_LOC_DESC = "The location to put the results.  Whichever is chosen, the results will be stored using the Results Key.";
  @Annotations.SchemaProperty(title = "Username", description = "The username to connect to Watson.  For services that only use an API key, this may be blank.")
  protected final String username;
  @Annotations.SchemaProperty(title = "Password/API Key",
          description = "The password and/or API key to connect to Watson.", required = true,
          hints = {UIHints.SECRET}
  )
  @Annotations.StringProperty(encrypted = true)
  protected final String password;
  @Annotations.SchemaProperty(title = "Watson Endpoint URL",
          hints = {UIHints.ADVANCED},
          description = "The Watson endpoint to use.  Only set if not using Watson's default settings."
          //From the SpeechToText.URL in the Watson java-sdk.
          )
  protected final String watsonEndpoint;
  @Annotations.SchemaProperty(title = "Results Key", description = "The name of the key to store the results object under.  See the documentation for the type of objects stored.",
          defaultValue = "watsonResults",
          required = true)
  protected String resultsKey;

  public AbstractWatsonStageConfig(String id, @JsonProperty("username") String username, @JsonProperty("watsonEndpoint") String watsonEndpoint, @JsonProperty("resultsKey") String resultsKey, @JsonProperty("password") String password) {
    super(id);
    this.username = username;
    this.watsonEndpoint = watsonEndpoint;
    this.resultsKey = resultsKey;
    this.password = password;
  }

  @JsonProperty("resultsKey")
  public String getResultsKey() {
    return resultsKey;
  }

  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  @JsonProperty("watsonEndpoint")
  public String getWatsonEndpoint() {
    return watsonEndpoint;
  }
}