package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityRequestTokenAgain;

public class RessolicitacaoToken extends TemplateDeTestes {

	
	private static final SolicitacaoDeDesbloqueioDeToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new SolicitacaoDeDesbloqueioDeToken();
	private static final ConfirmacaoDeEmail CONFIRMACAO_DE_EMAIL = new ConfirmacaoDeEmail();
	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private final int requisicaoDeReenvioDeTokenJaFeitaPreviamente = 409;
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;
	private final int usuarioNovoNoSistema = 404;
	private final int emailInvalido = 400;

	@Test
	public void emailInvalido() {
		this.ressolicitarToken(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}

	@Test
	public void usuarioNovoNoSistema() {
		this.ressolicitarToken(ConstantesParaTestesDeLogin.VALID_EMAIL, this.usuarioNovoNoSistema);
	}

	@Test
	public void requisicaoDeReenvioDeTokenJaFeitaPreviamente() {
		this.caminhoFeliz();
		this.ressolicitarToken(ConstantesParaTestesDeLogin.VALID_EMAIL, this.requisicaoDeReenvioDeTokenJaFeitaPreviamente);
	}
	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.ressolicitarToken(ConstantesParaTestesDeLogin.VALID_EMAIL, this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}
	@Test
	public void tokenPendenteDeDesbloqueio() {
		SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		this.ressolicitarToken(ConstantesParaTestesDeLogin.VALID_EMAIL, this.tokenPendenteDeDesbloqueio);
	}
	@Test
	public void caminhoFeliz() {
		CONFIRMACAO_DE_EMAIL.caminhoFeliz();
		this.ressolicitarToken(ConstantesParaTestesDeLogin.VALID_EMAIL, this.caminhoFeliz);
		boolean exists = new JnEntityRequestTokenAgain().exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists);
	}

	private void ressolicitarToken(String email, int expectedStatus) {
		String uri = "/login/" + email + "/token/language/portuguese/request/again";
		this.testarEndpoint(uri, expectedStatus);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}

}
