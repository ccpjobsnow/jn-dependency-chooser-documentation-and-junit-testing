package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.ExecuteLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaQuePedeSenhaParaEntrarNoSistema extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		String senha = "";
		this.executarLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, senha , ExecuteLogin.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.missingEmail);
	}

	@Test
	public void faltandoCadastrarSenha() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.lockedPassword);
	}

	@Test
	public void usuarioJaLogado() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.loginConflict);
	}

	@Test
	public void senhaIncorreta() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.wrongPassword);
	}

	@Test
	public void caminhoFeliz() {
		String senha = "";
		this.executarLogin(senha , ExecuteLogin.expectedStatus);
	}

	private void executarLogin(String senha, EndpointsLogin expectedStatus) {
		this.executarLogin(ConstantesParaTestesDeLogin.VALID_EMAIL, senha, expectedStatus);
	}
	
	private void executarLogin(String email, String senha, EndpointsLogin expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", senha);
		String uri = "login/"
		+ email;
		this.testarEndpoint(expectedStatus, body, uri, CcpHttpResponseType.singleRecord);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}
	
}
