package ch.bund.eda.services.pdf.bpm.handler;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.value.TypedValue;

import ch.bund.eda.services.pdf.data.jpa.FileEntity;

public class CreateRioAntragHandler implements ExternalTaskHandler {

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
	externalTask.getAllVariables();
	TypedValue typedInvoice = externalTask.getVariableTyped("invoice");
	FileEntity invoice = (FileEntity) typedInvoice.getValue();
	System.out.println("Invoice on process scope archived: " + invoice);
	boolean isRandomSample = Math.random() <= 0.5;
	Map<String, Object> variables = new HashMap<String, Object>();
	if (isRandomSample) {
	    externalTaskService.complete(externalTask, variables);
	} else {
	    externalTaskService.complete(externalTask, null, variables);
	}
    }

}
