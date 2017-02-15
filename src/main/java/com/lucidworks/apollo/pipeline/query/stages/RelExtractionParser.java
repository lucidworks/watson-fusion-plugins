package com.lucidworks.apollo.pipeline.query.stages;


import com.lucidworks.apollo.common.pipeline.Annotation;
import com.lucidworks.apollo.common.pipeline.PipelineField;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 **/
public class RelExtractionParser {
  /**
   * Parse the Watson Relationship Extraction response, updating the original field where appropriate and adding new items to the appropriate list.
   * @param watsonResponse
   * @param originalField
   * @param mentions must not be null
   * @param entities must not be null
   * @param relations must not be null
   * @throws XMLStreamException
   */
  public static void parseWatsonResponse(String target, String watsonResponse, PipelineField originalField, List<PipelineField> mentions,
                                         List<PipelineField> entities, List<PipelineField> relations) throws XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader parser = factory.createXMLStreamReader(new StringReader(watsonResponse));
    for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
      switch (event) {
        case XMLStreamConstants.START_ELEMENT: {
          switch (parser.getLocalName()) {
            case "mention": {
              Map<String, String> features = new HashMap<>(parser.getAttributeCount());
              attributesToMap(parser, "", features);//we don't need to distinguish feature keys here, since we are only adding one annotation
              //should we get the head begin/end?
              long begin = getLong(parser.getAttributeValue(null, "begin"));
              long end = getLong(parser.getAttributeValue(null, "end"));

              //get the characters, which are required according the IBM schema: http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/sireapi/tags.shtml
              int charEvent = parser.next();
              if (charEvent == XMLStreamConstants.CHARACTERS) {
                String text = parser.getText();
                if (text != null && text.isEmpty() == false) {
                  features.put("mentionText", text);
                  mentions.add(new PipelineField(target + ".mention", text));
                }
              }
              Annotation mention = new Annotation("mention", begin, end,
                      features);
              originalField.addAnnotation(mention);
              break;
            }
            case "entity": {
              Map<String, String> features = new HashMap<>(parser.getAttributeCount());
              attributesToMap(parser, "entity.", features);
              String fldName = target + ".entity." + parser.getAttributeValue(null, "type");
              String subType = parser.getAttributeValue(null, "subtype");
              if (subType != null && subType.isEmpty() == false) {
                fldName += '_' + subType;
              }
              //put type_subtype as the field name, put the mentref as the value.  Everything else as annotations
              boolean hasMore = parser.hasNext();
              while (hasMore) {
                //get the mentrefs
                int charEvent = parser.next();
                if (charEvent == XMLStreamConstants.START_ELEMENT) {
                  if (parser.getLocalName().equals("mentref")) {
                    //get the attributes of the mention, which is mainly the mention id
                    attributesToMap(parser, "mentref.", features);
                    //now get the actual text
                    charEvent = parser.next();
                    if (charEvent == XMLStreamConstants.CHARACTERS) {
                      String text = parser.getText();
                      if (text != null && text.isEmpty() == false) {
                        PipelineField fld = new PipelineField(fldName, text);

                        fld.setMetadata(features);
                        entities.add(fld);
                      }
                    }
                  }
                } else if (charEvent == XMLStreamConstants.END_ELEMENT) {
                  if (parser.getLocalName().equals("entity")) {
                    hasMore = false;
                  }
                }
                if (hasMore){//check to see that we can still parse the file, regardless of content
                  hasMore = parser.hasNext();
                }
              }
              break;
            }
            case "relation": {
              //we are somewhat flattening the structures here.  The alternative would be to have a diff. annotation for each tag in the relation hierarchy
              Map<String, String> features = new HashMap<>(parser.getAttributeCount());
              attributesToMap(parser, "relation.", features);
              String type = features.get("type");
              if (type != null && type.isEmpty() == false){
                String subType = features.get("subtype");
                String fldName = null;
                if (subType != null && subType.isEmpty() == false){
                  fldName = target + ".relation." + type + '_' + subType;
                } else {
                  fldName = target + ".relation." + type;
                }
                StringBuilder value = new StringBuilder();
                boolean hasMore = parser.hasNext();
                while (hasMore){
                  int relEvent = parser.next();
                  if (relEvent == XMLStreamConstants.START_ELEMENT){
                    if (parser.getLocalName().equals("rel_mention_arg")) {
                      attributesToMap(parser, "relMentionArg.", features);
                      //get the text
                      int charEvent = parser.next();
                      if (charEvent == XMLStreamConstants.CHARACTERS) {
                        String text = parser.getText();
                        value.append(text).append(' ');
                      }
                    } else if (parser.getLocalName().equals("relmention")) {
                      attributesToMap(parser, "relMention.", features);

                    } else if (parser.getLocalName().equals("rel_entity_arg")) {
                      attributesToMap(parser, "relEntityArg.", features);

                    }
                  } else if (relEvent == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals("relation")) {
                      hasMore = false;
                    }
                  }
                }
                if (value.length() > 0) {
                  PipelineField relationField = new PipelineField(fldName, value.toString().trim());
                  relationField.setMetadata(features);
                  relations.add(relationField);
                }
              }
              break;
            }
          }
          break;
        }
        case XMLStreamConstants.END_ELEMENT: {
          switch (parser.getLocalName()) {
            case "mention": {
              break;
            }
            case "entity": {
              break;
            }
            case "mentref": {
              break;
            }
            case "relation": {
              break;
            }
            case "relmention": {
              break;
            }
            case "rel_mention_arg": {
              break;
            }
          }
          break;
        }
        case XMLStreamConstants.CHARACTERS: {
          break;
        }
      }
    }
    parser.close();
  }

  protected static void attributesToMap(XMLStreamReader parser, String keyPrefix, Map<String, String> features) {
    if (keyPrefix != null) {
      keyPrefix = "";
    }
    for (int i = 0; i < parser.getAttributeCount(); i++) {
      features.put(keyPrefix + parser.getAttributeLocalName(i), parser.getAttributeValue(i));
    }
  }

  private static long getLong(String attrib) {
    if (attrib != null) {
      return Long.getLong(attrib, -1);
    }
    return -1;
  }
}