package com.lucidworks.apollo.pipeline.query.stages;


import com.lucidworks.apollo.pipeline.schema.Annotations.ArrayProperty;
import com.lucidworks.apollo.pipeline.schema.Annotations.Schema;
import com.lucidworks.apollo.pipeline.schema.Annotations.SchemaProperty;
import com.lucidworks.apollo.pipeline.schema.Annotations.SchemaPropertyGroup;
import com.lucidworks.apollo.pipeline.schema.Annotations.SchemaPropertyGroups;
import com.lucidworks.apollo.pipeline.schema.Annotations.StringProperty;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.Collections;
import java.util.List;

/**
 *
 *
 **/
@JsonTypeName(AlchemyLanguageStageConfig.TYPE)
@Schema(
        type = AlchemyLanguageStageConfig.TYPE,
        title = "IBM Watson Alchemy Stage",
        description = "This stage forwards documents to the IBM Watson Alchemy Language extraction stage and can extract/label a number of different types of things (entities, sentiment, more.  Note, some parameters (like 'useKnowledgeGraph' are shared" +
                "across multiple extraction types and so it can only be set once. " +
                "See http://www.ibm.com/watson/developercloud/alchemy-language/api/v1/ for more details."
)
@SchemaPropertyGroups(
          value = {@SchemaPropertyGroup(
                  label = "Dates",
                  properties = {
                          "anchorDate"
                  }
          ),
                  @SchemaPropertyGroup(
                          label = "Targeted Emotion",
                          properties = {
                                  "targets"
                          }
                  ),
                  @SchemaPropertyGroup(
                          label = "Entities",
                          properties = {
                                  "quotations", "structuredEntities"
                          }
                  ),
                  @SchemaPropertyGroup(
                          label = "Relations",
                          properties = {
                                  "relations_keywords", "relations_entities", "relations_requireEntities",
                                  "relations_sentimentExcludeEntities"
                          }
                  ),
                  @SchemaPropertyGroup(
                          label = "Typed Relations",
                          properties = {
                                  "typed_model"
                          }
                  ),
                  @SchemaPropertyGroup(
                          label = "Targeted Sentiment",
                          properties = {
                                  "targeted_sentiment_targets"
                          }
                  )
          }
  )
public class AlchemyLanguageStageConfig extends AbstractWatsonIndexStageConfig {

  public static final String TYPE = "alchemy";


  @SchemaProperty(title = "Source Fields", required = true)
  @ArrayProperty(minItems = 1)
  public final List<String> source;

  //!!!!!!!!!!!!! THESE THREE MUST BE IN SYNC
  //NOTE: these are not just uppercased versions of the NAMES
  public static final String[] EXTRACTION_DISPLAY_NAMES = {"Authors", "Concepts", "Dates", "Document Emotion", "Feeds", "Keywords", "Publication Date", "Relations",
          "Typed Relations", "Document Sentiment", "Taxonomy", "Title"
  };
  public static final String[] EXTRACTION_NAMES = {"authors", "concepts", "dates", "doc-emotion", "entities", "feeds", "keywords", "pub-date", "relations",
          "typed-rels", "doc-sentiment", "taxonomy", "title"
  };

  @SchemaProperty(title = "Extraction Types", required = true)
  @ArrayProperty(minItems = 1, defaultValue = "Entities")
  //We repeat this b/c for some reason we can't just pass in EXTRACTION_DISPLAY_NAMES variable here
  @StringProperty(allowedValues = {"Authors", "Concepts", "Dates", "Document Emotion", "Feeds", "Keywords", "Publication Date", "Relations",
          "Typed Relations", "Document Sentiment", "Taxonomy", "Title"
  })
  public final List<String> extractions;
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  //Common/Basic

  @SchemaProperty(name="text", title = "Text to process", description = "The text to process")
  protected String text;

