package com.lucidworks.apollo.pipeline.query.stages;


/**
 *
 *
 **/
public class NLClassifierStageTest {
  /*private static Logger log = LoggerFactory.getLogger(NLClassifierStageTest.class);

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
  //Example Curl: curl -G -u "USER":"PASS"  "https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/563C46x20-nlc-2357/classify"  --data-urlencode "text=How hot will it be today?"
  @Test(groups = "unit")
  public void testRequest() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    System.out.println("Watson Mock Endpoint" + url + " running: " + wireMockServer.isRunning());
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.REQUEST, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertEquals(testBasicsKey.toString(), "temperature conditions");
  }

  @Test(groups = "unit")
  public void testClassifierIdInRequest() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST,
            "testBasicsKey", url, null,
            NLClassifierStageConfig.REQUEST, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("classifierId", "563C46x20-nlc-2357");
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertEquals(testBasicsKey.toString(), "temperature conditions");
  }

  @Test(groups = "unit")
  public void testTopContent() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.REQUEST, "q", null, true);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertEquals(testBasicsKey.toString(), "temperature");

  }

  @Test(groups = "unit")
  public void testTemplate() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.REQUEST, "q", "<classification.topConfidence>", false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertEquals(testBasicsKey.toString(), "0.9933098208398066");
  }

  @Test(groups = "unit")
  public void testContext() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.CONTEXT, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = context.getProperty("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertTrue(testBasicsKey instanceof Classification);
    Classification classification = (Classification) testBasicsKey;
    Assert.assertEquals(classification.getTopClass(), "temperature");
  }

  @Test(groups = "unit")
  public void testResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
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
    Assert.assertEquals(trans.toString(), "temperature conditions");
  }


  //Sad path tests


  @Test(groups = "unit")
  public void testNoClassifierId() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST,
            "testBasicsKey", url, null,
            NLClassifierStageConfig.REQUEST, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();

    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (Exception e) {

    }
  }

  @Test(groups = "unit")
  public void testBadInputLocation() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    //shouldn't really be possible given the input validation
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST, "testBasicsKey", url, "563C46x20-nlc-2357",
            "foo", "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (Exception e) {

    }

  }

  @Test(groups = "unit")
  public void testNoInput() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(BAD_RESPONSE_400)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (Exception e) {
      //expected
    }
  }


  @Test(groups = "unit")
  public void testBadInput() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(BAD_RESPONSE_400)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (BadRequestException e) {
      //expected
    }
  }

  @Test(groups = "unit")
  public void testEmptyResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withFault(Fault.EMPTY_RESPONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)) {
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testBadResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof ProtocolException)) {
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testRandomResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (RuntimeException e) {
      //expected
      if (!(e.getCause() instanceof IOException)) {
        Assert.fail();
      }
    }
  }

  @Test(groups = "unit")
  public void testBadEndpointAvailable() throws Exception {
    String url = "http://foo:65543";
    NLClassifierStage stage = new NLClassifierStage();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.RESPONSE, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.CONTEXT, "q", null, false);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    context.setProperty("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    try {
      QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  @Test(groups = "unit")
  public void testDelayedResponse() throws Exception {
    stubFor(post(urlPathMatching("/v1/classifiers/563C46x20-nlc-2357"))
            .willReturn(aResponse()
                    .withStatus(200).withFixedDelay(1000)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_TYPE.toString())
                    .withBody(WATSON_BASIC_RESONSE)));

    NLClassifierStage stage = new NLClassifierStage();
    String url = "http://localhost:" + wireMockServer.port();
    NLClassifierStageConfig config = new NLClassifierStageConfig("testBasics", "foo", "bar", NLClassifierStageConfig.REQUEST, "testBasicsKey", url, "563C46x20-nlc-2357",
            NLClassifierStageConfig.REQUEST, "q", null, true);
    PipelineContext context = new PipelineContext();
    RequestParams params = new RequestParams();
    params.add("q", "How hot will it be today?");
    QueryRequestAndResponse msg = QueryRequestAndResponse.newRequest(params, null, "GET");
    QueryRequestAndResponse reqRsp = stage.process(context, config, msg, QueryCamelPipeline.DEFAULT_CALLBACK);
    Object testBasicsKey = reqRsp.request.getFirstFieldValue("testBasicsKey");
    Assert.assertNotNull(testBasicsKey);
    //we are expecting the query string
    Assert.assertEquals(testBasicsKey.toString(), "temperature");
  }


  // Obtained by doing:
  // curl -u WATSON_SPEECH_TO_TEXT_USER_KEY:WATSON_SPEECH_TO_TEXT_PASS --header "Content-Type: audio/flac" --header "Transfer-Encoding: chunked" --data-binary @../data/0001.flac "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true"
  public static final String WATSON_BASIC_RESONSE = "{\n" +
          "  \"classifier_id\" : \"563C46x20-nlc-2357\",\n" +
          "  \"url\" : \"https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/563C46x20-nlc-2357\",\n" +
          "  \"text\" : \"How hot will it be today?\",\n" +
          "  \"top_class\" : \"temperature\",\n" +
          "  \"classes\" : [ {\n" +
          "    \"class_name\" : \"temperature\",\n" +
          "    \"confidence\" : 0.9933098208398066\n" +
          "  }, {\n" +
          "    \"class_name\" : \"conditions\",\n" +
          "    \"confidence\" : 0.006690179160193508\n" +
          "  } ]\n" +
          "}";

  public static final String BAD_RESPONSE_400 = "{\n" +
          "  \"code\" : 400,\n" +
          "  \"error\" : \"Missing text\",\n" +
          "  \"description\" : \"The required 'text' parameter is missing.\"\n" +
          "}";*/
}