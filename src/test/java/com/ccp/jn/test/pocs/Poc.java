package com.ccp.jn.test.pocs;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class Poc {
	static{
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpGsonJsonHandler(), new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp()
				);

	}
	static int counter;
	
	public static void main(String[] args) {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("email", "vh67nwjc@teste.com")
				;
		JnEntityLoginAnswers.INSTANCE.create(put);
	}
	static void salvarVagaDoJobsNowAntigo() {
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				new CcpDbQueryOptions()
					.matchAll()
				;
		CcpFileDecorator file = new CcpStringDecorator("vagas.txt").file();
		String[] resourcesNames = new String[] {"vagas"};
		queryExecutor.consumeQueryResult(
				queryToSearchLastUpdatedResumes, 
				resourcesNames, 
				"10m", 
				10000, 
				vaga -> {
					String texto = vaga.getAsString("vaga").replace("\n", "").trim();
					String completeLeft = new CcpStringDecorator("" + ++counter).text().completeLeft('0', 6).content;
					file.append(completeLeft + ": " + texto);
					System.out.println(counter);
				}, "vaga");
	}
}
