package io.swagger.parser;

import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.util.RemoteUrl;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RelativeReferenceTest {
    @Mocked
    RemoteUrl remoteUrl;

    static final String spec =
            "swagger: '2.0'\n" +
            "schemes:\n" +
            "  - https\n" +
            "basePath: /\n" +
            "produces:\n" +
            "  - application/json\n" +
            "info:\n" +
            "  title: My API\n" +
            "  description: It works.\n" +
            "  version: '1.0.0'\n" +
            "paths:\n" +
            "  /samplePath:\n" +
            "    $ref: './path/samplePath.yaml'";
    static final String samplePath =
            "get:\n" +
            "  parameters:\n" +
            "    - name: data\n" +
            "      in: body\n" +
            "      required: true\n" +
            "      schema:\n" +
            "        type: object\n" +
            "  responses:\n" +
            "    200:\n" +
            "      description: It works";

    @Test
    public void testIssue213() throws Exception {
        new Expectations() {{
            RemoteUrl.urlToString("http://foo.bar.com/swagger.json", null);
            times = 1;
            result = spec;

            RemoteUrl.urlToString("http://foo.bar.com/path/samplePath.yaml", null);
            times = 1;
            result = samplePath;
        }};

        Swagger swagger = new SwaggerParser().read("http://foo.bar.com/swagger.json");

        assertNotNull(swagger.getPath("/samplePath").getGet());
        assertNotNull(swagger.getPath("/samplePath").getGet().getParameters().get(0));
        Parameter param = swagger.getPath("/samplePath").getGet().getParameters().get(0);
        assertTrue(param instanceof BodyParameter);

        BodyParameter bp = (BodyParameter) param;
        assertNotNull(bp.getSchema());
    }
}
