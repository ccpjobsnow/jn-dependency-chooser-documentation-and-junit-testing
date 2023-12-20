package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.support.ResponderSolicitacaoDeDesbloqueioDeToken;
import com.jn.commons.entities.JnEntityFailedUnlockToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;
import com.jn.commons.utils.JnConstants;

public class DesbloqueioDeToken extends TemplateDeTestes{
	
	private static final CadastroDeSenha CADASTRO_DE_SENHA = new CadastroDeSenha();
	private static final ResponderSolicitacaoDeDesbloqueioDeToken RESPONDER_SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new ResponderSolicitacaoDeDesbloqueioDeToken();
	private static final SolicitacaoDeDesbloqueioDeToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new SolicitacaoDeDesbloqueioDeToken();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int aguardandoVerificacaoDoSuporte = 202;
	private final int senhaDigitadaIncorretamente = 401;
	private final int usuarioNovoNoSistema = 404;
	private final int tokenNaoBloqueado = 422;
	private final int emailInvalido = 400;

	@Test
	public void emailInvalido() {
		this.desbloquearToken(ConstantesParaTestesDeLogin.INVALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.emailInvalido);
	}

	@Test
	public void usuarioNovoNoSistema() {
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.usuarioNovoNoSistema);
	}

	@Test
	public void tokenNaoBloqueado() {
		CADASTRO_DE_SENHA.caminhoFeliz();
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.tokenNaoBloqueado);
	}

	@Test
	public void senhaDigitadaIncorretamente1() {
		RESPONDER_SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.senhaDigitadaIncorretamente);
	}

	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		RESPONDER_SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		for(int k = 0; k < (JnConstants.maxTries); k++) {
			this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.senhaDigitadaIncorretamente);
		}
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.senhaDeDesbloqueioDeTokenEstaBloqueada);
		boolean exists = new JnEntityFailedUnlockToken().exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists);
	}

	@Test
	public void aguardandoVerificacaoDoSuporte() {
		SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, ConstantesParaTestesDeLogin.WRONG_PASSWORD, this.aguardandoVerificacaoDoSuporte );
		
	}
	
	@Test
	public void caminhoFeliz() {
		RESPONDER_SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		
		CcpMapDecorator oneById = new JnEntityRequestUnlockTokenAnswered().getOneById(ConstantesParaTestesDeLogin.TESTING_JSON);
		String senhaCorreta = oneById.getAsString("token");
		this.desbloquearToken(ConstantesParaTestesDeLogin.VALID_EMAIL, senhaCorreta, this.caminhoFeliz);
	}

	private void desbloquearToken(String email, String password, int expectedStatus) {
		CcpMapDecorator put = new CcpMapDecorator().put("password", password);
		String uri = "/login/" + email + "/token/lock";
		Integer valueOf = Integer.valueOf(expectedStatus);
		this.testarEndpoint(valueOf, put, uri, CcpHttpResponseType.singleRecord);
	}
	
	
	@Override
	protected String getMethod() {
		return "PATCH";
	}

}
