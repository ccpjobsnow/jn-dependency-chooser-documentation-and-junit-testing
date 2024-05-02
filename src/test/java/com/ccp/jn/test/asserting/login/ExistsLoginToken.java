package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;

public class ExistsLoginToken extends TemplateDeTestes{

	private final int faltandoCadastrarSenha = 202;
	private final int usuarioNovoNoSistema = 404;
	private final int faltandoPreRegistro = 201;
	private final int usuarioJaLogado = 409;
	private final int tokenBloqueado = 403;
	private final int senhaBloqueada = 401;
	private final int emailInvalido = 400;
	
	
	@Test
	public void emailInvalido() {
		this.verificarExistenciaDeEmail(this.emailInvalido, ConstantesParaTestesDeLogin.INVALID_EMAIL);
	}
	
	@Test
	public void usuarioNovoNoSistema() {
		this.verificarExistenciaDeEmail(this.usuarioNovoNoSistema);
	}

	@Test
	public void tokenBloqueado() {
		new UpdatePassword().tokenBloqueado();
		this.verificarExistenciaDeEmail(this.tokenBloqueado);
	}
	
	@Test
	public void faltandoPreRegistro() {
		new UpdatePassword().faltandoPreRegistro();
		this.verificarExistenciaDeEmail(this.faltandoPreRegistro);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		new SavePreRegistration().faltandoCadastrarSenha();
		this.verificarExistenciaDeEmail(this.faltandoCadastrarSenha);
	}

	@Test
	public void senhaBloqueada() {
		new ExecuteLogin().senhaBloqueada();
		this.verificarExistenciaDeEmail(this.senhaBloqueada);
	}
	@Test
	public void usuarioJaLogado() {
		new ExecuteLogin().usuarioJaLogado();
		this.verificarExistenciaDeEmail(this.usuarioJaLogado);
	}
	
	
	@Test
	public void caminhoFeliz() {
		new ExecuteLogout().caminhoFeliz();
		this.verificarExistenciaDeEmail(this.caminhoFeliz);
	}
	
	private void verificarExistenciaDeEmail(int expectedStatus) {
		this.verificarExistenciaDeEmail(expectedStatus, ConstantesParaTestesDeLogin.VALID_EMAIL);
	}	
	
	private void verificarExistenciaDeEmail(int expectedStatus, String email) {
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
