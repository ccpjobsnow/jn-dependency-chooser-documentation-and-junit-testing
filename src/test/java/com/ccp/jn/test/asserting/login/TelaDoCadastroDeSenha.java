package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginTokenLocked;
import com.jn.commons.utils.JnGenerateRandomToken;
import com.jn.commons.utils.JnGenerateRandomTokenWithHash;

public class TelaDoCadastroDeSenha extends TemplateDeTestes{
	public TelaDoCadastroDeSenha() {
		
	}
	
	@Test
	public void emailInvalido() {
		this.requisicaoFake(ConstantesParaTestesDeLogin.INVALID_EMAIL, "abcdefgh", StatusUpdatePassword.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginTokenLocked.INSTANCE.createOrUpdate( variaveisParaTeste.TESTING_JSON);
		String token = this.getTokenToValidateLogin(variaveisParaTeste);
		this.cadastrarSenha(variaveisParaTeste, token, StatusUpdatePassword.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getTokenToValidateLogin(variaveisParaTeste);
		JnEntityLoginEmail.INSTANCE.delete( variaveisParaTeste.TESTING_JSON);
		this.cadastrarSenha(variaveisParaTeste, token, StatusUpdatePassword.missingEmail);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.fluxoEsperado(variaveisParaTeste);
	}

	public void fluxoEsperado(VariaveisParaTeste variaveisParaTeste) {
		String token = this.getToken(variaveisParaTeste);
		this.cadastrarSenha(variaveisParaTeste, token, StatusUpdatePassword.expectedStatus);
	}

	private String getToken(VariaveisParaTeste variaveisParaTeste) {
		JnEntityLoginEmail.INSTANCE.createOrUpdate( variaveisParaTeste.TESTING_JSON);

		JnGenerateRandomTokenWithHash transformer = new JnGenerateRandomTokenWithHash(8, "token", "tokenHash");
		CcpJsonRepresentation entityValue =  variaveisParaTeste.REQUEST_TO_LOGIN.getTransformed(transformer);
		JnEntityLoginToken.INSTANCE.createOrUpdate(entityValue);
		String token = entityValue.getAsString("token");
		return token;
	}

	@Test
	public void errarParaDepoisAcertarToken() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getToken(variaveisParaTeste);
		for(int k = 1; k < 3; k++) {
			this.cadastrarSenha(variaveisParaTeste, "abcdefgh", StatusUpdatePassword.wrongToken);
		}
		this.cadastrarSenha(variaveisParaTeste, token, StatusUpdatePassword.expectedStatus);
	}

	
	@Test
	public void tokenRecemBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		String token = this.getToken(variaveisParaTeste);
		for(int k = 1; k < 3; k++) {
			this.cadastrarSenha(variaveisParaTeste, "abcdefgh", StatusUpdatePassword.wrongToken);
		}
		this.cadastrarSenha(variaveisParaTeste, "abcdefgh", StatusUpdatePassword.tokenLockedRecently);
		this.cadastrarSenha(variaveisParaTeste, token, StatusUpdatePassword.lockedToken);
	}

	private void cadastrarSenha(VariaveisParaTeste variaveisParaTeste, String tokenToValidateLogin, StatusEndpointsLogin expectedStatus) {
		String uri = "login/"
		+ variaveisParaTeste.VALID_EMAIL
		+ "/password";
		CcpJsonRepresentation body =  variaveisParaTeste.REQUEST_TO_LOGIN.put("password", ConstantesParaTestesDeLogin.CORRECT_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	private void requisicaoFake(String email, String tokenToValidateLogin, StatusEndpointsLogin expectedStatus) {
		String uri = "login/"
		+ email
		+ "/password";
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpJsonRepresentation body =  variaveisParaTeste.REQUEST_TO_LOGIN.put("password", ConstantesParaTestesDeLogin.CORRECT_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	
	private String getTokenToValidateLogin(VariaveisParaTeste variaveisParaTeste) {
		JnEntityLoginEmail.INSTANCE.createOrUpdate( variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate( variaveisParaTeste.TESTING_JSON);
		JnGenerateRandomToken transformer = new JnGenerateRandomToken(8, "token");
		CcpJsonRepresentation loginToken =  variaveisParaTeste.TESTING_JSON.getTransformed(transformer);
		String token = loginToken.getAsString("token");
		return token;

	}
	
	protected String getMethod() {
		return "POST";
	}


}
