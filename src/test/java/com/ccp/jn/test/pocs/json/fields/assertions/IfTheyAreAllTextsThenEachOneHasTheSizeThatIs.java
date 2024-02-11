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

public class IfTheyAreAllTextsThenEachOneHasTheSizeThatIs {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}
	
	
	@Test
	public void equalsTo() {
		String str = "onias";
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", str)
				.put("field2", str)
				;
		
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsTo(5d));

	}

	@Test
	public void greaterOrEqualsTo() {
		String str = "onias";
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", str)
				.put("field2", str) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrGreaterThan(4d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrGreaterThan(5d));
	}

	@Test
	public void lessOrEqualsTo() {
		String str = "onias";
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", str)
				.put("field2", str) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrLessThan(6d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().equalsOrLessThan(5d));
	}

	
	@Test
	public void greaterThan() {
		String str = "onias";
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", str)
				.put("field2", str) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().greaterThan(4d));
	}

	@Test
	public void lessThan() {
		String str = "onias";
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", str)
				.put("field2", str) 
				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAll().textsThenEachOneHasTheSizeThatIs().lessThan(6d));
	}

	
}
