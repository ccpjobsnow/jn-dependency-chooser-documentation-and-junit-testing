package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.status.login.CreateLoginEmail;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaParaConfirmacaoDeEmail  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.confirmarEmail(ConstantesParaTestesDeLogin.INVALID_EMAIL, CreateLoginEmail.emailInvalido);
	}
	
	@Test
	public void tokenBloqueado() {
		this.confirmarEmail(CreateLoginEmail.tokenBloqueado);
	}
	
	@Test
	public void senhaBloqueada() {
		this.confirmarEmail(CreateLoginEmail.senhaBloqueada);
	}
	
	@Test
	public void usuarioJaLogado() {
		this.confirmarEmail(CreateLoginEmail.usuarioJaLogado);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		this.confirmarEmail(CreateLoginEmail.faltandoCadastrarSenha);
	}
	
	@Test
	public void faltandoPreRegistro() {
		this.confirmarEmail(CreateLoginEmail.faltandoPreRegistration);
	}
	
	@Test
	public void caminhoFeliz() {
		this.confirmarEmail(CreateLoginEmail.caminhoFeliz);
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
