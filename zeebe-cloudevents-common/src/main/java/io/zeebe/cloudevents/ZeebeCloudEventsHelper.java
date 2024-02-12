package io.zeebe.cloudevents;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// import io.zeebe.cloudevents.CloudEventsHelper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;


public class ZeebeCloudEventsHelper {

    private static final Logger log = LoggerFactory.getLogger(ZeebeCloudEventsHelper.class);

    private static ObjectMapper mapper = new ObjectMapper();

    /*
     * This method will parse an HTTP request (headers and body) and it will create a Zeebe Cloud Event, that means
     * a Cloud Event From cloudevents.io with a Zeebe Extension
     * If the Zeebe Extension is not present in the headers, it will return a base Cloud Event.
     */
    public static CloudEvent  parseZeebeCloudEventFromRequest(HttpHeaders headers, Object body){
        ZeebeCloudEventExtension zeebeCloudEventExtension =  createZeebeCloudEventExtension(headers);
        return internalParseCloudEventWithExtensionOrDefault(body, headers, zeebeCloudEventExtension);
    }

    private static ZeebeCloudEventExtension createZeebeCloudEventExtension(HttpHeaders headers) {
        ZeebeCloudEventExtension zeebeCloudEventExtension =  new ZeebeCloudEventExtension();
        zeebeCloudEventExtension.setCorrelationKey(headers.getFirst(ZeebeCloudEventExtension.CORRELATION_KEY));
        zeebeCloudEventExtension.setBpmnActivityId(headers.getFirst(ZeebeCloudEventExtension.BPMN_ACTIVITY_ID));
        zeebeCloudEventExtension.setBpmnActivityName(headers.getFirst(ZeebeCloudEventExtension.BPMN_ACTIVITY_NAME));
        zeebeCloudEventExtension.setProcessDefinitionKey(headers.getFirst(ZeebeCloudEventExtension.PROCESS_DEFINITION_KEY));
        zeebeCloudEventExtension.setProcessInstanceKey(headers.getFirst(ZeebeCloudEventExtension.PROCESS_INSTANCE_KEY));
        zeebeCloudEventExtension.setJobKey(headers.getFirst(ZeebeCloudEventExtension.JOB_KEY));
        return zeebeCloudEventExtension;
    }

    private static CloudEvent internalParseCloudEventWithExtensionOrDefault(Object body,  HttpHeaders headers, ZeebeCloudEventExtension extension) {
        if (extension != null) {
            return CloudEventsHelper.parseFromRequestWithExtension(headers, body, extension);
        } else {
            return CloudEventsHelper.parseFromRequest(headers, body);
        }
    }






    /*
     * This method will create a Zeebe Cloud Event from an ActivatedJob inside a worker, this allow other systems to consume
     * this Cloud Event and
     */
    // public static CloudEvent createZeebeCloudEventFromJob(ActivatedJob job) throws JsonProcessingException {


    //     final ZeebeCloudEventExtension zeebeCloudEventExtension = new ZeebeCloudEventExtension();

    //     // I need to do the HTTP to Cloud Events mapping here, that means picking up the CorrelationKey header and add it to the Cloud Event
    //     zeebeCloudEventExtension.setBpmnActivityId(String.valueOf(job.getElementInstanceKey()));
    //     zeebeCloudEventExtension.setBpmnActivityName(job.getElementId());
    //     zeebeCloudEventExtension.setJobKey(String.valueOf(job.getKey()));
    //     zeebeCloudEventExtension.setProcessDefinitionKey(String.valueOf(job.getProcessDefinitionKey()));
    //     zeebeCloudEventExtension.setProcessInstanceKey(String.valueOf(job.getProcessInstanceKey()));
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     log.info(">>>>> Job Variables: " + objectMapper.writeValueAsString(job.getVariables()));

    //     // PojoCloudEventData<MyPojo> mappedData = CloudEventUtils.mapData(

    //     final CloudEvent zeebeCloudEvent = CloudEventBuilder.v1()
    //             .withId(UUID.randomUUID().toString())
    //             .withTime(OffsetDateTime.now()) // bug-> https://github.com/cloudevents/sdk-java/issues/200
    //             .withType(job.getCustomHeaders().get(Headers.CLOUD_EVENT_TYPE)) // from headers
    //             .withSource(URI.create("workflow-zeebe.workflow.svc.cluster.local"))
    //             .withData(objectMapper.writeValueAsString(job.getVariables()).getBytes())
    //             // .withData("application/json", new JsonCloudEventData(JsonNodeFactory.instance.String(job.getVariables())))
    //             .withDataContentType(Headers.CONTENT_TYPE)
    //             .withSubject("Zeebe Job")
    //             .withExtension(zeebeCloudEventExtension)
    //             .build();

    //     return zeebeCloudEvent;
    // }


    /*
     * Using a CloudEventsBuilder we can create a ZeebeCloudEventBuilder where we can add the Zeebe Extension parameters
     * and then build a ZeebeCloudEvent.
     */
    // public static ZeebeCloudEventBuilder buildZeebeCloudEvent(CloudEventBuilder cloudEventBuilder){
    //     return new ZeebeCloudEventBuilder(cloudEventBuilder);
    // }

    // public static void emitZeebeCloudEventHTTPFromJob(ActivatedJob job, String host) throws JsonProcessingException {

    //     final CloudEvent myCloudEvent = ZeebeCloudEventsHelper.createZeebeCloudEventFromJob(job);

    //     log.info("cloudEvent >>> " + myCloudEvent.toString());
    //     WebClient webClient = WebClient.builder().baseUrl(host).filter(logRequest()).build();
      
    //     // Mono<CloudEvent> response = webClient.post()
    //     //                                     .uri("http://localhost:8080/events")
    //     //                                     .bodyValue(myCloudEvent)
    //     //                                     .retrieve()
    //     //                                     .bodyToMono(CloudEvent.class);

    //     // W

    //     WebClient.ResponseSpec postCloudEvent = CloudEventsHelper.createPostCloudEvent(webClient, myCloudEvent);

    //     postCloudEvent.bodyToMono(String.class).doOnError(t -> t.printStackTrace())
    //             .doOnSuccess(s -> log.info("Result -> " + s)).subscribe();
    // }

    // //@TODO: refactor to helper class
    // public static ExchangeFilterFunction logRequest() {
    //     return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
    //         log.info("Request: " + clientRequest.method() + " - " + clientRequest.url());
    //         clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(name + "=" + value)));
    //         log.info(clientRequest.body().toString());
    //         return Mono.just(clientRequest);
    //     });
    // }



}
