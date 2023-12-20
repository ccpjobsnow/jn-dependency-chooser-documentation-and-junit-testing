package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;

public class VerificacaoDeEmail extends TemplateDeTestes{

	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;
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
		new CadastroDeSenha().tokenBloqueado();
		this.verificarExistenciaDeEmail(this.tokenBloqueado);
	}
	
	@Test
	public void faltandoPreRegistro() {
		new CadastroDeSenha().faltandoPreRegistro();
		this.verificarExistenciaDeEmail(this.faltandoPreRegistro);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		new PreRegistro().faltandoCadastrarSenha();
		this.verificarExistenciaDeEmail(this.faltandoCadastrarSenha);
	}

	@Test
	public void senhaBloqueada() {
		new ExecucaoDeLogin().senhaBloqueada();
		this.verificarExistenciaDeEmail(this.senhaBloqueada);
	}
	@Test
	public void usuarioJaLogado() {
		new ExecucaoDeLogin().usuarioJaLogado();
		this.verificarExistenciaDeEmail(this.usuarioJaLogado);
	}
	
	@Test
	public void tokenPendenteDeDesbloqueio() {
		new SolicitacaoDeDesbloqueioDeToken().caminhoFeliz();
		this.verificarExistenciaDeEmail(this.tokenPendenteDeDesbloqueio);
	}
	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.verificarExistenciaDeEmail(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}
	
	@Test
	public void caminhoFeliz() {
		new Logout().caminhoFeliz();
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
