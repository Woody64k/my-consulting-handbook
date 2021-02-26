package ch.bund.eda.services.pdf.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BpmConfig {

    @Value("${camunda.rest.url}")
    private String camundaRestUrl;

    @Bean
    public ExternalTaskClient createTaskClient() {
	ExternalTaskClient client = ExternalTaskClient.create().baseUrl(camundaRestUrl).asyncResponseTimeout(10000)
		.build();
	// subscribe to the topic
	// client.subscribe("invoiceCreator").handler(new
	// CreateRioAntragHandler()).open();
	return client;
    }
}
