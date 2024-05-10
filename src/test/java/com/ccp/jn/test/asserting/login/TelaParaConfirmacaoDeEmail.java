package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginPasswordLocked;
import com.jn.commons.entities.JnEntityLoginTokenLocked;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class TelaParaConfirmacaoDeEmail  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.confirmarEmail(ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusCreateLoginEmail.invalidEmail);
	}
	
	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginTokenLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.lockedToken);
	}
	
	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPasswordLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.lockedPassword);
	}
	
	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.loginConflict);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.missingPassword);
	}
	
	@Test
	public void faltandoPreRegistro() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		this.confirmarEmail(variaveisParaTeste, StatusCreateLoginEmail.expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(VariaveisParaTeste variaveisParaTeste, StatusEndpointsLogin expectedStatus) {
		return this.confirmarEmail(variaveisParaTeste.VALID_EMAIL, expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(String email, StatusEndpointsLogin expectedStatus) {
		
		String uri = "/login/"
		+ email
		+ "/token";
		CcpJsonRepresentation asSingleJson = this.testarEndpoint(uri, expectedStatus);
		return asSingleJson;
	}


	
	protected String getMethod() {
		return "POST";
	}

}
