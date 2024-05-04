package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusExecuteLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class TelaQuePedeSenhaParaEntrarNoSistema extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.executarLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD , StatusExecuteLogin.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.missingEmail);
	}

	@Test
	public void faltandoCadastrarSenha() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		JnEntityLoginLockedPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.lockedPassword);
	}

	@Test
	public void usuarioJaLogado() {
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.loginConflict);
	}

	@Test
	public void senhaIncorreta() {
		this.executarLogin(ConstantesParaTestesDeLogin.WRONG_PASSWORD, StatusExecuteLogin.wrongPassword);
	}

	@Test
	public void caminhoFeliz() {
		new TelaDoCadastroDeSenha().caminhoFeliz();
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.expectedStatus);
	}

	@Test
	public void senhaRecemBloqueada() {
		new TelaDoCadastroDeSenha().caminhoFeliz();
		for(int k = 0; k < 3; k++) {
			this.executarLogin(ConstantesParaTestesDeLogin.WRONG_PASSWORD, StatusExecuteLogin.wrongPassword);
		}
		this.executarLogin(ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.passwordLockedRecently);
	}

	private void executarLogin(String senha, StatusEndpointsLogin expectedStatus) {
		this.executarLogin(ConstantesParaTestesDeLogin.VALID_EMAIL, senha, expectedStatus);
	}
	
	private void executarLogin(String email, String senha, StatusEndpointsLogin expectedStatus) {
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
