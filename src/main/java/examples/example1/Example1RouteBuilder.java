package examples.example1;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author Adam Ostrozlik
 * @version 1.0
 * @since 06.10.2018
 **/
public class Example1RouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        from("file:src/data?noop=true")
                .to("file:target/messages/received");
    }
}
