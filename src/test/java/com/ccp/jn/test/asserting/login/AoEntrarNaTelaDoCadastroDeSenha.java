package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;

public class AoEntrarNaTelaDoCadastroDeSenha extends TemplateDeTestes{

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
		this.execute(variaveisParaTeste, StatusCreateLoginToken.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.ENTITY.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginAnswers.ENTITY.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		//FIXME CORRIGIR O ENVIO DO E-MAIL
		this.execute(variaveisParaTeste, StatusCreateLoginToken.statusExpectedStatus);
	}
	//
	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus, Function<VariaveisParaTeste, String> producer) {
		this.criarTokenDeLogin(variaveisParaTeste.VALID_EMAIL, expectedStatus);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
	}
	
	private void criarTokenDeLogin(String email, CcpProcessStatus expectedStatus) {
		String uri = "login/"
				+ email
				+ "/token/language/portuguese";
		this.testarEndpoint(uri, expectedStatus);
	}
	
	
	protected String getMethod() {
		return "POST";
	}

}
