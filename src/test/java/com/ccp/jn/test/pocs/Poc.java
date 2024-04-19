package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class Poc {
	static{
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());

	}
	static int counter;
	
	public static void main(String[] args) {
		System.out.println("Raiz quadrada de 37 eh " + ehPrimo(37));
	}

	static boolean ehPrimo(int numero) {
		int raizQuadradaAproximada = getRaizQuadradaAproximada(1, numero);
		
		List<Integer> primos = new ArrayList<>();
		
		for(int contador = 1; contador <= raizQuadradaAproximada; contador += 2) {
			boolean ehPrimo = ehPrimo(contador);
			if(ehPrimo) {
				primos.add(contador);
			}
		}
		
		Integer[] primos2 = primos.toArray(new Integer[primos.size()]);
		
		for (int primo : primos2) {
			boolean esteNumeroEhDivisor = numero % primo == 0;
			if(esteNumeroEhDivisor) {
				return false;
			}
		}
		
		return true;
	}
	
	static int getRaizQuadradaAproximada(int memoria, int numero) {
		
		int resposta = memoria * memoria;
		if(resposta == numero) {
			return memoria;
		}

		if(resposta > numero) {
			return memoria - 1;
		}
		int raizQuadradaAproximada = getRaizQuadradaAproximada(memoria + 1, numero);
		return raizQuadradaAproximada;
	
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
					String completeLeft = new CcpStringDecorator("" + ++counter).text().completeLeft('0', 6);
					file.append(completeLeft + ": " + texto);
					System.out.println(counter);
				}, "vaga");
	}
}
