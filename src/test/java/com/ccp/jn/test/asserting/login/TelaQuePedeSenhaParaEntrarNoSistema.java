package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.ExecuteLogin;
import com.ccp.jn.sync.status.login.UpdatePassword;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLockedPassword;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityPassword;
import com.jn.commons.entities.JnEntityPreRegistration;

public class TelaQuePedeSenhaParaEntrarNoSistema extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.executarLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		JnEntityLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD , ExecuteLogin.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.missingEmail);
	}

	@Test
	public void faltandoCadastrarSenha() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityPreRegistration.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		JnEntityLockedPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.lockedPassword);
	}

	@Test
	public void usuarioJaLogado() {
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.loginConflict);
	}

	@Test
	public void senhaIncorreta() {
		this.executarLogin(ConstantesParaTestesDeLogin.WRONG_PASSWORD, ExecuteLogin.wrongPassword);
	}

	@Test
	public void caminhoFeliz() {
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.expectedStatus);
	}

	@Test
	public void senhaRecemBloqueada() {
		for(int k = 0; k < 3; k++) {
			this.executarLogin("Novasenha1!", ExecuteLogin.wrongPassword);
		}
		this.executarLogin(ConstantesParaTestesDeLogin.STRONG_PASSWORD, ExecuteLogin.passwordLockedRecently);
	}

	private void executarLogin(String senha, EndpointsLogin expectedStatus) {
		new TelaDoCadastroDeSenha().caminhoFeliz();
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
