package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class TelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		String token = "abcdefgh";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INVALID_EMAIL, token, StatusUpdatePassword.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		String token = this.getTokenToValidateLogin();
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, StatusUpdatePassword.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		String token = this.getTokenToValidateLogin();
		JnEntityLoginEmail.INSTANCE.delete(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, StatusUpdatePassword.missingEmail);
	}

	@Test
	public void caminhoFeliz() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, StatusUpdatePassword.expectedStatus);
	}

	@Test
	public void tokenRecemBloqueado() {
		String token = this.getTokenToValidateLogin();
		for(int k = 0; k < 3; k++) {
			this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, "abcdefgh", StatusUpdatePassword.wrongToken);
		}
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, StatusUpdatePassword.tokenLockedRecently);
	}

	private void cadastrarSenha(String email, String tokenToValidateLogin, StatusEndpointsLogin expectedStatus) {
		String uri = "login/"
		+ email
		+ "/password";
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", ConstantesParaTestesDeLogin.CORRECT_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	private String getTokenToValidateLogin() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		new AoEntrarNaTelaDoCadastroDeSenha().criarTokenDeLogin(StatusCreateLoginToken.statusExpectedStatus);
		new CcpTimeDecorator().sleep(10000);
		CcpJsonRepresentation data = JnEntityLoginToken.INSTANCE.getOneById(ConstantesParaTestesDeLogin.TESTING_JSON);
		String token = data.getAsString("token");
		return token;

	}
	@Override
	protected String getMethod() {
		return "POST";
	}


}
