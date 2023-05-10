package com.yatin.tcd.customAnalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yatin.tcd.utils.Constants;

public class CustomAnalyzer extends Analyzer {
	
    public static Logger logger = LoggerFactory.getLogger(CustomAnalyzer.class);
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new ClassicFilter(tokenizer);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, getStopWords());
        tokenStream = new PorterStemFilter(tokenStream);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
    
    private CharArraySet getStopWords(){
        CharArraySet stopwords = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(Constants.STOPWORD_FILE));
            String[] words = new String(encoded, StandardCharsets.UTF_8).split("\n");
            stopwords =  new CharArraySet(Arrays.asList(words), true);
        } catch (IOException ioe) {
            logger.error("Error reading stopwords file" + Constants.STOPWORD_FILE, ioe);
        }
        return stopwords;
    }
}

