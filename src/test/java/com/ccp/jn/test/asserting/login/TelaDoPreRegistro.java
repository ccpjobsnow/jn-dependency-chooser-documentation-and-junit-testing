package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusSavePreRegistration;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class TelaDoPreRegistro  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusSavePreRegistration.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarPreRegistration(StatusSavePreRegistration.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		this.cadastrarPreRegistration(StatusSavePreRegistration.tokenFaltando);
	}

	@Test
	public void usuarioJaLogado() {
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarPreRegistration(StatusSavePreRegistration.loginConflict);
	}

	@Test
	public void faltandoCadastrarSenha() {
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarPreRegistration(StatusSavePreRegistration.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		JnEntityLoginLockedPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarPreRegistration(StatusSavePreRegistration.lockedPassword);
	}

	@Test
	public void caminhoFeliz() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.cadastrarPreRegistration(StatusSavePreRegistration.expectedStatus);
	}

	private void cadastrarPreRegistration(StatusEndpointsLogin expectedStatus) {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void cadastrarPreRegistration(String email, StatusEndpointsLogin expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("goal", "jobs").put("channel", "linkedin");
		String uri = "login/"+ email 	+ "/pre-registration";
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	
	protected String getMethod() {
		return "POST";
	}

}
