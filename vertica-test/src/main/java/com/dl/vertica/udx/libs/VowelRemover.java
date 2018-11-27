package com.dl.vertica.udx.libs;

public class VowelRemover {
	public String removevowels(String input) {
		return input.replaceAll("(?i)[aeiouy]", "");
	}
}
