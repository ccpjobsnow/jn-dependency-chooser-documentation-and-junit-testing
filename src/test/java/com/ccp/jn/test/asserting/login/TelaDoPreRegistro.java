package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusSavePreRegistration;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordLocked;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginTokenLocked;

public class TelaDoPreRegistro  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusSavePreRegistration.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginTokenLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.tokenFaltando);
	}

	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.loginConflict);
	}

	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPasswordLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.lockedPassword);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.cadastrarPreRegistration(variaveisParaTeste, StatusSavePreRegistration.expectedStatus);
	}

	private void cadastrarPreRegistration(VariaveisParaTeste variaveisParaTeste, StatusEndpointsLogin expectedStatus) {
		this.cadastrarPreRegistration(variaveisParaTeste.VALID_EMAIL, expectedStatus);
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
