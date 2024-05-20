package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginToken;

public class TelaParaConfirmacaoDeEmail  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.confirmarEmail(VariaveisParaTeste.INVALID_EMAIL, StatusCreateLoginEmail.invalidEmail);
	}
	
	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.lockedToken);
	}
	
	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		CcpEntity mirrorEntity = JnEntityLoginPassword.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.lockedPassword);
	}
	
	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.loginConflict);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.missingPassword);
	}
	
	@Test
	public void faltandoPreRegistro() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		this.execute(variaveisParaTeste, StatusCreateLoginEmail.expectedStatus);
	}
	//
	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus, Function<VariaveisParaTeste, String> producer) {
		this.confirmarEmail(variaveisParaTeste.VALID_EMAIL, expectedStatus);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
	}
	
	private CcpJsonRepresentation confirmarEmail(String email, CcpProcessStatus expectedStatus) {
		
		String uri = "/login/"
		+ email
		+ "/token";
		CcpJsonRepresentation asSingleJson = this.testarEndpoint(uri, expectedStatus);
		return asSingleJson;
	}


	
	protected String getMethod() {
		return "POST";
	}

}
