# IBM Watson Stages for Lucidworks Fusion
Lucidworks Fusion stages for IBM Watson (BlueMix) Services.


# Prerequisites

1. You will need an IBM BlueMix account.  Depending on what stages you use, you will need to start one or more BlueMix services.  https://www.ibm.com/cloud-computing/bluemix/
1. Lucidworks Fusion 3.0 or higher.  Download at http://lucidworks.com.
 
 
# Installation

1. Edit ~/.gradle/gradle.properties and add 
  ```fusionHome=<PATH TO WHERE FUSION IS INSTALLED>```
1. ./gradlew install
1. Restart Fusion

Note, if you have a multi-node Fusion installation, you will need to copy the jars to each node and set the classpath entries accordingly.  See the build.gradle file for info on what files are updated.


# Usage

The Watson plugins currently consist of 2 query pipeline stages: Speech To text and Query Classification.  To use them, add the stages to an
existing Query Pipeline and fill in the appropriate parameters.  See below for more details.


## Speech To Text

As the name implies, the Speech To Text stage converts audio (we only accept audio/wav MIME type so far) and converts it to text, which
can then be used downstream in the pipeline for querying or other usages.

## API Usage

The Speech To Text stage works by taking in audio/wav MIME type objects and transcribing them to text.  Thus, to use the stage, you need
to submit an audio file to a query pipeline configured to use the Speech To Text stage (see an example configuration below).

See our documentation for more detail, especially:
1. https://doc.lucidworks.com/fusion/3.0/Search/Query-Pipelines.html
1. https://doc.lucidworks.com/fusion/3.0/Pipeline_Stages_Reference/Query-Pipeline-Stages.html
1. https://doc.lucidworks.com/fusion/3.0/REST_API_Reference/Query-Pipelines-API.html

### Example API usage.

Assuming you have a collection named "watson" setup and the Speech To Text stage configured in the default pipeline, you can do the following:


    curl -X POST -H "Content-Type: audio/wav" -u USER:PASS --data-binary @/PATH/TO/WAV/FILE "http://localhost:8764/api/apollo/query-pipelines/default/collections/watson/select"

### Example Configuration
Here's an example configuration, with user and password XXXXXX'd out.  Note that transcribing audio can take some time.

    {
      "id" : "watson",
      "stages" : [ {
        "type" : "speech-to-text",
        "id" : "zo3ld8kamakeqgds4i",
        "username" : "xXx-Redacted-xXx",
        "password" : "xXx-Redacted-xXx",
        "resultsLocation" : "Request",
        "resultsKey" : "q",
        "extractString" : true,
        "type" : "speech-to-text",
        "skip" : false,
        "label" : "speech-to-text",
        "secretSourceStageId" : "zo3ld8kamakeqgds4i"
      }, {
        "type" : "recommendation",
        "id" : "99294a9e-65e5-4e3e-b415-954c0c4e35bf",
        "numRecommendations" : 10,
        "numSignals" : 100,
        "aggrType" : "*",
        "boostId" : "id",
        "type" : "recommendation",
        "skip" : false,
        "label" : "recommendation",
        "secretSourceStageId" : "99294a9e-65e5-4e3e-b415-954c0c4e35bf"
      }, {
        "type" : "search-fields",
        "id" : "77778df6-b753-457b-8c32-180ff505a566",
        "rows" : 10,
        "start" : 0,
        "queryFields" : [ ],
        "returnFields" : [ "*", "score" ],
        "type" : "search-fields",
        "skip" : false,
        "label" : "search-fields",
        "secretSourceStageId" : "77778df6-b753-457b-8c32-180ff505a566"
      }, {
        "type" : "facet",
        "id" : "16ed9919-e894-41d3-9119-309efd7b606b",
        "fieldFacets" : [ ],
        "type" : "facet",
        "skip" : false,
        "label" : "facet",
        "secretSourceStageId" : "16ed9919-e894-41d3-9119-309efd7b606b"
      }, {
        "type" : "solr-query",
        "id" : "38a85e5f-9955-489a-97cb-5426efbb2321",
        "allowedRequestHandlers" : [ ],
        "httpMethod" : "POST",
        "allowFederatedSearch" : false,
        "type" : "solr-query",
        "skip" : false,
        "label" : "solr-query",
        "secretSourceStageId" : "38a85e5f-9955-489a-97cb-5426efbb2321"
      } ],
      "properties" : {
        "secretSourcePipelineId" : "watson"
      }
    }


## Natural Language Query Classifier

The NL Query Classifier is useful for attaching labels to a query, such as the intent of the query.  It requires you to build and train a model using Watson's services.  You must also know the 
name of that model in order to plug it into the stage configuration in Fusion.

### Example API usage.

Assuming you have a collection named "watson" setup and the NL Query Classifier stage configured in the default pipeline, you can do the following:


    curl -X POST  -u USER:PASS  "http://localhost:8764/api/apollo/query-pipelines/default/collections/watson/select?q=is+it+hot+out&classifierId=XXXXXXXXXX"


### Example Stage Configuration

    {
        "id": "default",
        "stages": [
          {
            "type": "nl-query-classifier",
            "id": "m624ajt47a6qg2e29",
            "username": "6c4d626c-6475-443f-9835-93cf56ca3eba",
            "password": "xXx-Redacted-xXx",
            "resultsLocation": "Request",
            "resultsKey": "watsonResults",
            "inputLocation": "Request",
            "inputKey": "q",
            "topCategoryOnly": true,
            "skip": false,
            "label": "nl-query-classifier",
            "secretSourceStageId": "m624ajt47a6qg2e29"
          },
          {
            "type": "recommendation",
            "id": "0df8d4f4-e857-4568-b6b1-cc6ab0b769be",
            "numRecommendations": 10,
            "numSignals": 100,
            "aggrType": "*",
            "boostId": "id",
            "skip": false,
            "label": "recommendation",
            "secretSourceStageId": "0df8d4f4-e857-4568-b6b1-cc6ab0b769be"
          },
          {
            "type": "search-fields",
            "id": "0df37088-0e43-4405-914f-5a397990b066",
            "rows": 10,
            "start": 0,
            "queryFields": [],
            "returnFields": [
              "*",
              "score"
            ],
            "skip": false,
            "label": "search-fields",
            "secretSourceStageId": "0df37088-0e43-4405-914f-5a397990b066"
          },
          {
            "type": "facet",
            "id": "537e1d5d-8a01-472d-8e67-e6363480d062",
            "fieldFacets": [],
            "skip": false,
            "label": "facet",
            "secretSourceStageId": "537e1d5d-8a01-472d-8e67-e6363480d062"
          },
          {
            "type": "solr-query",
            "id": "6d94cf6b-0f64-4dce-be12-7d6115333d47",
            "allowedRequestHandlers": [],
            "httpMethod": "POST",
            "allowFederatedSearch": false,
            "skip": false,
            "label": "solr-query",
            "secretSourceStageId": "6d94cf6b-0f64-4dce-be12-7d6115333d47"
          }
        ],
        "properties": {
          "secretSourcePipelineId": "default"
        }
      }

# Roadmap

1. Add support for the Alchemy APIs.  This work has begun on a branch and is under development.
1. Image categorization.
1. Text to Speech