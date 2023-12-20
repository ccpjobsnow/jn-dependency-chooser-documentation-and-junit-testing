package com.ccp.jn.test.asserting.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.login.ConstantesParaTestesDeLogin;
import com.ccp.jn.test.asserting.login.DesbloqueioDeToken;
import com.ccp.jn.test.asserting.login.PreRegistro;
import com.ccp.jn.test.asserting.login.SolicitacaoDeDesbloqueioDeToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;

public class ResponderSolicitacaoDeDesbloqueioDeToken extends TemplateDeTestes {
	private static final SolicitacaoDeDesbloqueioDeToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new SolicitacaoDeDesbloqueioDeToken();
	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private static final PreRegistro PRE_REGISTRO = new PreRegistro();
	private final int usuarioNaoRegistradoComoOperadorDeSuporte = 401;
	private final int requisicaoDeDesbloqueioDeTokenNaoExiste = 422;
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int requisicaoJaRespondida = 409;
	private final int tokenNaoCadastrado = 404;

	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.senhaDeDesbloqueioDeTokenEstaBloqueada);
		
	}
	
	
	
	@Test
	public void usuarioNaoRegistradoComoOperadorDeSuporte() {
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_NAO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.usuarioNaoRegistradoComoOperadorDeSuporte);
	}
	
	@Test
	public void tokenNaoCadastrado() {
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.tokenNaoCadastrado);
	}

	@Test
	public void requisicaoDeDesbloqueioDeTokenNaoExiste() {
		PRE_REGISTRO.faltandoCadastrarSenha();	
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.requisicaoDeDesbloqueioDeTokenNaoExiste);
	}
	
	@Test
	public void caminhoFeliz() {
		SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();	
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.caminhoFeliz);
		boolean exists = new JnEntityRequestUnlockTokenAnswered().exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists);
	}
	
	@Test
	public void requisicaoJaRespondida() {
		this.caminhoFeliz();
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.requisicaoJaRespondida);
	}
	
	
	
	private CcpMapDecorator desbloquearToken(Long chatId, String email, Integer expectedStatus) {
		
		String uri = "support/"
				+ chatId
				+ "/token"
				+ "/"
				+ email
				+ "/unlock";
		
		CcpMapDecorator testarEndpoint = this.testarEndpoint(uri, expectedStatus);
		return testarEndpoint;
		
	}
	
	
	@Override
	protected String getMethod() {
		return "POST";
	}
	

}
