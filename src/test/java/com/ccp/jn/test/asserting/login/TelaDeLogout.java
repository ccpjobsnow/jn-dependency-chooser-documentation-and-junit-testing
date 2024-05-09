package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;

public class TelaDeLogout extends TemplateDeTestes {

	@Test
	public void emailInvalido() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusExecuteLogout.invalidEmail);
	}

	@Test
	public void usuarioNaoLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.testarEndpoint("/login/" + variaveisParaTeste.VALID_EMAIL, StatusExecuteLogout.missingLogin);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		String uri = "/login/" + variaveisParaTeste.VALID_EMAIL;
		this.testarEndpoint(uri, StatusExecuteLogout.expectedStatus);
	}

	protected CcpJsonRepresentation getHeaders() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("sessionToken", VariaveisParaTeste.SESSION_TOKEN);
		return put;
	}
	
	protected String getMethod() {
		return "DELETE";
	}

}
