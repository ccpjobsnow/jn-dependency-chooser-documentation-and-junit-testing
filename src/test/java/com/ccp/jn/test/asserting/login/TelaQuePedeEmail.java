package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.ExistsLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaQuePedeEmail extends TemplateDeTestes{
	
	@Test
	public void emailInvalido() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.emailInvalido, ConstantesParaTestesDeLogin.INVALID_EMAIL);
	}
	
	@Test
	public void tokenBloqueado() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.tokenBloqueado);
	}
	
	@Test
	public void tokenFaltando() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.tokenFaltando);		
	}
	
	@Test
	public void senhaBloqueada() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.senhaBloqueada);		
	}
	
	@Test
	public void usuarioJaLogado() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.usuarioJaLogado);		
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.faltandoCadastrarSenha);		
	}
	
	@Test
	public void faltandoPreRegistro() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.faltandoPreRegistration);		
	}
	
	@Test
	public void caminhoFeliz() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.caminhoFeliz);		
	}
	
	

	private void verificarExistenciaDeEmail(EndpointsLogin expectedStatus) {
		this.verificarExistenciaDeEmail(expectedStatus, ConstantesParaTestesDeLogin.VALID_EMAIL);
	}	
	
	private void verificarExistenciaDeEmail(EndpointsLogin expectedStatus, String email) {
		String uri = "login/"
				+ email
				+ "/token";

		this.testarEndpoint(uri, expectedStatus);
	}

	@Override
	protected String getMethod() {
		return "HEAD";
	}
}
