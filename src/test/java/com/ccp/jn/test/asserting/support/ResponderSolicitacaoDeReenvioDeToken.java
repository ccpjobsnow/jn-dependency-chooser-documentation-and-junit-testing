package com.ccp.jn.test.asserting.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.login.UpdatePassword;
import com.ccp.jn.test.asserting.login.ConstantesParaTestesDeLogin;
import com.ccp.jn.test.asserting.login.UnlockToken;
import com.ccp.jn.test.asserting.login.SavePreRegistration;
import com.ccp.jn.test.asserting.login.RequestTokenAgain;
import com.ccp.jn.test.asserting.login.RequestUnlockToken;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.entities.JnEntityRequestTokenAgainAnswered;

public class ResponderSolicitacaoDeReenvioDeToken extends TemplateDeTestes {

	private static final RequestUnlockToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new RequestUnlockToken();
	private static final RequestTokenAgain RESSOLICITACAO_TOKEN = new RequestTokenAgain();
	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final UpdatePassword CADASTRO_DE_SENHA = new UpdatePassword();
	private static final SavePreRegistration PRE_REGISTRO = new SavePreRegistration();
	private final int usuarioNaoRegistradoComoOperadorDeSuporte = 401;
	private final int requisicaoDeDesbloqueioDeTokenNaoExiste = 422;
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;
	private final int requisicaoJaRespondida = 409;
	private final int tokenNaoCadastrado = 404;
	private final int tokenBloqueado = 403;

	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.senhaDeDesbloqueioDeTokenEstaBloqueada);
		
	}
	
	@Test
	public void tokenBloqueado() {
		CADASTRO_DE_SENHA.tokenBloqueado();
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.tokenBloqueado);
		
	}
	
	@Test
	public void tokenPendenteDeDesbloqueio() {
		SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.tokenPendenteDeDesbloqueio);
	}

	
	@Test
	public void usuarioNaoRegistradoComoOperadorDeSuporte() {
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_NAO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.usuarioNaoRegistradoComoOperadorDeSuporte);
	}
	
	@Test
	public void tokenNaoCadastrado() {
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.tokenNaoCadastrado);
	}

	@Test
	public void requisicaoDeDesbloqueioDeTokenNaoExiste() {
		PRE_REGISTRO.faltandoCadastrarSenha();	
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.requisicaoDeDesbloqueioDeTokenNaoExiste);
	}
	
	@Test
	public void caminhoFeliz() {
		RESSOLICITACAO_TOKEN.caminhoFeliz();
		boolean exists1 = new JnEntityRequestTokenAgain().exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists1);
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, ConstantesParaTestesDeSuporte.VALID_EMAIL, this.caminhoFeliz);
		boolean exists = new JnEntityRequestTokenAgainAnswered().exists(ConstantesParaTestesDeLogin.TESTING_JSON);
		assertTrue(exists);
	}
	
	@Test
	public void requisicaoJaRespondida() {
		this.caminhoFeliz();
		this.responderSolicitacaoDeReenvioDeToken(ConstantesParaTestesDeSuporte.USUARIO_REGISTRADO_COMO_OPERADOR_DE_SUPORTE, 
				ConstantesParaTestesDeSuporte.VALID_EMAIL, this.requisicaoJaRespondida);
	}
	
	
	
	private CcpMapDecorator responderSolicitacaoDeReenvioDeToken(Long chatId, String email, Integer expectedStatus) {
		
		String uri = "support/"
				+ chatId
				+ "/token"
				+ "/"
				+ email
				+ "/resending";
		
		CcpMapDecorator testarEndpoint = this.testarEndpoint(uri, expectedStatus);
		return testarEndpoint;
		
	}
	
	@Override
	protected String getMethod() {
		return "POST";
	}
	
	

}
