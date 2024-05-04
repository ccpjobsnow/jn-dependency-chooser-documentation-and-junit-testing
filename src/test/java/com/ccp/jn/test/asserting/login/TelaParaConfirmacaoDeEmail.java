package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLogin;
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
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.lockedToken);
	}
	
	@Test
	public void senhaBloqueada() {
		JnEntityLoginLockedPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.lockedPassword);
	}
	
	@Test
	public void usuarioJaLogado() {
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.loginConflict);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.missingPassword);
	}
	
	@Test
	public void faltandoPreRegistro() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.confirmarEmail(StatusCreateLoginEmail.expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(StatusEndpointsLogin expectedStatus) {
		return this.confirmarEmail(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(String email, StatusEndpointsLogin expectedStatus) {
		
		String uri = "/login/"
		+ email
		+ "/token/language/portuguese";
		CcpJsonRepresentation asSingleJson = this.testarEndpoint(uri, expectedStatus);
		return asSingleJson;
	}


	@Override
	protected String getMethod() {
		return "POST";
	}

}
