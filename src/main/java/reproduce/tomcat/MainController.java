package reproduce.tomcat;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;

@RestController
public class MainController {

    public static class Data {
        private String a;

        public Data() {
        }

        public Data(String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }

    @PostMapping(value = "/streamed", consumes = APPLICATION_STREAM_JSON_VALUE)
    public Mono<Void> streamed(@RequestBody Publisher<Data> strings) {
        return Flux
                .from(strings)
                .log("streamedBefore")
                .subscribeOn(Schedulers.elastic())
                .map(x -> x + "1")
                .log("streamed")
                .then();
    }

    @PostMapping(value = "/notStreamed", consumes = APPLICATION_JSON_VALUE)
    public Mono<Void> notStreamed(@RequestBody Publisher<Data> strings) {
        return Flux
                .from(strings)
                .log("notStreamedBefore")
                .subscribeOn(Schedulers.elastic())
                .map(x -> x + "1")
                .log("notStreamed")
                .then();
    }


    @PostMapping(value = "/notStreamedMono", consumes = APPLICATION_JSON_VALUE)
    public Mono<Void> notStreamedMono(@RequestBody Mono<List<Data>> strings) {
        return strings.flatMapIterable(list -> list)
                .log("notStreamedMonoBefore")
                .subscribeOn(Schedulers.elastic())
                .map(x -> x + "1")
                .log("notStreamedMono")
                .then();
    }
}
