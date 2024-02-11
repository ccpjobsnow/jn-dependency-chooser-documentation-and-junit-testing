package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class IfTheyAreAllArrayValuesThenEachOneIsNumberAndItIs {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}
	
	
	@Test
	public void equalsTo() {
		List<Integer> asList = Arrays.asList(5,5,5);
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList)

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsTo(5d));

	}

	@Test
	public void greaterOrEqualsTo() {
		List<Integer> asList = Arrays.asList(5,5,5);
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrGreaterThan(4d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrGreaterThan(5d));
	}

	@Test
	public void lessOrEqualsTo() {
		List<Integer> asList = Arrays.asList(5,5,5);
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrLessThan(6d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().equalsOrLessThan(5d));
	}

	
	@Test
	public void greaterThan() {
		List<Integer> asList = Arrays.asList(5,5,5);
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().greaterThan(4d));
	}

	@Test
	public void lessThan() {
		List<Integer> asList = Arrays.asList(0,1,-2);
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", Arrays.asList(3,4,5)) 
				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().isNumberAndItIs().lessThan(6d));
	}

	
}
