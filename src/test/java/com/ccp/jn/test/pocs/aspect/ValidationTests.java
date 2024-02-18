package com.ccp.jn.test.pocs.aspect;

import java.util.Map;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.fields.validations.annotations.ObjectRules;
import com.ccp.fields.validations.annotations.ValidationRules;
import com.ccp.fields.validations.enums.ObjectValidations; 

public class ValidationTests {
	@Test() 
	public void teste() {
		new Pojo().teste(CcpConstants.EMPTY_JSON.content);
	}

	public static class Pojo {
		@ValidationRules(simpleObjectRules = {
				@ObjectRules(rule = ObjectValidations.requiredFields, fields = { "password" }) })
		void teste(Map<String, Object> json) {
		}

	} 
}
