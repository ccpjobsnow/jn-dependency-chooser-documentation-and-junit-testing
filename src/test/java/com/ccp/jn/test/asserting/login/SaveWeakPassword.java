package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;

public class SaveWeakPassword extends TemplateDeTestes{

	private final int usuarioNovoNoSistema = 404;
	private final int faltandoPreRegistro = 201;
	private final int tokenBloqueado = 403;
	private final int emailInvalido = 400;
	
	@Test
	public void emailInvalido() {
		this.confirmarSenhaFraca(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}

	@Test
	public void usuarioNovoNoSistema() {
		this.confirmarSenhaFraca(this.usuarioNovoNoSistema);
	}

	@Test
	public void tokenBloqueado() {
		new UpdatePassword().tokenBloqueado();
		this.confirmarSenhaFraca(this.tokenBloqueado); 
	}

	@Test
	public void faltandoPreRegistro() {
		new UpdatePassword().faltandoPreRegistro();
		this.confirmarSenhaFraca(this.faltandoPreRegistro);
	}

	@Test
	public void caminhoFeliz() {
		new SavePreRegistration().caminhoFeliz();
		this.confirmarSenhaFraca(this.caminhoFeliz);
	}

	private void confirmarSenhaFraca(int expectedStatus) {
		this.confirmarSenhaFraca(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void confirmarSenhaFraca(String email, int expectedStatus) {
		this.testarEndpoint("login/"
				+ email
				+ "/password/weak", expectedStatus);
	}
	
	@Override
	protected String getMethod() {
		return "POST";
	}

}
