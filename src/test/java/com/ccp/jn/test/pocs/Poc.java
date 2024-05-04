package com.ccp.jn.test.pocs;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpFolderDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.entities.base.JnBaseEntity;

public class Poc {
	static{
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpGsonJsonHandler(), new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());

	}
	static int counter;
	
	public static void main(String[] args) {
		extracted();
	}

	@SuppressWarnings("unchecked")
	 static void extracted() {
		CcpFolderDecorator folderJava = new CcpStringDecorator("C:\\eclipse-workspaces\\ccp\\jn\\jn-business-commons\\src\\main\\java\\com\\jn\\commons\\entities").folder();
		Collection<Object> java = new ArrayList<>();
		folderJava.readFiles(x -> {
			String name = new File(x.content).getName();
			if("base".equals(name)) {
				return;
			}
			String replace = name.replace(".base", "").replace(".java", "");
			String className = "com.jn.commons.entities." + replace;
			Constructor<JnBaseEntity> declaredConstructor;
			try {
				declaredConstructor = (Constructor<JnBaseEntity>) Class.forName(className).getDeclaredConstructor();
				declaredConstructor.setAccessible(true);
				JnBaseEntity newInstance = declaredConstructor.newInstance();
				String entityName = newInstance.getEntityName();
				java.add(entityName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		Collection<Object> elastic = new ArrayList<>();
		CcpFolderDecorator folderElastic = new CcpStringDecorator("C:\\eclipse-workspaces\\ccp\\jn\\jn-dependency-chooser-documentation-and-junit-testing\\documentation\\database\\elasticsearch\\scripts\\create_table").folder();
		folderElastic.readFiles(x -> {
			String entityName = new File(x.content).getName();
			elastic.add(entityName);
		});
		
		
		List<Object> estaNoJavaMasNaoEstaNoElastic = new CcpCollectionDecorator(java).getExclusiveList(elastic);
		List<Object> estaNoElasticMasNaoEstaNoJava = new CcpCollectionDecorator(elastic).getExclusiveList(java);
		System.out.println(estaNoJavaMasNaoEstaNoElastic);
		System.out.println(estaNoElasticMasNaoEstaNoJava);
	}

	static void levantarNumerosParaFatoracao(int limite, String pessoa) {
		Set<Integer> numeros = new HashSet<>();
		int k = 1;
		outer:while(numeros.size() < limite) {
			System.out.println(k++);
			int numero = (int)(Math.random() * 1_000_0);
			for (Integer outroNumero : numeros) {
				
				if(numero % outroNumero == 0) {
					continue outer;
				}
				
				if(outroNumero % numero == 0) {
					continue outer;
				}
				
				if(numero < 10) {
					
					continue outer;
				}
			}
			numeros.add(numero);
		}
		CcpFileDecorator arquivo = new CcpStringDecorator(pessoa + ".txt").file().reset();
		
		for (Integer numero : numeros) {
			arquivo.append("" + numero);
		}
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
					String completeLeft = new CcpStringDecorator("" + ++counter).text().completeLeft('0', 6).content;
					file.append(completeLeft + ": " + texto);
					System.out.println(counter);
				}, "vaga");
	}
}
