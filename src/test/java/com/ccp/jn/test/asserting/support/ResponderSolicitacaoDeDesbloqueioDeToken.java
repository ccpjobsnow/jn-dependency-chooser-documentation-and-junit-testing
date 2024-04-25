package com.ccp.jn.test.asserting.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.login.ConstantesParaTestesDeLogin;
import com.ccp.jn.test.asserting.login.UnlockToken;
import com.ccp.jn.test.asserting.login.SavePreRegistration;
import com.ccp.jn.test.asserting.login.RequestUnlockToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;

public class ResponderSolicitacaoDeDesbloqueioDeToken extends TemplateDeTestes {
	private static final RequestUnlockToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new RequestUnlockToken();
	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final SavePreRegistration PRE_REGISTRO = new SavePreRegistration();
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
		boolean exists = JnEntityRequestUnlockTokenAnswered.INSTANCE.exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists);
	}
	
	@Test
	public void requisicaoJaRespondida() {
		this.caminhoFeliz();
		this.desbloquearToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.requisicaoJaRespondida);
	}
	
	
	
	private CcpJsonRepresentation desbloquearToken(Long chatId, String email, Integer expectedStatus) {
		
		String uri = "support/"
				+ chatId
				+ "/token"
				+ "/"
				+ email
				+ "/unlock";
		
		CcpJsonRepresentation testarEndpoint = this.testarEndpoint(uri, expectedStatus);
		return testarEndpoint;
		
	}
	
	
	@Override
	protected String getMethod() {
		return "POST";
	}
	

}
