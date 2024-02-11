package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.WrongType;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class IfTheyAreAllArrayValuesThenEachOneHasTheSizeThatIs {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}
	
	@Test(expected = WrongType.class)
	public void wrongType() {
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field2", 1)
				;
		givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsTo(3d);
	}
	
	@Test
	public void equalsTo() {
		List<String> asList = Arrays.asList("onias", "saino", "teste");
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList)

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsTo(3d));

	}

	@Test
	public void greaterOrEqualsTo() {
		List<String> asList = Arrays.asList("onias", "saino", "teste");
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsOrGreaterThan(2d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsOrGreaterThan(3d));
	}

	@Test
	public void lessOrEqualsTo() {
		List<String> asList = Arrays.asList("onias", "saino", "teste");
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsOrLessThan(4d));
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().equalsOrLessThan(3d));
	}

	
	@Test
	public void greaterThan() {
		List<String> asList = Arrays.asList("onias", "saino", "teste");
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", asList) 

				;
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().greaterThan(2d));
	}

	@Test
	public void lessThan() {
		List<String> asList = Arrays.asList("onias", "saino", "teste");
		CcpJsonRepresentation givenTheJson = CcpConstants.EMPTY_JSON
				.put("field1", asList)
				.put("field2", Arrays.asList("onias", "saino", "teste")) 
				;
		
		assertTrue(givenTheJson.itIsTrueThatTheFollowingFields("field1", "field2").ifTheyAreAllArrayValuesThenEachOne().hasTheSizeThatIs().lessThan(4d));
	}

	
}