  @SchemaProperty(title = "Show Source Text", description = "if true, the source text is in the response",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean showSourceText;

  @SchemaProperty(name="maxRetrieve", title = "Max Results To Retrieve", description = "",
          defaultValue = "5")
  protected int maxRetrieve;

  @SchemaProperty(name="useKnowledgeGraph", title = "Use Knowledge Graph", description = "True if using knowledge graph information in results.  See the Alchemy documentation for more details",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean useKnowledgeGraph;

  @SchemaProperty(name="emotion", title = "Analyze Emotions as part of entity detection", description = "True if emotions should be associated with detected entities.",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean emotion;

  @SchemaProperty(name="sentiment", title = "Analyze sentiment as part of entity detection", description = "True if sentiment should be associated with detected entities.",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean sentiment;



  @SchemaProperty(name="useLinkedData", title = "Use Linked Data", description = "True if using Linked Data.  Only applicable for some tasks.  See the Alchemy documentation for more details.",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean useLinkedData;

  @SchemaProperty(name="coreferences", title = "Separate co-references?", description = "True if co-references should be treated as separate entities",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean coreferences;

  @SchemaProperty(name="disambiguate", title = "Include disambiguation?", description = "True if disambiguation information should be included in the response",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean disambiguate;



  //Date Extraction

  @SchemaProperty(name="anchorDate", title = "Anchor Date", description = "The date to use as 'today' when interpreting text like 'next Tuesday'.  Format: yyyy-mm-dd hh:mm:ss",
          hints = {UIHints.ADVANCED})
  protected String anchorDate;

  //Targeted Emotion

  @SchemaProperty(name="targets", title = "Targets", description = "List of phrases to target for emotion analysis.  Supports up to 20 phrases",
          hints = {UIHints.ADVANCED})
  @ArrayProperty(maxItems = 20)
  protected List<String> targets;

  //Entities


  @SchemaProperty(name="quotations", title = "Include Quotations?", description = "True if quotations should be linked to detected entities",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean quotations;


  @SchemaProperty(name="structuredEntities", title = "Identify Structured Entities", description = "False if structured entities should be ignored, such as quantity, email addresses and twitter handles.  See the Alchemy docs for more info.",
          defaultValue = "true",
          hints = {UIHints.ADVANCED})
  protected Boolean structuredEntities;


  // Relations

  @SchemaProperty(name="relations_keywords", title = "Identify keywords?", description = "True if keywords information should be included in the response",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean relationsKeywords;

  @SchemaProperty(name="relations_entities", title = "Identify entities?", description = "True if entity information should be included in the response",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean relationsEntities;


  @SchemaProperty(name="relations_requireEntities", title = "Require Entities?", description = "True to restrict to relations that contain at least one named entity.  See the Alchemy documentation for more details.",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean relationsRequireEntities;

  @SchemaProperty(name="relations_sentimentExcludeEntities", title = "Exclude Entity Text from Sentiment Analysis?", description = "True to exclude.",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected Boolean relationsSentimentExcludeEntities;

  //Typed Relations
  @SchemaProperty(name="typed_model", title = "Model Name", description = "The unique alphanumeric identifier for a model or one of the public models: 'en-news', 'es-news', 'ar-news'",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  protected String typedModel;

  //Sentiment
     // only generic

  //Targeted Sentiment
  @SchemaProperty(name="targeted_sentimeent_targets", title = "Targets", description = "List of phrases to target for emotion analysis.  Supports up to 20 phrases",
          hints = {UIHints.ADVANCED})
  @ArrayProperty(maxItems = 20)
  protected List<String> targetedSentimentTargets;


  /*@SchemaProperty(title = "Include Original Response", description = "If true, than the raw XML response from Watson will be included as a field named <fieldName>.watsonResponse.",
          defaultValue = "false",
          hints = {UIHints.ADVANCED})
  public Boolean includeOriginalResponse;*/



  //Truly a work of art, but such is required for Jackson serialization
  //Perhaps it makes sense to have separate stages for these things, but then it would require sending the same content multiple times
  @JsonCreator
  public AlchemyLanguageStageConfig(String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("resultsLocation") String resultsLocation,
                                    @JsonProperty("resultsKey") String resultsKey, @JsonProperty("watsonEndpoint") String watsonEndpoint, @JsonProperty("source") List<String> source,
                                    @JsonProperty("extractions")List<String> extractions, @JsonProperty("text")String text, @JsonProperty("showSourceText")Boolean showSourceText,
                                    @JsonProperty("maxRetrieve")int maxRetrieve,
                                    @JsonProperty("useKnowledgeGraph")Boolean useKnowledgeGraph,
                                    @JsonProperty("useLinkedData")Boolean useLinkedData,
                                    @JsonProperty("anchorDate")String anchorDate,
                                    @JsonProperty("targets")List<String> targets, @JsonProperty("entities_coreference")Boolean coreferences,
                                    @JsonProperty("disambiguate")Boolean disambiguate,
                                    @JsonProperty("quotations")Boolean quotations,
                                    @JsonProperty("emotion")Boolean emotion,
                                    @JsonProperty("sentiment")Boolean sentiment, @JsonProperty("structuredEntities")Boolean structuredEntities,
                                    @JsonProperty("relations_keywords")Boolean relationsKeywords, @JsonProperty("relations_entities")Boolean relationsEntities,
                                    @JsonProperty("relations_requireEntities")Boolean relationsRequireEntities,
                                    @JsonProperty("relations_sentimentExcludeEntities")Boolean relationsSentimentExcludeEntities,
                                    @JsonProperty("typed_model")String typedModel, @JsonProperty("targeted_sentiment_targets")List<String> targetedSentimentTargets) {
    super(id, username, password, resultsLocation, resultsKey, watsonEndpoint);
    if (source != null) {
      this.source = Collections.unmodifiableList(source);
    } else {
      this.source = Collections.emptyList();
    }
    if (extractions != null) {
      this.extractions = Collections.unmodifiableList(source);
    } else {
      this.extractions = Collections.emptyList();
    }
    this.text = text;
    this.showSourceText = showSourceText;
    this.maxRetrieve = maxRetrieve;
    this.useKnowledgeGraph = useKnowledgeGraph;
    this.useLinkedData = useLinkedData;
    this.anchorDate = anchorDate;
    if (targets != null){
      this.targets = Collections.unmodifiableList(targets);
    } else {
      this.targets = Collections.emptyList();
    }

    this.coreferences = coreferences;
    this.disambiguate = disambiguate;
    this.quotations = quotations;
    this.emotion = emotion;
    this.sentiment = sentiment;
    this.structuredEntities = structuredEntities;
    this.relationsKeywords = relationsKeywords;
    this.relationsEntities = relationsEntities;
    this.relationsRequireEntities = relationsRequireEntities;
    this.relationsSentimentExcludeEntities = relationsSentimentExcludeEntities;
    this.typedModel = typedModel;
    if (targetedSentimentTargets != null){
      this.targetedSentimentTargets = Collections.unmodifiableList(targetedSentimentTargets);
    } else {
      this.targetedSentimentTargets = Collections.emptyList();
    }
  }

  @JsonProperty("source")
  public List<String> getSource() {
    return source;
  }

  @JsonProperty("extractions")
  public List<String> getExtractions() {
    return extractions;
  }

  @JsonProperty("text")
  public String getText() {
    return text;
  }

  @JsonProperty("showSourceText")
  public Boolean getShowSourceText() {
    return showSourceText;
  }

  @JsonProperty("maxRetrieve")
  public int getMaxRetrieve() {
    return maxRetrieve;
  }

  @JsonProperty("useKnowledgeGraph")
  public Boolean getUseKnowledgeGraph() {
    return useKnowledgeGraph;
  }

  @JsonProperty("emotion")
  public Boolean getEmotion() {
    return emotion;
  }

  @JsonProperty("sentiment")
  public Boolean getSentiment() {
    return sentiment;
  }

  @JsonProperty("useLinkedData")
  public Boolean getUseLinkedData() {
    return useLinkedData;
  }

  @JsonProperty("coreference")
  public Boolean getCoreferences() {
    return coreferences;
  }

  @JsonProperty("disambiguate")
  public Boolean getDisambiguate() {
    return disambiguate;
  }

  @JsonProperty("anchorDate")
  public String getAnchorDate() {
    return anchorDate;
  }

  @JsonProperty("targets")
  public List<String> getTargets() {
    return targets;
  }

  @JsonProperty("quotations")
  public Boolean getQuotations() {
    return quotations;
  }

  @JsonProperty("structuredEntities")
  public Boolean getStructuredEntities() {
    return structuredEntities;
  }

  @JsonProperty("relationsKeywords")
  public Boolean getRelationsKeywords() {
    return relationsKeywords;
  }

  @JsonProperty("relationsEntities")
  public Boolean getRelationsEntities() {
    return relationsEntities;
  }

  @JsonProperty("relationsRequireEntities")
  public Boolean getRelationsRequireEntities() {
    return relationsRequireEntities;
  }

  @JsonProperty("relationsSentimentExcludeEntities")
  public Boolean getRelationsSentimentExcludeEntities() {
    return relationsSentimentExcludeEntities;
  }

  @JsonProperty("typedModel")
  public String getTypedModel() {
    return typedModel;
  }

  @JsonProperty("targetedSentimentTargets")
  public List<String> getTargetedSentimentTargets() {
    return targetedSentimentTargets;
  }
}