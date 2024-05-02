package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.UpdatePassword;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginToken;

public class TelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INVALID_EMAIL, token, UpdatePassword.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.missingToken);
	}

	@Test
	public void caminhoFeliz() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.expectedStatus);
	}

	@Test
	public void tokenDigitadoIncorretamente() {
		String token = this.getTokenToValidateLogin();
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.wrongToken);
	}

	@Test
	public void tokenRecemBloqueado() {
		String token = this.getTokenToValidateLogin();
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
		CcpJsonRepresentation data = JnEntityLoginToken.INSTANCE.getOneById(ConstantesParaTestesDeLogin.TESTING_JSON);
		String token = data.getAsString("token");
		return token;

	}
	@Override
	protected String getMethod() {
		return "POST";
	}


}
