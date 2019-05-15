package de.mid.consulting.training.carmunda.twitter_tool;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

public class CarmundaConfig {

	/**
	 * Richtet eine Prozessengine ein.
	 * 
	 * @return
	 */
	@Bean
	public ProcessEngine api() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
				.setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000").buildProcessEngine();
	}

	@Autowired
	ProcessEngine processEngine;

	public void startProcess(String prozessKey) {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		runtimeService.startProcessInstanceByKey(prozessKey);
	}
}
