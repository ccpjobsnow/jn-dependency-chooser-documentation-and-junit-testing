package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class SimpleValidationsTests {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}

	@Test
	public void fieldArrayNumbersThatAreContainedAtTheList() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
		.put("field1", "[1, 3.1]")
		.put("field2", "[2, 3]")
		.put("field2", "[1, 2]");
		assertTrue(json.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIsContainedAtTheList(1d, 2d, 3.1d));
	}

	@Test
	public void fieldArrayTextsThatAreContainedAtTheList() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
//		.put("field1", "['A', 'B']")
		.put("field2", "['B', 'C']")
//		.put("field2", "['A', 'D']")
		;
		assertTrue(json.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isTextAndItIsContainedAtTheList("A", "B", "D"));
	}
	
}
