package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SampleJSoup {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
        System.out.println(doc.title());
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
//            log("%s\n\t%s",
//                    headline.attr("title"), headline.absUrl("href"));
//        }
    }
}

