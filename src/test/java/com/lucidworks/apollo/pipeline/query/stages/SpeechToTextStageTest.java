package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/
public class SpeechToTextStageTest {
  /*private static Logger log = LoggerFactory.getLogger(SpeechToTextStageTest.class);

  private WireMockServer wireMockServer;

  @BeforeClass
  public void setup() {
    WireMockConfiguration wireMockConfig = new WireMockConfiguration();
    int port = TestHelper.getRandomPort();
    wireMockConfig.port(port);
    wireMockServer = new WireMockServer(port);
    WireMock.configureFor(port);
    wireMockServer.start();
  }

  @AfterClass
  public void tearDown() {
    WireMock.reset();
    wireMockServer.stop();
  }
  //Happy path tests
  //Example Curl: curl -v -u S2T_USER:S2T_PASS --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../data/0001.flac "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
  @Test(groups = "unit")
  public void testRequest() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    System.out.println("Watson Mock Endpoint" + url + " running: " + wireMockServer.isRunning());
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV());
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the "best" string
    Assert.assertEquals(testBasicsKey.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
  }


  //Get back the whole Transcript object
  @Test(groups = "unit")
  public void testTranscriptObject() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config;
    PipelineContext context;
    config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", false, url);
    context = new PipelineContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV());
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    Assert.assertTrue(testBasicsKey instanceof String);

    config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", false, url);
    context = new PipelineContext();
    reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    Assert.assertTrue(testBasicsKey instanceof SpeechResults);

  }

  @Test(groups = "unit")
  public void testContext() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV());
    stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the "best" string
    Assert.assertEquals(testBasicsKey.toString(), "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday");
  }

  @Test(groups = "unit")
  public void testResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.RESPONSE,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    //set an existing that adds it's own output
    context.setSharedProperty("responseTransformer", new Function<QueryRequestAndResponse, QueryRequestAndResponse>() {
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

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV());
    stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
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
    Function<QueryRequestAndResponse, QueryRequestAndResponse> transformer = context.getProperty(PipelineContext.RESPONSE_TRANSFORMER, Function.class);
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
  @Test(groups = "unit")
  public void testBadInput() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(BAD_RESPONSE_400)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray());
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (BadRequestException e) {
      //expected
    }
  }

  @Test(groups = "unit")
  public void testEmptyResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.EMPTY_RESPONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray());
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)){
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testBadResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray());
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof ProtocolException)){
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testRandomResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray());
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)){
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testBadEndpointAvailable() throws Exception {
    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://foo:65543";
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.REQUEST,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
    //Try: curl  -u user:pass --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../../zk-client.tmproj "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
    baos.write("this is not an audio file and we don't care about the encoding of the bytes".getBytes());

    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", baos.toByteArray());
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  @Test(groups = "unit")
  public void testDelayedResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/recognize"))
            .willReturn(aResponse()
                    .withStatus(200).withFixedDelay(1000)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    SpeechToTextStage stage = new SpeechToTextStage();
    String url = "http://localhost:" + wireMockServer.port();
    SpeechToTextStageConfig config = new SpeechToTextStageConfig("testBasics", "foo", "bar", SpeechToTextStageConfig.CONTEXT,
            "testBasicsKey", true, url);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET", getTestWAV());
    stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
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
          "}";*/
}