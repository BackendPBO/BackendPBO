package org.han.webtest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WebtestApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        System.out.println("http://localhost:8080/h2-console");
        System.out.println("jbdc url di h2 : jdbc:h2:mem:rogerdb");

        Thread.sleep(Long.MAX_VALUE);
    }
}