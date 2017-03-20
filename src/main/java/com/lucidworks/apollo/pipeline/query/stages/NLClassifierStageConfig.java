package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/

import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 *
 *
 **/
@JsonTypeName(NLClassifierStageConfig.TYPE)
@Annotations.Schema(
        type = NLClassifierStageConfig.TYPE,
        title = "IBM Watson Natural Language Query Classifier",
        description = "This stage forwards query request data to IBM Watson's Natural Language Classifier service.  It is up to you to train the model for classification.  " +
                "If you are putting the results into either the REQUEST or RESPONSE object, the raw response from IBM will be converted to a string using String Template"
)
public class NLClassifierStageConfig extends AbstractWatsonQueryStageConfig {

  public static final String CLASSIFIER_ID_KEY = "classifierId";

  public static final String TYPE = "nl-query-classifier";

  @Annotations.SchemaProperty(title = "Input Key",
          hints = {UIHints.ADVANCED},
          description = "The key to use to lookup the input to send to Watson.  The value stored under this key may be a String Template.",
          required = true,
          //From the SpeechToText.URL in the Watson java-sdk.
          defaultValue = "q")
  protected final String inputKey;

  @Annotations.SchemaProperty(
          title = "Input Location",
          description = "The location to get the input from, the context or the request.  Whichever is chosen, it will be looked up using the input key.",
          name = INPUT_LOCATION,
          hints = {UIHints.ADVANCED},
          defaultValue = REQUEST)
  @Annotations.StringProperty(allowedValues = {REQUEST, CONTEXT}) //TODO: should this be an enumerated type?
  protected final String inputLocation;

  @Annotations.SchemaProperty(title = "Results Template",
          hints = {UIHints.ADVANCED},
          description = "The String Template to apply to the Classification when generating the results value.  The String Template context will contain the Classification object, the request, context and response, if applicable.  The Classification object is stored in the String Template Context as 'classification'.  If a template is provided, than Top Category Only is ignored.",
          required = false)
  protected final String resultsTemplate;

  @Annotations.SchemaProperty(title = "Default Classifier", description = "If a Watson Classifier ID is not passed in via the 'nl-classifier-id' request parameter, the system will use this one by default.  If it is not specified and not in the request, the system will raise an error.",
          required = false,
          hints = {UIHints.ADVANCED})
  protected String nlClassifierId;

  @Annotations.SchemaProperty(title = "Use Top Category Only", description = "If true and the results location is either REQUEST or RESPONSE, than only use the top matching category, otherwise all categories will be appended by whitespace to a string",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean topCategoryOnly;

  @org.codehaus.jackson.annotate.JsonCreator
  public NLClassifierStageConfig(@JsonProperty("id") String id,
                                 @JsonProperty("username") String username,
                                 @JsonProperty("password") String password,
                                 @JsonProperty("resultsLocation") String resultsLocation,
                                 @JsonProperty("resultsKey") String resultsKey,
                                 @JsonProperty("watsonEndpoint") String watsonEndpoint,
                                 @JsonProperty("nlClassifierId") String nlClassifierId,
                                 @JsonProperty("inputLocation") String inputLocation,
                                 @JsonProperty("inputKey") String inputKey,
                                 @JsonProperty("resultsTemplate") String resultsTemplate,
                                 @JsonProperty("topCategoryOnly") Boolean topCategoryOnly) {
    super(id, username, password, resultsLocation, resultsKey, watsonEndpoint);
    this.nlClassifierId = nlClassifierId;
    this.inputLocation = inputLocation;
    this.inputKey = inputKey;
    this.resultsTemplate = resultsTemplate;
    this.topCategoryOnly = topCategoryOnly;
  }

  @JsonProperty("resultsTemplate")
  public String getResultsTemplate() {
    return resultsTemplate;
  }

  @JsonProperty("topCategoryOnly")
  public Boolean isTopCategoryOnly() {
    return topCategoryOnly;
  }

  @JsonProperty("nlClassifierId")
  public String getNlClassifierId() {
    return nlClassifierId;
  }

  @JsonProperty("inputKey")
  public String getInputKey() {
    return inputKey;
  }

  @JsonProperty("inputLocation")
  public String getInputLocation() {
    return inputLocation;
  }
}