package examples.example2;

import org.apache.camel.main.Main;

import java.util.Arrays;

/**
 * @author Adam Ostrozlik
 * @version 1.0
 * @since 06.10.2018
 **/
public class CamelRunner {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.setRouteBuilders(Arrays.asList(
                new Example2RouteBuilder()
        ));
        main.run();
    }
}
