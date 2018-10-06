package examples.example2;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Ostrozlik
 * @version 1.0
 * @since 06.10.2018
 **/
public class Example2RouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Example2RouteBuilder.class);

    public void configure() throws Exception {
        final String orderQuantityXPath = "/order/orderline/quantity/text()";
        final String orderCustomerCountryXPath = "/order/customer/country/text()";

        from("file:src/data?noop=true")
                .to("seda:fileProcessor");

        from("seda:fileProcessor?concurrentConsumers=10")
                .choice()
                .when().xpath(String.format("%s >= 10", orderQuantityXPath))
                    .log(LoggingLevel.WARN, logger, "Quantity exceeded standard limit of 10")
                    .setHeader("Destination", constant("Invalid"))
                    .to("file:target/messages/invalidOrders")
                .otherwise()
                    .log(LoggingLevel.INFO, logger, "This is valid order")
                    .setHeader("Destination", constant("Valid"))
                    .to("seda:orderFulfillment")
                .end();

        from("seda:orderFulfillment?concurrentConsumers=10")
                .setHeader("country", xpath(orderCustomerCountryXPath))
                .recipientList(simple("file:target/messages/country/${header.country}"))
                .removeHeader("country");

    }
}
