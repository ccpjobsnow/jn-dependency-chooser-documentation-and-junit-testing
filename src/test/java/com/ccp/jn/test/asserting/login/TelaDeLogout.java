package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLogin;

public class TelaDeLogout extends TemplateDeTestes {

	@Test
	public void emailInvalido() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusExecuteLogout.invalidEmail);
	}

	@Test
	public void usuarioNaoLogado() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, StatusExecuteLogout.missingLogin);
	}

	@Test
	public void caminhoFeliz() {
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, StatusExecuteLogout.expectedStatus);
	}

	
	protected String getMethod() {
		return "DELETE";
	}

}
