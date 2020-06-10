package baoying.eval.spring.boot;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//更多例子，参考 https://spring.io/guides/tutorials/rest/
@RestController
public class HelloController {

    @RequestMapping("/")
    //@RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/echoPost")
    public String echoPost(@RequestBody String postBody) {
        return postBody;
    }

    //https://spring.io/guides/gs/actuator-service/
    //e.g. http://localhost:8080/sayHello?name=baoying
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    @GetMapping("/sayHello")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        final Greeting greeting = new Greeting(counter.incrementAndGet(), String.format(template, name));
        return greeting;
    }

    class Greeting {

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
