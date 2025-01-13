package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.test.asserting.JnTemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;

public class TelaDeLogout extends JnTemplateDeTestes {

	@Test
	public void emailInvalido() {
		this.testarEndpoint("/login/" + VariaveisParaTeste.INVALID_EMAIL, StatusExecuteLogout.invalidEmail);
	}

	@Test
	public void usuarioNaoLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.execute(variaveisParaTeste, StatusExecuteLogout.missingLogin);
	}

	@Test 
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.ENTITY.create(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginSessionCurrent.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogout.expectedStatus);
	}

	protected CcpJsonRepresentation getHeaders() {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
//				.put("sessionToken", VariaveisParaTeste.SESSION_TOKEN)
				;
		return put;
	}
	
	
	
	protected String getMethod() {
		return "DELETE";
	}

	@Override
	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus,
			Function<VariaveisParaTeste, String> producer) {
		String uri = "/login/" + variaveisParaTeste.VALID_EMAIL;
		this.testarEndpoint(uri, expectedStatus);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
		
	}

}
