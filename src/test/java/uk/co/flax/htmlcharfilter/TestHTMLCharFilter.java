package uk.co.flax.htmlcharfilter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static uk.co.flax.util.TokenStreamAssert.assertThat;

/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TestHTMLCharFilter {

    public static final Analyzer WHITESPACE = new WhitespaceAnalyzer(Version.LUCENE_48);

    public static TokenStream tokens(String text) throws IOException {
        return WHITESPACE.tokenStream("field", new HTMLCharFilter(new StringReader(text)));
    }

    @Test
    public void removesTagsButPreservesOffsets() throws IOException {
        String text = "this is <b id=\"moo\">some</b> text";
        assertThat(tokens(text))
                .nextEquals("this", 1, 0, 4)
                .nextEquals("is", 2, 5, 7)
                .nextEquals("some", 3, 20, 24)
                .nextEquals("text", 4, 29, 33)
                .isExhausted();
    }

    @Test
    public void interpretsTagsAsWhitespace() throws IOException {
        String text = "<a>term1</a><b>term2</b>";
        assertThat(tokens(text))
                .nextEquals("term1", 1, 3, 8)
                .nextEquals("term2", 2, 15, 20)
                .isExhausted();
    }

    @Test
    public void correctlyDealsWithEntities() throws IOException {
        String text = "<a>this &amp; th&aacute;t</b> &gt; 3 H&amp;";
        assertThat(tokens(text))
                .nextEquals("this", 1, 3, 7)
                .nextEquals("&", 2, 8, 13)
                .nextEquals("thát", 3, 14, 25)
                .nextEquals(">", 4, 30, 34)
                .nextEquals("3", 5, 35, 36)
                .nextEquals("H&", 6, 37, 43)
                .isExhausted();
    }

    @Test
    public void ignoresComments() throws IOException {
        String text = "term1 <!-- ignore this --> term2";
        assertThat(tokens(text))
                .nextEquals("term1", 1, 0, 5)
                .nextEquals("term2", 2, 27, 32)
                .isExhausted();
    }

}
