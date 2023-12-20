package com.ccp.jn.test.asserting;

import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;

public class VerificacaoDeStatusDaTarefaAssincrona extends TemplateDeTestes{

	@SuppressWarnings("unchecked")
	public void getCaminhoFeliz(String asyncTaskId, CcpMapDecorator jsonToTest, Predicate<CcpMapDecorator>... predicates) {
		
		CcpMapDecorator json = this.getCaminhoFeliz("async/task/" + asyncTaskId);
		
		for(int k = 0; k < 10; k++) {
			new CcpTimeDecorator().sleep(1000);
			
			boolean stillIsRunning = json.containsKey("finished") == false;
			
			if(stillIsRunning) {
				continue;
			}
			
			boolean success = json.getAsBoolean("success");
			assertTrue(success);
			
			for (Predicate<CcpMapDecorator> predicate : predicates) {
				boolean test = predicate.test(jsonToTest);
				assertTrue(test);
			}
			return;

		}
	}
	
	@Override
	protected String getMethod() {
		return "GET";
	}

}
