package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.CreateLoginToken;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.UpdatePassword;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityPreRegistration;

public class TelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		String token = "abcdefgh";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INVALID_EMAIL, token, UpdatePassword.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		String token = this.getTokenToValidateLogin();
		JnEntityLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		String token = this.getTokenToValidateLogin();
		JnEntityLoginEmail.INSTANCE.delete(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.missingEmail);
	}

	@Test
	public void caminhoFeliz() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.expectedStatus);
	}

	@Test
	public void tokenRecemBloqueado() {
		String token = this.getTokenToValidateLogin();
		for(int k = 0; k < 3; k++) {
			this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, "abcdefgh", UpdatePassword.wrongToken);
		}
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.tokenLockedRecently);
	}

	private void cadastrarSenha(String email, String tokenToValidateLogin, EndpointsLogin expectedStatus) {
		String uri = "login/"
		+ email
		+ "/password";
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", ConstantesParaTestesDeLogin.STRONG_PASSWORD)
				.put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	private String getTokenToValidateLogin() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityPreRegistration.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		new AoEntrarNaTelaDoCadastroDeSenha().criarTokenDeLogin(CreateLoginToken.expectedStatus);
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
