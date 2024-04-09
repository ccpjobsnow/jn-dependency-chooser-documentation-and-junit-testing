package com.ccp.jn.test.pocs;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpStringDecorator;

public class ContarMensagensDoTelegram {

	public static String transform(String palavra) {
		String s = "";
//		new CcpStringDecorator(line).text().stripAccents()
		char[] charArray = new CcpStringDecorator(palavra).text().stripAccents().toUpperCase().toCharArray();

		for (char c : charArray) {
			if (c < '0') {
				continue;
			}
			if (c > 'Z') {
				continue;
			}

			s += c;
		}
		return s;
	}

	static int negados = 0;
	static int aceitos = 0;

	public static void main(String[] args) throws IOException {
		File pasta = new File("chats");
		BufferedWriter writer = new BufferedWriter(new FileWriter("saida.html"));
		Set<String> hashes = new HashSet<>();
		StringBuilder content = new StringBuilder();
		for (File arquivo : pasta.listFiles()) {
			System.out.println(arquivo.getName() + ": " + hashes.size());
			BufferedReader reader = new BufferedReader(new FileReader(arquivo));
			String line;
			boolean insideDiv = false;
			while ((line = reader.readLine()) != null) {
				if (line.contains("<div class=\"text\">")) {
					insideDiv = true;
					// content.append(line).append("\n");
				} else if (insideDiv) {
					if (line.contains("</div>")) {
						insideDiv = false;
						content.setLength(0);
						continue;
					}

					if (line.length() < 135) {
						continue;
					}
					String hash = getHash(line);

					if (hashes.add(hash) == false) {
						System.out.println("Negados: " + ++negados);
						continue;
					}
					content.append(line).append("\n");
					writer.write(content.toString());
					System.out.println("Aceitos: " + ++aceitos);
				}
			}}
			new CcpStringDecorator("vagas.txt").file().readLines((lineValue, lineNumber) -> {
				
				String substring = lineValue.substring(8);
				
				if (substring.length() < 135) {
					return;
				}
				
				String hash = getHash(substring);

				if (hashes.add(hash) == false) {
					System.out.println("Negados: " + ++negados);
					return;
				}
				content.append(substring).append("\n");
				try {
					writer.write(content.toString());
					content.setLength(0);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				System.out.println("Aceitos: " + ++aceitos);
			});
		
	}

	private static String getHash(String line) {
		String delimitadores = "\\/|\\s|\n|\\:|\\,|\\;|\\!|\\?|\\[|\\]|\\{|\\}|\\<|\\>|\\=|\\(|\\)\\ |\\'|\\\"|\\`";
		String[] split = line.split(delimitadores);
		List<String> asList = Arrays.asList(split).stream().map(x -> transform(x)).filter(x -> x.length() > 2)
				.collect(Collectors.toList());
		TreeSet<String> treeSet = new TreeSet<String>(asList);
		String hash = new CcpStringDecorator(treeSet.toString()).hash().asString("SHA1");
		return hash;
	}
}