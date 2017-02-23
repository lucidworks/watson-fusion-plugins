# watson-stages
Lucidworks Fusion stages for IBM Watson (BlueMix) Services.


Prerequisites

1. You will need an IBM BlueMix account.  Depending on what stages you use, you will need to start one or more BlueMix services.  https://www.ibm.com/cloud-computing/bluemix/
 
 
# Installation

1. Edit ~/.gradle/gradle.properties and add 
  ```fusionHome=<PATH TO FUSION>```
1. ./gradlew install
1. Restart Fusion



# Usage

The Watson plugins currently consist of 2 query pipeline stages: Speech To text and Query Classification.  To use them, add the stages to an
existing Query Pipeline and fill in the appropriate parameters.

## Speech To Text

As the name implies, the Speech To Text stage converts audio (we only accept audio/wav MIME type so far) and converts it to text, which
can then be used downstream in the pipeline for querying or other usages.

### Example Configuration
Here's an example configuration, with user and password XXXXXX'd out.  Note that transcribing audio can take some time.  


## Natural Language Query Classifier

The NL Query Classifier is useful for attaching labels to a query, such as the intent of the query.

# Roadmap

1. Add support for the Alchemy APIs.  This work has begun on a branch and is under development.
1. Image categorization.
1. Text to Speech