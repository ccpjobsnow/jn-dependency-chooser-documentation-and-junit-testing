package com.ccp.jn.test.pocs.json.fields.assertions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.fields.validations.enums.ObjectValidations;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class ObjectTests {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());		
	}

	@Test
	public void booleanFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "false").put("field2", true);
		assertTrue(ObjectValidations.booleanFields.isValidJson(json, "field1", "field2"));
	}

	@Test
	public void doubleFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "1.5").put("field2", 2.5).put("field3", 3).put("field4", 4);
		assertTrue(ObjectValidations.doubleFields.isValidJson(json, "field1", "field2", "field3", "field4"));
}
	
	@Test
	public void integerFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", "1").put("field2", 2).put("field3", 3.0).put("field4", "4.0");
		assertTrue(ObjectValidations.integerFields.isValidJson(json, "field1", "field2", "field3", "field4"));
	}
	
	@Test
	public void jsonFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", CcpConstants.EMPTY_JSON).put("field2", CcpConstants.EMPTY_JSON.put("teste", 1));
		assertTrue(ObjectValidations.jsonFields.isValidJson(json, "field1", "field2", "field3", "field4"));
	}


	@Test
	public void listFields() {
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON.put("field1", Arrays.asList()).put("field2","[1, 2]").put("field3","['A', 'B']").put("field4","[{}, {'nome':'onias', 'idade':38}]");
		assertTrue(ObjectValidations.listFields.isValidJson(json, "field1", "field2", "field3", "field4"));
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
		assertTrue(ObjectValidations.nonRepeatedLists.isValidJson(json, "field1", "field2", "field3", "field4", "field5"));
	}
}
