package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.http.CcpHttpMethods;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.test.asserting.JnTemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnLanguage;

public class AoEntrarNaTelaDoCadastroDeSenha extends JnTemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.criarTokenDeLogin(VariaveisParaTeste.INVALID_EMAIL, StatusCreateLoginToken.statusInvalidEmail);
	} 
	 
	@Test
	public void tokenBloqueado() { 
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.ENTITY.getTwinEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginToken.statusLockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.execute(variaveisParaTeste, StatusCreateLoginToken.statusMissingEmail);
	}
	
	@Test
	public void faltandoPreRegistro() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusCreateLoginToken.missingSaveAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginAnswers.ENTITY.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		this.execute(variaveisParaTeste, StatusCreateLoginToken.expectedStatus);
	}

	
	
	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus, Function<VariaveisParaTeste, String> producer) {
		this.criarTokenDeLogin(variaveisParaTeste.VALID_EMAIL, expectedStatus);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
	}
	
	private void criarTokenDeLogin(String email, CcpProcessStatus expectedStatus) {
		String uri = "login/"
				+ email
				+ "/token/language/"+ JnLanguage.portuguese.name();
		this.testarEndpoint(uri, expectedStatus);
	}
	
	
	protected CcpHttpMethods getMethod() {
		return CcpHttpMethods.POST;
	}

}
