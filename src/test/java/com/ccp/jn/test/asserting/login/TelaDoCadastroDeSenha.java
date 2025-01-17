package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.ccp.jn.test.asserting.JnTemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginToken;

public class TelaDoCadastroDeSenha extends JnTemplateDeTestes{
	@Test
	public void emailInvalido() {
		this.requisicaoFake(VariaveisParaTeste.INVALID_EMAIL, "abcdefgh", StatusUpdatePassword.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.ENTITY.getTwinEntity();
		mirrorEntity.createOrUpdate( variaveisParaTeste.REQUEST_TO_LOGIN);
		String token = this.getTokenToValidateLogin(variaveisParaTeste);
		this.execute(variaveisParaTeste, StatusUpdatePassword.lockedToken, x -> token);
	}

	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getTokenToValidateLogin(variaveisParaTeste);
		JnEntityLoginEmail.ENTITY.delete( variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusUpdatePassword.missingEmail, x -> token);
	}

	@Test
	public void efetuarDesbloqueios() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginPassword.ENTITY.getTwinEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginSessionCurrent.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.fluxoEsperado(variaveisParaTeste);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.fluxoEsperado(variaveisParaTeste);
	}

	public void fluxoEsperado(VariaveisParaTeste variaveisParaTeste) {
		String token = this.getToken(variaveisParaTeste);
		this.execute(variaveisParaTeste,StatusUpdatePassword.expectedStatus, x -> token);
	}

	private String getToken(VariaveisParaTeste variaveisParaTeste) {
		JnEntityLoginEmail.ENTITY.createOrUpdate( variaveisParaTeste.REQUEST_TO_LOGIN);

		CcpJsonRepresentation entityValue =  variaveisParaTeste.REQUEST_TO_LOGIN;
		CcpJsonRepresentation createOrUpdate = JnEntityLoginToken.ENTITY.createOrUpdate(entityValue);
		String token = createOrUpdate.getAsString("originalToken");
		return token;
	}

	@Test
	public void errarParaDepoisAcertarToken() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getToken(variaveisParaTeste);
		for(int k = 1; k < 3; k++) {
			this.execute(variaveisParaTeste, StatusUpdatePassword.wrongToken, x -> "abcdefgh");
		}
		this.execute(variaveisParaTeste, StatusUpdatePassword.expectedStatus, x -> token);
	}
	
	@Test
	public void tokenRecemBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getToken(variaveisParaTeste);
		for(int k = 1; k <= 3; k++) {
			this.execute(variaveisParaTeste, StatusUpdatePassword.wrongToken, x -> "abcdefgh");
		}
		this.execute(variaveisParaTeste, StatusUpdatePassword.tokenLockedRecently, x -> "abcdefgh");
		new CcpTimeDecorator().sleep(10_000);
		this.execute(variaveisParaTeste, StatusUpdatePassword.lockedToken, x -> token);
	}

	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus, Function<VariaveisParaTeste, String> producer) {
		String tokenToValidateLogin = producer.apply(variaveisParaTeste);
		String uri = "login/"
		+ variaveisParaTeste.VALID_EMAIL
		+ "/password";
		
		CcpJsonRepresentation body =  variaveisParaTeste.REQUEST_TO_LOGIN.put("password", VariaveisParaTeste.CORRECT_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
	}

	private void requisicaoFake(String email, String tokenToValidateLogin, CcpProcessStatus expectedStatus) {
		String uri = "login/"
		+ email
		+ "/password";
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpJsonRepresentation body =  variaveisParaTeste.REQUEST_TO_LOGIN.put("password", VariaveisParaTeste.CORRECT_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	
	private String getTokenToValidateLogin(VariaveisParaTeste variaveisParaTeste) {
		JnEntityLoginEmail.ENTITY.createOrUpdate( variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginAnswers.ENTITY.createOrUpdate( variaveisParaTeste.ANSWERS_JSON);
		return "";

	}
	
	protected String getMethod() {
		return "POST";
	}


}
