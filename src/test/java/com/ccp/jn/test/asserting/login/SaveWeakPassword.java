package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;

public class SaveWeakPassword extends TemplateDeTestes{
	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;

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
	public void tokenPendenteDeDesbloqueio() {
		new RequestUnlockToken().caminhoFeliz();
		this.confirmarSenhaFraca(this.tokenPendenteDeDesbloqueio);
	}
	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.confirmarSenhaFraca(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
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
