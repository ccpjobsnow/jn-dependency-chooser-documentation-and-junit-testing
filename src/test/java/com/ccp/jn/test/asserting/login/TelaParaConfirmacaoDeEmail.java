package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.CreateLoginEmail;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaParaConfirmacaoDeEmail  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.confirmarEmail(ConstantesParaTestesDeLogin.INVALID_EMAIL, CreateLoginEmail.invalidEmail);
	}
	
	@Test
	public void tokenBloqueado() {
		this.confirmarEmail(CreateLoginEmail.lockedToken);
	}
	
	@Test
	public void senhaBloqueada() {
		this.confirmarEmail(CreateLoginEmail.lockedPassword);
	}
	
	@Test
	public void usuarioJaLogado() {
		this.confirmarEmail(CreateLoginEmail.loginConflict);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		this.confirmarEmail(CreateLoginEmail.missingPassword);
	}
	
	@Test
	public void faltandoPreRegistro() {
		this.confirmarEmail(CreateLoginEmail.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		this.confirmarEmail(CreateLoginEmail.expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(EndpointsLogin expectedStatus) {
		return this.confirmarEmail(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(String email, EndpointsLogin expectedStatus) {
		
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
