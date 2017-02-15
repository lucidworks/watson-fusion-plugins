package com.lucidworks.apollo.pipeline.query.stages;


import com.lucidworks.apollo.common.pipeline.PipelineField;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 **/
public class RelExtractionParserTest {
  @Test
  public void testBasics() throws Exception {

    PipelineField originalField = new PipelineField("input", "Patrick Ryan was hired by IBM in 2004. He works in New York.");
    List<PipelineField> mentions = new ArrayList<>();
    List<PipelineField> entities = new ArrayList<>();
    List<PipelineField> relations = new ArrayList<>();
    RelExtractionParser.parseWatsonResponse("foo", EXAMPLE_OUTPUT_XML, originalField, mentions, entities, relations);
    System.out.println(mentions);
    Assert.assertEquals(6, mentions.size());
    System.out.println(entities);
    Assert.assertEquals(6, entities.size());
    System.out.println(relations);
    Assert.assertEquals(5, relations.size());
  }

  //http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/sireapi/output.shtml
  public static final String EXAMPLE_OUTPUT_XML = "<?xml version='1.0' encoding='utf-8'?> <rep sts=\"OK\"> <doc id=\"test\"> " +
          "<text>Patrick Ryan was hired by IBM in 2004. He works in New York. </text> <sents> <sent sid=\"0\" begin=\"0\" end=\"8\">" +
          " <text>Patrick Ryan was hired by IBM in 2004.</text> <parse score=\"-1.47891\">[S [NP Patrick_NNP Ryan_NNP NP] [VP was_VBD " +
          "[VP hired_VBN [PP by_IN [NP IBM_NNP NP] PP] [PP in_IN [NP 2004_CD NP] PP] VP] VP] ._. S]</parse> <dependency_parse>Patrick NNP " +
          "1 -I Ryan NNP 2 NP was VBD -1 VP hired VBN 2 VP by IN 3 PP IBM NNP 4 NP in IN 3 PP 2004 CD 6 NP . . 2 -E </dependency_parse>" +
          " <usd_dependency_parse>Patrick NNP 1 dep Ryan NNP 2 npmod was VBD -1 root hired VBN 2 VP by IN 5 case IBM NNP 3 nmod in IN 7 " +
          "case 2004 CD 3 nmod . . 2 dep</usd_dependency_parse> <tokens> <token tid=\"0\" begin=\"0\" end=\"6\" type=\"0\">Patrick</token> " +
          "<token tid=\"1\" begin=\"8\" end=\"11\" type=\"0\">Ryan</token> <token tid=\"2\" begin=\"13\" end=\"15\" type=\"0\">was</token> " +
          "<token tid=\"3\" begin=\"17\" end=\"21\" type=\"0\">hired</token> <token tid=\"4\" begin=\"23\" end=\"24\" type=\"0\">by</token>" +
          " <token tid=\"5\" begin=\"26\" end=\"28\" type=\"0\">IBM</token> <token tid=\"6\" begin=\"30\" end=\"31\" type=\"0\">in</token>" +
          " <token tid=\"7\" begin=\"33\" end=\"36\" type=\"0\">2004</token> <token tid=\"8\" begin=\"37\" end=\"37\" type=\"6144\">.</token> " +
          "</tokens> </sent> <sent sid=\"1\" begin=\"9\" end=\"14\"> <text>He works in New York.</text> <parse score=\"-0.408826\">" +
          "[S [NP He_PRP NP] [VP works_VBZ [PP in_IN [NP New_NNP York_NNP NP] PP] VP] ._. S]</parse> <dependency_parse>He PRP 1 NP works " +
          "VBZ -1 VP in IN 1 PP New NNP 4 -I York NNP 2 NP . . 1 -E </dependency_parse> <usd_dependency_parse>He PRP 1 npmod works VBZ -1 " +
          "root in IN 4 case New NNP 4 dep York NNP 1 nmod . . 1 dep</usd_dependency_parse> <tokens> <token tid=\"9\" begin=\"39\" " +
          "end=\"40\" type=\"0\">He</token> <token tid=\"10\" begin=\"42\" end=\"46\" type=\"0\">works</token> <token tid=\"11\" " +
          "begin=\"48\" end=\"49\" type=\"0\">in</token> <token tid=\"12\" begin=\"51\" end=\"53\" type=\"0\">New</token> <token tid=\"13\" " +
          "begin=\"55\" end=\"58\" type=\"0\">York</token> <token tid=\"14\" begin=\"59\" end=\"59\" type=\"6144\">.</token> </tokens>" +
          " </sent> </sents> <mentions> <mention mid=\"test-M0\" mtype=\"NAM\" begin=\"0\" end=\"11\" head-begin=\"0\" head-end=\"11\" " +
          "eid=\"test-E0\" etype=\"PERSON\" role=\"PERSON\" metonymy=\"0\" class=\"SPC\" score=\"0.975165\" corefScore=\"1\">" +
          "Patrick Ryan</mention> <mention mid=\"test-M1\" mtype=\"NONE\" begin=\"17\" end=\"21\" head-begin=\"17\" head-end=\"21\" " +
          "eid=\"test-E3\" etype=\"EVENT_PERSONNEL\" role=\"EVENT_PERSONNEL\" metonymy=\"0\" class=\"SPC\" score=\"0.972867\" " +
          "corefScore=\"1\">hired</mention> <mention mid=\"test-M2\" mtype=\"NAM\" begin=\"26\" end=\"28\" head-begin=\"26\"" +
          " head-end=\"28\" eid=\"test-E1\" etype=\"ORGANIZATION\" role=\"ORGANIZATION\" metonymy=\"0\" class=\"SPC\" score=\"0.948638\" " +
          "corefScore=\"1\">IBM</mention> <mention mid=\"test-M3\" mtype=\"NONE\" begin=\"33\" end=\"36\" head-begin=\"33\"" +
          " head-end=\"36\" eid=\"test-E4\" etype=\"DATE\" role=\"DATE\" metonymy=\"0\" class=\"SPC\" score=\"0.922923\" " +
          "corefScore=\"1\">2004</mention> <mention mid=\"test-M4\" mtype=\"PRO\" begin=\"39\" end=\"40\" head-begin=\"39\" " +
          "head-end=\"40\" eid=\"test-E0\" etype=\"PERSON\" role=\"PERSON\" metonymy=\"0\" class=\"SPC\" score=\"0.992041\" " +
          "corefScore=\"0.821132\">He</mention> <mention mid=\"test-M5\" mtype=\"NAM\" begin=\"51\" end=\"58\" head-begin=\"51\" " +
          "head-end=\"58\" eid=\"test-E2\" etype=\"GPE\" role=\"LOCATION\" metonymy=\"0\" class=\"SPC\" score=\"0.969694\" " +
          "corefScore=\"1\">New York</mention> </mentions> <entities> <entity eid=\"test-E0\" type=\"PERSON\" generic=\"0\" " +
          "class=\"SPC\" level=\"NAM\" subtype=\"OTHER\" score=\"0.906163\"> <mentref mid=\"test-M0\">Patrick Ryan</mentref> " +
          "<mentref mid=\"test-M4\">He</mentref> </entity> <entity eid=\"test-E1\" type=\"ORGANIZATION\" generic=\"0\" " +
          "class=\"SPC\" level=\"NAM\" subtype=\"COMMERCIAL\" score=\"1\"> <mentref mid=\"test-M2\">IBM</mentref> </entity> " +
          "<entity eid=\"test-E2\" type=\"GPE\" generic=\"0\" class=\"SPC\" level=\"NAM\" subtype=\"OTHER\" score=\"1\"> " +
          "<mentref mid=\"test-M5\">New York</mentref> </entity> <entity eid=\"test-E3\" type=\"EVENT_PERSONNEL\" " +
          "generic=\"0\" class=\"SPC\" level=\"NONE\" subtype=\"OTHER\" score=\"1\"> <mentref mid=\"test-M1\">hired</mentref> </entity> " +
          "<entity eid=\"test-E4\" type=\"DATE\" generic=\"0\" class=\"SPC\" level=\"NONE\" subtype=\"OTHER\" score=\"1\"> " +
          "<mentref mid=\"test-M3\">2004</mentref> </entity> </entities> <relations version=\"KLUE2_cascaded:2011_10_25\"> " +
          "<relation rid=\"test-R1\" type=\"affectedBy\" subtype=\"OTHER\"> <rel_entity_arg eid=\"test-E0\" argnum=\"1\"/> " +
          "<rel_entity_arg eid=\"test-E3\" argnum=\"2\"/> <relmentions> <relmention rmid=\"test-R1-1\" score=\"0.981645\" " +
          "class=\"SPECIFIC\" modality=\"ASSERTED\" tense=\"UNSPECIFIED\"> <rel_mention_arg mid=\"test-M0\" argnum=\"1\">" +
          "Patrick Ryan</rel_mention_arg> <rel_mention_arg mid=\"test-M1\" argnum=\"2\">hired</rel_mention_arg> </relmention> </relmentions> " +
          "</relation> <relation rid=\"test-R2\" type=\"employedBy\" subtype=\"OTHER\"> <rel_entity_arg eid=\"test-E0\" argnum=\"1\"/> " +
          "<rel_entity_arg eid=\"test-E1\" argnum=\"2\"/> <relmentions> <relmention rmid=\"test-R2-1\" score=\"0.462538\" " +
          "class=\"SPECIFIC\" modality=\"ASSERTED\" tense=\"UNSPECIFIED\"> <rel_mention_arg mid=\"test-M0\" argnum=\"1\">Patrick Ryan" +
          "</rel_mention_arg> <rel_mention_arg mid=\"test-M2\" argnum=\"2\">IBM</rel_mention_arg> </relmention> </relmentions> </relation> " +
          "<relation rid=\"test-R3\" type=\"agentOf\" subtype=\"OTHER\"> <rel_entity_arg eid=\"test-E1\" argnum=\"1\"/> " +
          "<rel_entity_arg eid=\"test-E3\" argnum=\"2\"/> <relmentions> <relmention rmid=\"test-R3-1\" score=\"0.922564\" " +
          "class=\"SPECIFIC\" modality=\"ASSERTED\" tense=\"UNSPECIFIED\"> <rel_mention_arg mid=\"test-M2\" argnum=\"1\">IBM</rel_mention_arg> " +
          "<rel_mention_arg mid=\"test-M1\" argnum=\"2\">hired</rel_mention_arg> </relmention> </relmentions> </relation> " +
          "<relation rid=\"test-R4\" type=\"timeOf\" subtype=\"OTHER\"> <rel_entity_arg eid=\"test-E4\" argnum=\"1\"/> " +
          "<rel_entity_arg eid=\"test-E3\" argnum=\"2\"/> <relmentions> <relmention rmid=\"test-R4-1\" score=\"0.991814\" " +
          "class=\"SPECIFIC\" modality=\"ASSERTED\" tense=\"UNSPECIFIED\"> <rel_mention_arg mid=\"test-M3\" argnum=\"1\">2004</rel_mention_arg>" +
          " <rel_mention_arg mid=\"test-M1\" argnum=\"2\">hired</rel_mention_arg> </relmention> </relmentions> </relation> " +
          "<relation rid=\"test-R5\" type=\"locatedAt\" subtype=\"OTHER\"> <rel_entity_arg eid=\"test-E0\" argnum=\"1\"/> " +
          "<rel_entity_arg eid=\"test-E2\" argnum=\"2\"/> <relmentions> <relmention rmid=\"test-R5-1\" score=\"0.513522\" " +
          "class=\"SPECIFIC\" modality=\"ASSERTED\" tense=\"UNSPECIFIED\"> <rel_mention_arg mid=\"test-M4\" argnum=\"1\">He<" +
          "/rel_mention_arg> <rel_mention_arg mid=\"test-M5\" argnum=\"2\">New York</rel_mention_arg> </relmention> </relmentions>" +
          " </relation> </relations> <sgml_sent_info/> <sgml_char_info/> </doc></rep>";

}