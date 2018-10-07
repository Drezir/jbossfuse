import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

/**
 * @author Adam Ostrozlik
 * @version 1.0
 * @since 07.10.2018
 **/
public class BlueprintTest extends CamelBlueprintTestSupport {
    // TODO Create test message bodies that work for the route(s) being tested
    // Expected message bodies
    protected String body1;
    protected String body2;
    protected String body3;
    protected String body4;
    protected String body5;
    protected String body6;
    // Templates to send to input endpoints
    @Produce(uri = "file:src/data?noop=true")
    protected ProducerTemplate inputEndpoint;
    @Produce(uri = "seda:orderFulfillment")
    protected ProducerTemplate input2Endpoint;
    // Mock endpoints used to consume messages from the output endpoints and then perform assertions
    @EndpointInject(uri = "mock:output")
    protected MockEndpoint outputEndpoint;
    @EndpointInject(uri = "mock:output2")
    protected MockEndpoint output2Endpoint;
    @EndpointInject(uri = "mock:output3")
    protected MockEndpoint output3Endpoint;
    @EndpointInject(uri = "mock:output4")
    protected MockEndpoint output4Endpoint;

    @Test
    public void testCamelRoute() throws Exception {
        // Create routes from the output endpoints to our mock endpoints so we can assert expectations
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Valid orders
                body2 = FileUtils.readFileToString(new
                        File("src/data/message2.xml"), "UTF-8");
                body4 = FileUtils.readFileToString(new
                        File("src/data/message4.xml"), "UTF-8");
                body5 = FileUtils.readFileToString(new
                        File("src/data/message5.xml"), "UTF-8");
                body6 = FileUtils.readFileToString(new
                        File("src/data/message6.xml"), "UTF-8");
                // Invalid orders
                body1 = FileUtils.readFileToString(new
                        File("src/data/message1.xml"), "UTF-8");
                body3 = FileUtils.readFileToString(new
                        File("src/data/message3.xml"), "UTF-8");

                from("file:target/messages/invalidOrders").to(outputEndpoint);
                from("file:target/messages/validOrders/USA").to(output3Endpoint);
                from("file:target/messages/validOrders/Germany").to(output4Endpoint);
                from("seda:orderFulfillment").to(output2Endpoint);
            }
        });

        // Define some expectations
        // TODO Ensure expectations make sense for the route(s) we're testing
        // Invalid orders
        outputEndpoint.expectedBodiesReceived(body1, body3);
        // Valid orders for USA
        output3Endpoint.expectedBodiesReceived(body2, body5, body6);
        // Valid order for Germany
        output4Endpoint.expectedBodiesReceived(body4);
        // Validate our expectations
        assertMockEndpointsSatisfied();
    }

    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/blueprint1.xml";
    }
}
