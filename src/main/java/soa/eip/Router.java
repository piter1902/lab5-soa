package soa.eip;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

    public static final String DIRECT_URI = "direct:twitter";

    @Override
    public void configure() {
        from(DIRECT_URI)
                .log("Body contains \"${body}\"")
                .log("Searching twitter for \"${body}\"!")
                .process(exchange -> {
                    // Extract max:n
                    Message in = exchange.getIn();
                    String body = in.getBody(String.class);
                    // max keyword should be at the end of the query
                    if (body.matches("^[a-zA-Z0-9 ]+ max:[0-9]+$")) {
                        // Contains max keyword
                        // Split by : -> "xxxxx max:3" --> {"xxxxx max", "3"}
                        int count = Integer.parseInt(body.split(":")[1]);
                        in.setHeader("count", count);
                        body = body.replace("max:" + count, "");
                        in.setBody(body);
                    } else {
                        // Default value = 5
                        in.setHeader("count", 5);
                    }
                })
                .toD("twitter-search:${body}?count=${header.count}")
                .log("Body now contains the response from twitter:\n${body}");
    }
}
