package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\skills.json").file().extractStringContent();
		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
	
		CcpFileDecorator file = new CcpStringDecorator("documentation\\skills\\classificacao.txt").file();
		List<String> lines = file.getLines();
		List<String> collect = lines.stream()
		.filter(x -> x.startsWith("(1)"))
		.map(x -> x.split("\\)")[1].split(":")[0].trim())
		.map(x -> Integer.valueOf(x))
		.map(x -> skills.get(x))
		.filter(x -> x.getAsJsonList("synonyms").isEmpty())
		.map(x -> x.getAsString("word"))
		.collect(Collectors.toList());
		;
		for (String string : collect) {
			System.out.println(string);
		}
	}

	static void levantarSkillsAvulsas(String... palavras) {
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\skills.json").file().extractStringContent();
		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
		for (CcpJsonRepresentation skill : skills) {
			boolean incorrectJson = skill.containsAllFields("somatoria") == false;
			if(incorrectJson) {
				continue;
			}
			Integer somatoria = skill.getAsIntegerNumber("somatoria");
			Integer ranking = skill.getAsIntegerNumber("ranking") - 1;
			String word = skill.getAsString("word");
			System.out.println(ranking + ": " + word + " = " + somatoria);
		}
	}
	static void criarArquivosFinais() {
		//		criarRelatoriosDosArquivos();
				String extractStringContent = new CcpStringDecorator("documentation\\skills\\classificacao\\skills.json").file().extractStringContent();
				List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
				List<String> classificados = new CcpStringDecorator("documentation\\skills\\classificacao\\perguntar.txt").file().getLines();
				List<String> perguntasParaGemini = classificados.stream().filter(x -> x.trim().endsWith("(1)")).map(x -> Integer.valueOf(x.split(":")[0]))
				.map(x -> skills.get(x)).map(x -> {
					String word = x.getAsString("word");
					Set<String> set = x.getAsJsonList("synonyms").stream().map(y -> y.getAsString("skill")).collect(Collectors.toSet());
					ArrayList<String> arrayList = new ArrayList<String>(set);
					arrayList.add(word);
					return arrayList.toString().replace("[", "").replace("]", "");
				}).collect(Collectors.toList());
				
				
				CcpFileDecorator arquivoDePerguntasParaGemini = new CcpStringDecorator("documentation\\skills\\classificacao\\arquivoDePerguntasParaGemini.txt").file().reset();
					
				for (String perguntaParaGemini : perguntasParaGemini) {
					arquivoDePerguntasParaGemini.append(perguntaParaGemini);
				}
				
				List<CcpJsonRepresentation> skillsQueNaoSaoDeTecnologiaParaPopularTabelaDeSkills = classificados.stream().filter(x -> x.trim().endsWith("(1)") == false).map(x -> Integer.valueOf(x.split(":")[0]))
				.map(x -> skills.get(x)).collect(Collectors.toList());
				String extractStringContent2 = new CcpStringDecorator("documentation\\skills\\classificacao\\profissoes.json").file().extractStringContent();
				List<CcpJsonRepresentation> profissoesParaPopularTabelaDeSkills = new CcpJsonRepresentation(extractStringContent2).getAsJsonList("linhas").subList(0, 151);
				
				ArrayList<CcpJsonRepresentation> primeirosRegistrosDaTabelaDeSkills = new ArrayList<>(profissoesParaPopularTabelaDeSkills);
				primeirosRegistrosDaTabelaDeSkills.addAll(skillsQueNaoSaoDeTecnologiaParaPopularTabelaDeSkills);
				
				CcpFileDecorator arquivoComPrimeirosRegistrosDaTabelaDeSkills = new CcpStringDecorator("documentation\\skills\\classificacao\\listSkills.json").file().reset();
				arquivoComPrimeirosRegistrosDaTabelaDeSkills.append(primeirosRegistrosDaTabelaDeSkills.toString());
	}
	

	static void criarRelatoriosDosArquivos() {
		List<String> vagas = new CcpStringDecorator("documentation\\skills\\vagas.txt")
				.file().getLines().stream().map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
		
		criarRelatorioDeUmArquivo("documentation\\skills\\classificacao\\gemini.txt", "documentation\\skills\\classificacao\\skills.json", vagas);
		criarRelatorioDeUmArquivo("documentation\\skills\\classificacao\\profissoes.txt", "documentation\\skills\\classificacao\\profissoes.json", vagas);
	}

	static void criarRelatorioDeUmArquivo(String arquivoParaLer, String arquivoParaEscrever, List<String> vagas) {
	
		CcpFileDecorator fileToRead = new CcpStringDecorator(arquivoParaLer).file();
		
		List<String> lines2 = fileToRead.getLines();
		
		Collection<String> lines = new HashSet<>(lines2);
		
		List<CcpJsonRepresentation> relatorios = lines.stream().map(line -> getRelatorioDeUmaLinha(line, vagas)).collect(Collectors.toList());
		
		long mediaGeral = 0;
		long totalGeralDeSkills = 0;
		long totalGeralDeVagasEncontradas = 0;
		int k = 1;
		for (CcpJsonRepresentation relatorio : relatorios) {
			System.out.println("lendo a linha " + k++);
			boolean isIncompleteProcess = relatorio.containsAllFields("somatoria", "listSize") == false;
			if(isIncompleteProcess) {
				continue;
			}
			Integer somatoria = relatorio.getAsIntegerNumber("somatoria");
			Integer listSize = relatorio.getAsIntegerNumber("listSize");
			totalGeralDeVagasEncontradas += somatoria;
			totalGeralDeSkills += listSize;
		}
		mediaGeral = totalGeralDeVagasEncontradas / totalGeralDeSkills;
		relatorios.sort((a, b) -> ordernarLinhasNoArquivo(a, b));
		
		int ranking = 1;
		List<CcpJsonRepresentation> linhas = new ArrayList<>();
		
		for (CcpJsonRepresentation relatorio : relatorios) {
			String word = relatorio.getValueFromPath("", "skill", "skill"); 
			CcpJsonRepresentation putAll = CcpConstants.EMPTY_JSON
					.put("ranking", ranking++)
					.put("word", word)
					.putAll(relatorio)
					;
			linhas.add(putAll);
		}
		
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
		.put("linhas", linhas)
		.put("mediaGeral", mediaGeral)
		.put("totalGeralDeSkills", totalGeralDeSkills)
		.put("totalGeralDeVagasEncontradas", totalGeralDeVagasEncontradas);
		
		CcpFileDecorator fileToWrite = new CcpStringDecorator(arquivoParaEscrever).file().reset();
		fileToWrite.append(put.toString());
	}
	
	static CcpJsonRepresentation getRelatorioDeUmaLinha(String linha, List<String> vagas) {
		String[] split = linha.split(",");
		List<String> asList = Arrays.asList(split);
		Set<String> collect = asList.stream()
		.map(x -> sanitizeWord(x)).filter(x -> x.trim().length() >= 2)
		.collect(Collectors.toSet());
		
		boolean hasNoWords = collect.isEmpty();
		if(hasNoWords) {
			return CcpConstants.EMPTY_JSON;
		}
		
		List<CcpJsonRepresentation> jsons = collect.stream().map(skill -> getRelatorioDeUmaPalavra(skill, vagas)).collect(Collectors.toList());
		
		int somatoria = 0;
		for (CcpJsonRepresentation json : jsons) {
			Integer count = json.getAsIntegerNumber("vagas");
			somatoria += count;
		}
		int listSize = jsons.size();
		int media = somatoria / listSize;
		ArrayList<CcpJsonRepresentation> synonyms = new ArrayList<>(jsons);
		synonyms.sort((a, b) -> ordenarSkillsNaLinha(a, b));
		CcpJsonRepresentation skill = synonyms.remove(0);
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("somatoria", somatoria)
				.put("listSize", listSize)
				.put("media", media)
				.put("skill", skill)
				.put("synonyms", synonyms)
				;
		
		return put;
	}
	
	static int ordernarLinhasNoArquivo(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		boolean m1 = a.containsAllFields("somatoria", "listSize", "media", "skill") == false;
		if(m1) {
			return 0;
		}
		
		boolean m2 = b.containsAllFields("somatoria", "listSize", "media", "skill") == false;
		if(m2) {
			return 0;
		}
		
		Integer somatoria1 = a.getAsIntegerNumber("somatoria");
		Integer somatoria2 = b.getAsIntegerNumber("somatoria");
		
		if(somatoria1 != somatoria2) {
			int subtracao = somatoria2 - somatoria1;
			return subtracao;
		}

		Long count1 = a.getValueFromPath(0L, "skill", "vagas");
		Long count2 = b.getValueFromPath(0l, "skill", "vagas");
		
		if(count2 != count1) {
			int subtracao = (int)(count2 - count1);
			return subtracao;
		}

		Integer media1 = a.getAsIntegerNumber("media");
		Integer media2 = b.getAsIntegerNumber("media");
		
		if(media1 != media2) {
			int subtracao = media2 - media1;
			return subtracao;
		}

		String skill1 = a.getValueFromPath("", "skill", "skill");
		String skill2 = b.getValueFromPath("", "skill", "skill");
		
		int compareTo = skill1.compareTo(skill2);
		return compareTo;
	}
	
	static int ordenarSkillsNaLinha(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		
		Integer count1 = a.getAsIntegerNumber("vagas");
		Integer count2 = b.getAsIntegerNumber("vagas");
		
		if(count1 != count2) {
			int sub = count2 - count1;
			return sub;
		}
		
		String skill1 = a.getAsString("skill");
		String skill2 = b.getAsString("skill");
		int compareTo = skill1.compareTo(skill2);
		return compareTo;
	}
	
	static CcpJsonRepresentation getRelatorioDeUmaPalavra(String palavra, List<String> vagas) {
		
		long count = vagas.stream().filter(vaga -> contains(vaga, palavra)).count();
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("skill", palavra).put("vagas", count);
	
		return put;
	}
	
	static void gravarArquivo(
			List<String> lines,
			String nomeDoArquivoOndeGravar,
			String sigla
			) {
			
			List<String> palavrasClassificadas = lines.stream().filter(line -> line.startsWith(sigla)).map(line -> new CcpJsonRepresentation(line.substring(1)).getAsString("skill")).collect(Collectors.toList());
			TreeSet<String> classifiedWords = new TreeSet<>(palavrasClassificadas);
			CcpFileDecorator reset = new CcpStringDecorator(nomeDoArquivoOndeGravar).file();
			List<String> palavrasQueJaEstavam = reset.getLines();
			LinkedHashSet<String> set = new LinkedHashSet<>();
			for (String string : palavrasQueJaEstavam) {
				String[] split = string.split(",");
				for (String word : split) {
					set.add(word.trim().toUpperCase());
				}
			}
			int k = 1;
			for (String word : classifiedWords) {
				String upperCase = word.trim().toUpperCase();
				boolean add = set.add(upperCase);
				if(add) {
					reset.append(upperCase);
					System.out.println(k++);
				}
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
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("skill", skill).put("vagas", count);
			ocorrencias.add(put);
			System.out.println(k++ + " = " + put);
		}
		ocorrencias = ocorrencias.stream().filter(x -> x.getAsIntegerNumber("vagas") >= 2).collect(Collectors.toList());
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
