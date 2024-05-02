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
		String tokenCorreto = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INVALID_EMAIL, tokenCorreto, UpdatePassword.emailInvalido);
	}

	@Test
	public void tokenBloqueado() {
		String token = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.tokenBloqueado);
	}

	@Test
	public void tokenFaltando() {
		String token = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.tokenFaltando);
	}

	@Test
	public void caminhoFeliz() {
		String token = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.caminhoFeliz);
	}

	@Test
	public void tokenDigitadoIncorretamente() {
		String token = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.tokenDigitadoIncorretamente);
	}

	@Test
	public void tokenRecemBloqueado() {
		String token = "";
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, token, UpdatePassword.tokenRecemBloqueado);
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
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("email", ConstantesParaTestesDeLogin.VALID_EMAIL);
		CcpJsonRepresentation data = JnEntityLoginToken.INSTANCE.getOneById(put);
		String token = data.getAsString("token");
		return token;

	}
	@Override
	protected String getMethod() {
		return "POST";
	}


}
