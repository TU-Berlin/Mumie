/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.mumie.cocoon.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 *  Extended Analyzer for German language. Supports an external list of stopwords (words that
 * will not be indexed at all) and an external list of exclusions (word that will
 * not be stemmed, but indexed). Additionally it replace some important XML-entities 
 * for the german language. A default set of stopwords is used unless an alternative 
 * list is specified, the exclusion list is empty by default.
 *
 * @author Gerhard Schwarz, Helmut Vieritz
 * @version $Id: GermanEntityAnalyzer.java,v 1.3 2006/11/03 10:25:19 rassy Exp $
 */
public class GermanEntityAnalyzer extends Analyzer {
	
  /**
   * List of typical german stopwords.
   */
  private String[] GERMAN_STOP_WORDS = {
    "einer", "eine", "eines", "einem", "einen",
    "der", "die", "das", "des", "dass", "daß", "den",
    "du", "er", "sie", "es", "zu", "ich", "zur",
    "was", "wer", "wie", "wir", "sei", "ein",
    "und", "oder", "ohne", "mit", "seien", 
    "am", "im", "in", "aus", "auf",
    "ist", "sein", "war", "wird", "sind", "werden",
    "ihr", "ihre", "ihres", "so", "auch", "dem", "kann",
    "als", "für", "von", "mit", "nach",
    "dich", "dir", "mich", "mir",
    "mein", "sein", "kein", "vor",
    "durch", "wegen", "wird", "sodass"
  };

  /**
   * Contains the stopwords used with the StopFilter.
   */
  private Set stopSet = new HashSet();

  /**
   * Contains words that should be indexed but not stemmed.
   */
  private Set exclusionSet = new HashSet();

  /**
   * Builds an analyzer.
   */
  public GermanEntityAnalyzer() {
    stopSet = StopFilter.makeStopSet(GERMAN_STOP_WORDS);
  }

  /**
   * Builds an analyzer with the given stop words.
   */
  public GermanEntityAnalyzer(String[] stopwords) {
    stopSet = StopFilter.makeStopSet(stopwords);
  }

  /**
   * Builds an analyzer with the given stop words.
   */
  public GermanEntityAnalyzer(Hashtable stopwords) {
    stopSet = new HashSet(stopwords.keySet());
  }

  /**
   * Builds an analyzer with the given stop words.
   */
  public GermanEntityAnalyzer(File stopwords) throws IOException {
    stopSet = WordlistLoader.getWordSet(stopwords);
  }

  /**
   * Builds an exclusionlist from an array of Strings.
   */
  public void setStemExclusionTable(String[] exclusionlist) {
    exclusionSet = StopFilter.makeStopSet(exclusionlist);
  }

  /**
   * Builds an exclusionlist from a Hashtable.
   */
  public void setStemExclusionTable(Hashtable exclusionlist) {
    exclusionSet = new HashSet(exclusionlist.keySet());
  }

  /**
   * Builds an exclusionlist from the words contained in the given file.
   */
  public void setStemExclusionTable(File exclusionlist) throws IOException {
    exclusionSet = WordlistLoader.getWordSet(exclusionlist);
  }

  /**
   * Creates a TokenStream which tokenizes all the text in the provided Reader.
   *
   * @return A TokenStream build from a StandardTokenizer filtered with
   *         StandardFilter, LowerCaseFilter, StopFilter, GermanStemFilter
   */
  public TokenStream tokenStream(String fieldName, Reader reader) {
    TokenStream result = new StandardTokenizer(reader);
    result = new StandardFilter(result);
    result = new EntityFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, stopSet);
    result = new GermanStemFilter(result, exclusionSet);
    return result;
  }
}
