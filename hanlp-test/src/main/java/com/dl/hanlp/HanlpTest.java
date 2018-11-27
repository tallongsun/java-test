package com.dl.hanlp;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

public class HanlpTest {

	public static void main(String[] args) {
		// CoreStopWordDictionary.add(",");

		List<String> texts = getTexts();
		Map<String, Integer> wordFrequency = getWordFrequency(texts);
		List<Map.Entry<String, Integer>> sortedWordFrequency = sortWord(wordFrequency);
		System.out.println(sortedWordFrequency.toString());
		
		drawWordCloud(sortedWordFrequency);

	}

	private static List<String> getTexts() {
		List<String> result = new ArrayList<String>();
		result.add("你好，欢迎使用HanLP！");
		result.add("算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。");
		result.add("算法可以宽泛的分为三类，");
		result.add("一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。");
		result.add("二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。");
		result.add("三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。");
		return result;
	}

	public static Map<String, Integer> getWordFrequency(List<String> texts) {
		Map<String, Integer> result = new LinkedHashMap<>();
		for (String text : texts) {
			List<Term> terms = HanLP.segment(text);
			for (Term term : terms) {
				String word = term.word;
				if (CoreStopWordDictionary.contains(word)) {
					continue;
				}
				if (result.containsKey(word)) {
					result.put(word, result.get(word) + 1);
				} else {
					result.put(word, 1);
				}
			}
		}
		return result;
	}

	public static List<Map.Entry<String, Integer>> sortWord(Map<String, Integer> wordFrequency) {
		List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFrequency.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		return list;
	}

	private static void drawWordCloud(List<Map.Entry<String, Integer>> sortedWordFrequency) {
//		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
//		frequencyAnalyzer.setWordFrequenciesToReturn(500);
//		frequencyAnalyzer.setMinWordLength(4); 
//		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load("text/my_text_file.txt");
		List<WordFrequency> wordFrequencies = sortedWordFrequency.stream().map(e -> new WordFrequency(e.getKey(), e.getValue())).collect(Collectors.toList());
	    final Dimension dimension = new Dimension(600, 600);
	    final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
	    wordCloud.setPadding(2);
	    wordCloud.setBackground(new CircleBackground(300));
	    wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.GREEN, Color.BLUE, 30 , 30));
	    wordCloud.setFontScalar( new SqrtFontScalar(10, 40));
	    wordCloud.build(wordFrequencies);

	    wordCloud.writeToFile("/Users/tallong/tmp/wordcloud.png");
	}
	
}
