package baoying.eval.spring.boot;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 更多例子，参考 https://spring.io/guides/tutorials/rest/
 */
@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/echoPost")
    public String echoPost(@RequestBody String postBody) {
        return postBody;
    }


    private static final String TEMPLATE = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     * https://spring.io/guides/gs/actuator-service/
     * e.g. http://localhost:8080/sayHello?name=baoying
     */
    @GetMapping("/sayHello")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        final Greeting greeting = new Greeting(counter.incrementAndGet(), String.format(TEMPLATE, name));
        return greeting;
    }

    static class Greeting {

        private final long id;
        private final String content;

        public Greeting(long id, String content) {
            this.id = id;
            this.content = content;
        }

        public long getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

    }
}
