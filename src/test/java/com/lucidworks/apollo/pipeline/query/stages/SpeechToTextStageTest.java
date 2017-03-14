package com.lucidworks.apollo.pipeline.query.stages;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import com.google.common.base.Function;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.impl.DefaultContext;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.Response;
import com.lucidworks.apollo.rest.ExtraMediaTypes;
import com.lucidworks.apollo.rest.RequestParams;
import com.lucidworks.apollo.solr.response.AppendableResponse;
import com.lucidworks.apollo.solr.response.JSONResponse;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 *
 *
 **/
public class SpeechToTextStageTest {
  private static Logger log = LoggerFactory.getLogger(SpeechToTextStageTest.class);

  private WireMockServer wireMockServer;

  @Before
  public void setup() {
    WireMockConfiguration wireMockConfig = new WireMockConfiguration();
    int port = TestHelper.getRandomPort();
    wireMockConfig.port(port);
    wireMockServer = new WireMockServer(port);
    WireMock.configureFor(port);
    wireMockServer.start();
  }

  @After
  public void tearDown() {
    WireMock.reset();
    wireMockServer.stop();
  }

  //Happy path tests
  //Example Curl: curl -v -u S2T_USER:S2T_PASS --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../data/0001.flac "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
  @Test
  public void testRequest() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    System.out.println("Watson Mock Endpoint" + url + " running: " + wireMockServer.isRunning());
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    QueryRequestAndResponse reqRsp = stage.process(msg, context);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the "best" string
    Assert.assertEquals(testBasicsKey.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
  }


  //Get back the whole Transcript object
  @Test
  public void testTranscriptObject() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config;
    config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", false, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    QueryRequestAndResponse reqRsp = stage.process(msg, context);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    Assert.assertTrue(testBasicsKey instanceof String);

    config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", false, url);
    context = DefaultContext.newContext();
    stage = new SpeechToTextStage(TestHelper.newParams(config));
    reqRsp = stage.process(msg, context);
    testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    Assert.assertTrue(testBasicsKey instanceof SpeechResults);

  }

  @Test
  public void testContext() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    stage.process(msg, context);
    Object testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the "best" string
    Assert.assertEquals(testBasicsKey.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
  }

  @Test
  public void testResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.RESPONSE,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    //set an existing that adds it's own output
    context.setShared("responseTransformer", new Function<QueryRequestAndResponse, QueryRequestAndResponse>() {
      @Override
      public QueryRequestAndResponse apply(QueryRequestAndResponse input) {
        if (input == null) {
          return null;
        }
        if (input.response.isPresent()) {
          Response response = input.response.get();
          if (response.initialEntity instanceof AppendableResponse) {
            ((AppendableResponse) response.initialEntity).appendString("foo", "bar");
          }
        }
        return input;
      }
    });
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    stage.process(msg, context);
    // Given a fake JSON response here, add the landing pages to it
    JSONResponse resp = JSONResponse.create("{\n" +
            "  \"responseHeader\":{\n" +
            "    \"status\":0,\n" +
            "    \"QTime\":4,\n" +
            "    \"params\":{\n" +
            "      \"indent\":\"true\",\n" +
            "      \"q\":\"*:*\",\n" +
            "      \"wt\":\"json\"}},\n" +
            "  \"response\":{\"numFound\":0,\"start\":0,\"maxScore\":0.0,\"docs\":[]\n" +
            "  }}");
    msg = msg.withResponse(new MultivaluedHashMap<String, String>(), resp, -1);
    Function<QueryRequestAndResponse, QueryRequestAndResponse> transformer = context.getProperty(Context.RESPONSE_TRANSFORMER, Function.class);
    msg = transformer.apply(msg);
    JSONResponse finalResponse = (JSONResponse) msg.response.get().initialEntity;
    Assert.assertNotNull(finalResponse);
    //we are expecting the "best" string
    String jxpath = String.format("/%s/%s",
            AppendableResponse.FUSION_NODE,
            "testBasicsKey");
    Object trans = finalResponse.query(jxpath).get();
    Assert.assertEquals(trans.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
    jxpath = String.format("/%s/%s",
            AppendableResponse.FUSION_NODE,
            "foo");
    trans = finalResponse.query(jxpath).get();
    Assert.assertEquals(trans.toString(), "bar");
  }

  //Sad path tests
  @Test
  public void testBadInput() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(BAD_RESPONSE_400)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    try {
      QueryRequestAndResponse reqRsp = stage.process(msg, context);
      Assert.fail();
    } catch (BadRequestException e) {
      //expected
    }
  }

  @Test
  public void testEmptyResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.EMPTY_RESPONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    try {
      QueryRequestAndResponse reqRsp = stage.process(msg, context);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)) {
        Assert.fail();
      }
    }
  }

  @Test
  public void testBadResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    try {
      QueryRequestAndResponse reqRsp = stage.process(msg, context);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof ProtocolException)) {
        Assert.fail();
      }
    }
  }

  @Test
  public void testRandomResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    try {
      QueryRequestAndResponse reqRsp = stage.process(msg, context);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)) {
        Assert.fail();
      }
    }
  }

  @Test
  public void testBadEndpointAvailable() throws Exception {
    String url = "http://foo:65543";
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    try {
      QueryRequestAndResponse reqRsp = stage.process(msg, context);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  @Test
  public void testDelayedResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200).withFixedDelay(1000)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));


    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", true, url);
    SpeechToTextStage stage = new SpeechToTextStage(TestHelper.newParams(config));
    Context context = DefaultContext.newContext();
    RequestParams params = new RequestParams();
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV(), ExtraMediaTypes.AUDIO_WAV_TYPE);
    stage.process(msg, context);
    Object testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the "best" string
    Assert.assertEquals(testBasicsKey.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
  }


  protected byte[] getTestWAV() throws IOException {

    InputStream stream = null;
    try {//we don't care about the actual message, since we are using Wiremock
      stream = SpeechToTextStageTest.class.getClassLoader().getResourceAsStream("testspeech.wav");
      return IOUtils.toByteArray(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  // Obtained by doing:
  // curl -u WATSON_SPEECH_TO_TEXT_USER_KEY:WATSON_SPEECH_TO_TEXT_PASS --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../data/0001.flac "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
  public static final String WATSON_BASIC_RESONSE = "{\n" +
          "   \"results\": [\n" +
          "      {\n" +
          "         \"alternatives\": [\n" +
          "            {\n" +
          "               \"confidence\": 0.885, \n" +
          "               \"transcript\": \"several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday \"\n" +
          "            }\n" +
          "         ], \n" +
          "         \"final\": true\n" +
          "      }\n" +
          "   ], \n" +
          "   \"result_index\": 0\n" +
          "}";

  public static final String BAD_RESPONSE_400 = "{\n" +
          "   \"code_description\": \"Bad Request\", \n" +
          "   \"code\": 400, \n" +
          "   \"error\": \"unable to transcode data stream audio/flac -> audio/x-float-array \"\n" +
          "}";
}