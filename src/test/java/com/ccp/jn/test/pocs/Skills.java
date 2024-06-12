package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class Skills {

	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpElasticSearchDbRequest(), 
				new CcpElasticSearchCrud(),
				new CcpGsonJsonHandler(), 
				new CcpApacheMimeHttp()
				);
		
		List<String> lines = new CcpStringDecorator("documentation\\skills\\classificacao\\restou.txt").file().getLines();
		gravarArquivo(lines, "documentation\\skills\\classificacao\\profissoes.txt", "P");
		gravarArquivo(lines, "documentation\\skills\\classificacao\\excluidos.txt", "E");
		gravarArquivo(lines, "documentation\\skills\\classificacao\\skills.txt", "S");
	}

	static void gravarArquivo(
			List<String> lines,
			String nomeDoArquivoOndeGravar,
			String sigla
			) {
			
			List<String> collect = lines.stream().filter(line -> line.startsWith(sigla)).map(line -> new CcpJsonRepresentation(line.substring(1)).getAsString("skill")).collect(Collectors.toList());
			TreeSet<String> words = new TreeSet<>(collect);
			CcpFileDecorator reset = new CcpStringDecorator(nomeDoArquivoOndeGravar).file().reset();
			for (String word : words) {
				reset.append(word);
			}
			
	}

	static void levantarSkills() {
		Set<String> novasPalavras = new HashSet<>();
		
		Set<String> sinonimos_frases = readSkills("documentation\\skills\\sinonimos_frases.txt");
		
		Set<String> sinonimos = readSkills("documentation\\skills\\sinonimos.txt");

		Set<String> levantadas = read("documentation\\skills\\levantadas.json");


		Set<String> antigos_termos = readTermosEstudaveis("documentation\\skills\\antigos_termos.json");

		Set<String> profissoes = readSkills("documentation\\skills\\profissoes.txt");

		novasPalavras.addAll(sinonimos_frases);
		novasPalavras.addAll(antigos_termos);
		novasPalavras.addAll(levantadas);
		novasPalavras.addAll(profissoes);
		novasPalavras.addAll(sinonimos);
		System.out.println(novasPalavras.size());
		Set<String> listaNegra = readSkills("documentation\\skills\\listaNegra.txt");
		Set<String> readFolder = readFolder();
		listaNegra.addAll(readFolder);
		for (String string : listaNegra) {
			novasPalavras.remove(string);
		}
		System.out.println(novasPalavras.size());
		List<String> vagas = new CcpStringDecorator("documentation\\skills\\vagas.txt")
				.file().getLines().stream().map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
		
		List<CcpJsonRepresentation> ocorrencias = new ArrayList<>();
		
		int k = 0;
		
		for (String skill : novasPalavras) {
			long count = vagas.stream().filter(vaga -> contains(vaga, skill)).count();
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("skill", skill).put("count", count);
			ocorrencias.add(put);
			System.out.println(k++ + " = " + put);
		}
		ocorrencias = ocorrencias.stream().filter(x -> x.getAsIntegerNumber("count") >= 2).collect(Collectors.toList());
		List<String> skills = ocorrencias.stream().map(x -> x.getAsString("skill")).collect(Collectors.toList());
		Set<String> set = new HashSet<>();
		for (CcpJsonRepresentation ocorrencia : ocorrencias) {
			String asString = ocorrencia.getAsString("skill");
			for (String skill : skills) {
				boolean equalsIgnoreCase = asString.equalsIgnoreCase(skill);
				
				if(equalsIgnoreCase) {
					continue;
				}
				
				String trim2 = skill.replace(" ", "");
				
				boolean differentPhrases = trim2.contains(asString) == false;
				
				if(differentPhrases) {
					continue;
				}
				
				List<String> asList = Arrays.asList(asString.split(" "));
				List<String> asList2 = Arrays.asList(skill.split(" "));
				boolean notContains = asList2.containsAll(asList) == false;
				if(notContains) {
					continue;
				}
				
				set.add(asString);
				break;
			}
			
		}	
		ocorrencias.sort((a, b) -> sort(a, b));
		CcpFileDecorator estatisticas = new CcpStringDecorator("documentation\\skills\\estatisticas.txt").file().reset();
		
		for (CcpJsonRepresentation ocorrencia : ocorrencias) {
			String skill = ocorrencia.getAsString("skill");
			boolean contains = set.contains(skill);
			if(contains) {
//				continue;
			}
			String asUgglyJson = ocorrencia.asUgglyJson();
			estatisticas.append(asUgglyJson);
		}
	}
	static boolean contains(String text, String phrase) {
		boolean notContains = text.contains(phrase) == false;
		if(notContains) {
			return false;
		}
		
		List<String> asList2 = Arrays.asList(text.split(" "));
		List<String> asList = Arrays.asList(phrase.split(" "));
		boolean containsAll = asList2.containsAll(asList);
		return containsAll;
	}
	private static int sort(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		int i = a.getAsString("skill").compareTo(b.getAsString("skill"));
		return i;
	}
	

	static Set<String> readSkills(String path){
		Set<String> result = new HashSet<String>();
		new CcpStringDecorator(path).file().readLines((line, x) -> {
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		});
		
		System.out.println(result.size());
		return result;
	}
	
	static Set<String> readTermosEstudaveis(String path) {
		Set<String> result = new HashSet<String>();
		String extractStringContent = new CcpStringDecorator(path).file().extractStringContent();
		CcpJsonRepresentation data = new CcpJsonRepresentation(extractStringContent);
		List<CcpJsonRepresentation> collect = data.getInnerJson("hits").getAsJsonList("hits");
		for (CcpJsonRepresentation json : collect) {
			String line = json.getAsString("_id");
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		}
		System.out.println(result.size());
		return result;
	
	}	
	static Set<String> read(String path) {
		String extractStringContent = new CcpStringDecorator(path).file().extractStringContent();
		List<Map<String, Object>> list = CcpDependencyInjection.getDependency(CcpJsonHandler.class).fromJson(extractStringContent);
		List<CcpJsonRepresentation> collect = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		Set<String> result = new HashSet<String>();
		for (CcpJsonRepresentation json : collect) {
			String line = json.getAsString("nome");
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		}
		System.out.println(result.size());
		return result;
	}
	
	static Set<String> getAllSkills(String word) {
		String upperCase = sanitizeWord(word);
		String[] split = upperCase.replace("=>", ",").replace("(", ",").replace("/", ",").replace(")", "").split(",");
		Set<String> asList = Arrays.asList(split).stream().map(x -> sanitizeWord(x))
				.filter(x -> x.length() > 2)
				.collect(Collectors.toSet());
		
		return asList;
	}


	private static String sanitizeWord(String word) {
		CcpTextDecorator text = new CcpStringDecorator(word).text();
		String upperCase = text.stripAccents().content.trim().toUpperCase()
				.replace("+", "")
				;
		
		String[] split = upperCase.split(" ");
		
		for (String string : split) {
			boolean doubleNumber = new CcpStringDecorator(string).isDoubleNumber();
			if(doubleNumber) {
				upperCase = upperCase.replace(string, "").trim();
			}
		}
		int length = upperCase.length();
		if(length == 0) {
			return "";
		}
		String last = "" + upperCase.charAt(length - 1);
		
		try {
			Integer.valueOf(last);
			return upperCase.substring(0, length - 1);
		} catch (Exception e) {
			return upperCase;
		}
	}
	
	static Set<String> readFolder(){
		Set<String> result = new HashSet<String>();
		
		new CcpStringDecorator("documentation\\skills\\classificacao")
		.folder().readFiles(file -> {
			file.readLines((line, number) -> {
				try {
					
					CcpJsonRepresentation json = new CcpJsonRepresentation(line);
					String skill = json.getAsString("skill");
					result.add(skill);
				} catch (Exception e) {
				}
			});
		});;
		
		System.out.println(result.size());
		return result;

	}
}
