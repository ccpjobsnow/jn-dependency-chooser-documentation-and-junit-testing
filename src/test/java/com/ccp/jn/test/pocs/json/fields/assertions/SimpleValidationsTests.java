package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
	public void booleanFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "false").put("field2", true);
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2").areAllOfTheType().bool());
	}

	@Test
	public void doubleFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "1.5").put("field2", 2.5).put("field3", 3).put("field4", 4);
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2", "field3", "field4").areAllOfTheType().doubleNumber());
	}
	
	@Test
	public void integerFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "1").put("field2", 2).put("field3", 3.0).put("field4", "4.0");
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2", "field3", "field4").areAllOfTheType().longNumber());
	}
	
	@Test
	public void jsonFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", CcpConstants.EMPTY_JSON).put("field2", CcpConstants.EMPTY_JSON.put("teste", 1));
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2").areAllOfTheType().json());
	}


	@Test
	public void listFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", Arrays.asList()).put("field2","[1, 2]").put("field3","['A', 'B']").put("field4","[{}, {'nome':'onias', 'idade':38}]");
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2", "field3", "field4").areAllOfTheType().list());
	}

	@Test
	public void nonRepeatedLists() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
				.put("field1", Arrays.asList(1, 2, 3))
				.put("field2", Arrays.asList(1d, 2d, 3d))
				.put("field3","['A', 'B', 'C']")
				.put("field4","[1.0, 2.0, 3.0]")
				.put("field5","[true, false]")
				;
		assertTrue( json.itIsTrueThatTheFollowingFields("field1", "field2", "field3", "field4", "field5").ifTheyAreAllArrayValuesThenEachOne().hasNonDuplicatedItems());
	}
}