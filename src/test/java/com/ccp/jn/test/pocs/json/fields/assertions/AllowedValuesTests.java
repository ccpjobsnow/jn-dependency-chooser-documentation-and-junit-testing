package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.fields.validations.enums.AllowedValuesValidations;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class AllowedValuesTests {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}

	@Test
	public void arrayWithAllowedNumbers() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
		.put("field1", "[1, 3.1]")
		.put("field2", "[2, 3.1]")
		.put("field3", "[1, 2]");
		assertTrue(AllowedValuesValidations.arrayWithAllowedNumbers.isValidJson(json, Arrays.asList("1", "2", "3.1"), "field1", "field2", "field3"));
	}

	@Test
	public void arrayWithAllowedTexts() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
		.put("field1", "['A', 'B']")
		.put("field2", "['B', 'C']")
		.put("field3", "['A', 'C']")
		;
		assertTrue(AllowedValuesValidations.arrayWithAllowedTexts.isValidJson(json, Arrays.asList("A", "B", "C"), "field1", "field2", "field3"));
	}

	@Test
	public void objectWithAllowedNumbers() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
		.put("field1", "1")
		.put("field2", "2")
		.put("field3", "3");
//		assertTrue(json.itIsTrueThatTheFollowingFields("field1", "field2", "field3").ifTheyAreAll().numbersThenEachOneIsContainedAtTheList(1d, 2d, 3d));
		assertTrue(AllowedValuesValidations.objectWithAllowedNumbers.isValidJson(json, 
				Arrays.asList("1", "2", "3"), "field1", "field2", "field3"));
	}

	@Test
	public void objectWithAllowedTexts() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
		.put("field1", "A")
		.put("field2", "B")
		.put("field3", "C")
		;
//		assertTrue(json.itIsTrueThatTheFollowingFields("field1", "field2", "field3").ifTheyAreAll().textsThenEachOneIsContainedAtTheList("A", "B", "C"));
		assertTrue(AllowedValuesValidations.objectWithAllowedTexts.isValidJson(json, 
				Arrays.asList("A", "B", "C"), "field1", "field2", "field3"));
	}
	
}
