package reproduce.tomcat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reproduce.tomcat.MainController.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class MainControllerTest {

    @Autowired
    WebTestClient client;


    @Test
    public void streamed() {
        client
                .post()
                .uri("/streamed")
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(Flux.just(new Data("1"), new Data("2")).log("streamedTest"), Data.class)
                .exchange();
    }

    @Test
    public void notStreamed() {
        client
                .post()
                .uri("/notStreamed")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(new Data("1"), new Data("2")).log("notStreamedTest"), Data.class)
                .exchange();
    }

    @Test
    public void notStreamedMono() {
        client
                .post()
                .uri("/notStreamedMono")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        BodyInserters.fromPublisher(
                                Mono.just(Stream.of(new Data("1"), new Data("2")).collect(toList())).log("notStreamedMonoTest"),
                                new ParameterizedTypeReference<List<Data>>() {}
                        )
                )
                .exchange();
    }

}