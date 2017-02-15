package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifiedClass;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.QueryStage;
import com.lucidworks.apollo.pipeline.query.Request;
import com.lucidworks.apollo.pipeline.query.Response;
import com.lucidworks.apollo.solr.response.AppendableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 **/
@AutoDiscover(type = NLClassifierStageConfig.TYPE)
public class NLClassifierStage extends QueryStage<NLClassifierStageConfig> {
  private static final String CLASSIFICATION_TEMPLATE_KEY = "classification";

  private static Logger log = LoggerFactory.getLogger(NLClassifierStage.class);

  @Inject
  public NLClassifierStage(@Assisted StageAssistFactoryParams params) {
    super(params);
  }

  @Override
  public QueryRequestAndResponse process(final QueryRequestAndResponse message, final Context context) throws Exception {
    QueryRequestAndResponse result = message;
    final NLClassifierStageConfig config = getConfiguration();
    String inputLoc = config.getInputLocation();
    String inputContent = null;
    String inputKey = config.getInputKey();
    switch (inputLoc) {
      case NLClassifierStageConfig.REQUEST: {
        inputContent = message.request.getFirstFieldValue(inputKey);
        break;
      }
      case NLClassifierStageConfig.CONTEXT: {
        Object prop = context.getProperty(inputKey);
        inputContent = (prop != null ? prop.toString() : null);
        break;
      }
      default: {
        throw new Exception("Invalid input location: " + inputLoc);
      }
    }
    if (inputContent != null) {
      inputContent = renderTemplate(inputContent, message, context);
    }
    if (inputContent != null) {//it's possible renderTempmlate yields null
      //TODO: is this cacheable?  Thread-safe?
      NaturalLanguageClassifier service = new NaturalLanguageClassifier();
      service.setUsernameAndPassword(config.getUsername(), config.getPassword());
      String endpoint = config.getWatsonEndpoint();
      if (endpoint != null && endpoint.isEmpty() == false) {
        service.setEndPoint(endpoint);
      }
      String classId = message.request.getFirstParam(NLClassifierStageConfig.CLASSIFIER_ID_KEY);
      if (classId == null) {
        //see if there is a default
        classId = config.getNlClassifierId();
        if (classId == null) {
          //if we still don't have any, then we have an error
          throw new Exception("You must either pass in a classifier ID in the request using the " + NLClassifierStageConfig.CLASSIFIER_ID_KEY + " key or set a default classifier ID on the Stage configuration");
        }
      }
      final ServiceCall<Classification> classificationCall = service.classify(classId, inputContent);
      //TODO: fix this
      Classification classification = classificationCall.execute();
      if (classification != null) {
        String resultsLocation = config.getResultsLocation();
        final String resultsKey = config.getResultsKey();
        switch (resultsLocation) {
          case NLClassifierStageConfig.REQUEST: {
            //Hmm,
            message.request.addParam(resultsKey, createQuery(classification, config.getResultsTemplate(), config.isTopCategoryOnly(), message, context));
            break;
          }
          case NLClassifierStageConfig.RESPONSE: {
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
                    ((AppendableResponse) response.initialEntity).appendString(resultsKey, createQuery(classification, config.getResultsTemplate(), config.isTopCategoryOnly(), message, context));
                  } else {
                    log.error("Could not add '" + resultsKey + "' to the Fusion response since it is not appendable");
                  }
                }
                return input;
              }
            });
            break;
          }
          case NLClassifierStageConfig.CONTEXT: {
            context.setProperty(resultsKey, classification);
            break;
          }
          default: {
            throw new RuntimeException("Invalid results location: " + resultsLocation);
          }
        }
      }
    } else {
      throw new Exception("No input content available to classify stored at location " + inputKey + " in the " + inputLoc);
    }
    return result;
  }


  private String createQuery(Classification classification, String resultsTemplate, Boolean topCategoryOnly, QueryRequestAndResponse reqResp, Context context) {
    StringBuilder result = new StringBuilder();
    if (resultsTemplate == null) {
      if (topCategoryOnly) {
        result.append(classification.getTopClass());
      } else {
        for (ClassifiedClass category : classification.getClasses()) {
          result.append(category.getName()).append(' ');
        }
      }
    } else {
      result.append(renderTemplate(resultsTemplate, reqResp, context, classification));
    }
    return result.toString().trim();
  }

  protected String renderTemplate(String templateStr, QueryRequestAndResponse reqResp, Context context, Classification classification) {
    ST template = new ST(templateStr);
    template.groupThatCreatedThisInstance.registerModelAdaptor(Request.class, new RequestAdaptor());
    //Ordering: request params, headers, context
    //put the context into the template too
    Map<String, Object> props = context.getProperties();
    for (Map.Entry<String, Object> entry : props.entrySet()) {
      template.add(escapeKey(entry.getKey()), entry.getValue());
    }
    template.add(CONTEXT_TEMPLATE_KEY, context);
    template.add(CLASSIFICATION_TEMPLATE_KEY, classification);
    //headers
    for (Map.Entry<String, List<String>> entry : reqResp.request.getHeaders().entrySet()) {
      addEntryToTemplate(template, entry);
    }
    //last: request params
    MultivaluedMap<String, String> params = reqResp.request.getParams();
    for (Map.Entry<String, List<String>> entry : params.entrySet()) {
      addEntryToTemplate(template, entry);
    }

    template.add(REQ_RESP_TEMPLATE_KEY, reqResp);
    template.add(REQUEST_TEMPLATE_KEY, reqResp.request);
    template.add(RESPONSE_TEMPLATE_KEY, reqResp.response);

    return template.render();
  }

  private void addEntryToTemplate(ST template, Map.Entry<String, List<String>> entry) {
    if (entry.getValue().isEmpty() == false) {
      for (String val : entry.getValue()) {
        template.add(escapeKey(entry.getKey()), val);
      }
    }
  }


  static class RequestAdaptor implements ModelAdaptor {
    @Override
    public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
      Request request = (Request) o;
      Object result = null;
      if (propertyName.equals("httpMethod")) {
        result = request.getHttpMethod();
      } else if (propertyName.equals("params")) {
        result = request.getParams();
      } else {
        result = request.getParam(propertyName);
      }
      return result;
    }
  }


}