package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.http.CcpHttpMethods;
import com.ccp.jn.commons.status.login.StatusExecuteLogout;
import com.ccp.jn.test.asserting.JnTemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginSessionConflict;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.json.transformers.JnJsonTransformerPutRandomTokenHash;

public class TelaDeLogout extends JnTemplateDeTestes {

	@Test
	public void emailInvalido() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste(VariaveisParaTeste.INVALID_EMAIL);
		this.execute(variaveisParaTeste, StatusExecuteLogout.invalidEmail, variaveis -> "qualquerToken");
	}

	@Test
	public void usuarioNaoLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.execute(variaveisParaTeste, StatusExecuteLogout.missingLogin, variaveis -> "qualquerToken");
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginSessionConflict.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		Function<VariaveisParaTeste, String> producer = variaveis -> JnEntityLoginSessionValidation.ENTITY
				.createOrUpdate(variaveis.REQUEST_TO_LOGIN.getTransformedJson(JnJsonTransformerPutRandomTokenHash.INSTANCE))
				.getAsString("originalToken");
		this.execute(variaveisParaTeste, StatusExecuteLogout.expectedStatus, producer);
	}
	
	public void caminhoFeliz2() {
		
	}
 
	protected CcpJsonRepresentation getHeaders() { 
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				;
		return put;
	}
	
	
	
	protected CcpHttpMethods getMethod() {
		return CcpHttpMethods.DELETE;
	}

	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus,
			Function<VariaveisParaTeste, String> producer) {
		String sessionToken = producer.apply(variaveisParaTeste);
		String uri = "/login/" + variaveisParaTeste.VALID_EMAIL + "/" + sessionToken;
		this.testarEndpoint(uri, expectedStatus);
		return sessionToken;
		
	}

}
