package com.jointsky.edps.spider.extractor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import com.hankcs.hanlp.HanLP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.UrlUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * From https://github.com/CrawlScript/WebCollector
 */
public class ContentExtractor {

    private static final String REMOVE_TAGS = "script,noscript,style,footer,foot,nav";
    private Map<Element, CountInfo> infoMap = new HashMap<>();
    private Document doc;

    private ContentExtractor(Document doc) {
        this.doc = doc;
    }

    private ContentExtractor(String html) {
        this.doc = Jsoup.parse(html);
    }

    class CountInfo {
        int textCount = 0;
        int linkTextCount = 0;
        int tagCount = 0;
        int linkTagCount = 0;
        double density = 0;         //密度
        double densitySum = 0;
        public double score = 0;
        int pCount = 0;
        ArrayList<Integer> leafList = new ArrayList<>();

        void combineInfo(CountInfo countInfo){
            this.textCount += countInfo.textCount;
            this.linkTextCount += countInfo.linkTextCount;
            this.tagCount += countInfo.tagCount;
            this.linkTagCount += countInfo.linkTagCount;
            this.leafList.addAll(countInfo.leafList);
            this.densitySum += countInfo.density;
            this.pCount += countInfo.pCount;
        }
    }

    private void clean() {
        if (doc != null){
            doc.select(REMOVE_TAGS).remove();
        }
    }

    private CountInfo computeCountInfo(Node node) {
        if (node instanceof Element) {
            Element tag = (Element) node;
            CountInfo countInfo = new CountInfo();
            for (Node childNode : tag.childNodes()) {
                countInfo.combineInfo(computeCountInfo(childNode));
            }
            countInfo.tagCount++;
            String tagName = tag.tagName();
            if (tagName.equals("a")) {
                countInfo.linkTextCount = countInfo.textCount;
                countInfo.linkTagCount++;
            } else if (tagName.equals("p")) {
                countInfo.pCount++;
            }
            int pureLen = countInfo.textCount - countInfo.linkTextCount;
            int len = countInfo.tagCount - countInfo.linkTagCount;
            if (pureLen == 0 || len == 0) {
                countInfo.density = 0;
            } else {
                countInfo.density = (pureLen + 0.0) / len;
            }
            infoMap.put(tag, countInfo);
            return countInfo;
        }

        if (node instanceof TextNode) {
            TextNode tn = (TextNode) node;
            CountInfo countInfo = new CountInfo();
            String text = tn.text();
            int len = text.length();
            countInfo.textCount = len;
            countInfo.leafList.add(len);
            return countInfo;
        }
        return new CountInfo();
    }

    private double computeScore(Element tag) {
        CountInfo countInfo = infoMap.get(tag);
        double var = Math.sqrt(computeVar(countInfo.leafList) + 1);
        return Math.log(var) * countInfo.densitySum * Math.log(countInfo.textCount - countInfo.linkTextCount + 1) * Math.log10(countInfo.pCount + 2);
    }

    /**
     * 计算 VAR（variance）方差
     */
    private double computeVar(ArrayList<Integer> data) {
        if (data.size() == 0) {
            return 0;
        }
        if (data.size() == 1) {
            return data.get(0) / 2.0;
        }
        double sum = 0;
        for (Integer i : data) {
            sum += i;
        }
        double avg = sum / data.size();
        sum = 0;
        for (Integer i : data) {
            sum += (i - avg) * (i - avg);
        }
        sum = sum / data.size();
        return sum;
    }

    private Element getContentElement(){
        clean();
        computeCountInfo(doc.body());
        double maxScore = 0;
        Element content = null;
        for (Map.Entry<Element, CountInfo> entry : infoMap.entrySet()) {
            Element tag = entry.getKey();
            if (tag.tagName().equals("a") || tag == doc.body()) {
                continue;
            }
            double score = computeScore(tag);
            if (score > maxScore) {
                maxScore = score;
                content = tag;
            }
        }
        return content;
    }

    private String getTime(Element contentElement) {
        String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-2]?[1-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-9]{1,2})";
        Pattern pattern = Pattern.compile(regex);
        Element current = contentElement;
        for (int i = 0; i < 2; i++) {
            if (current != null && current != doc.body()) {
                Element parent = current.parent();
                if (parent != null) {
                    current = parent;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (current == null) {
                break;
            }
            String currentHtml = current.outerHtml();
            Matcher matcher = pattern.matcher(currentHtml);
            if (matcher.find()) {
                return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " " + matcher.group(4) + ":" + matcher.group(5) + ":" + matcher.group(6);
            }
            if (current != doc.body()) {
                current = current.parent();
            }
        }
        return getDate(contentElement);
    }

    private String getDate(Element contentElement) {
        String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})";
        Pattern pattern = Pattern.compile(regex);
        Element current = contentElement;
        for (int i = 0; i < 2; i++) {
            if (current != null && current != doc.body()) {
                Element parent = current.parent();
                if (parent != null) {
                    current = parent;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (current == null) {
                break;
            }
            String currentHtml = current.outerHtml();
            Matcher matcher = pattern.matcher(currentHtml);
            if (matcher.find()) {
                return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
            }
            if (current != doc.body()) {
                current = current.parent();
            }
        }
        return null;
    }

    private double strSim(String a, String b) {
        int len1 = a.length();
        int len2 = b.length();
        if (len1 == 0 || len2 == 0) {
            return 0;
        }
        double ratio;
        if (len1 > len2) {
            ratio = (len1 + 0.0) / len2;
        } else {
            ratio = (len2 + 0.0) / len1;
        }
        if (ratio >= 3) {
            return 0;
        }
        return (lcs(a, b) + 0.0) / Math.max(len1, len2);
    }

    private String getTitle(final Element contentElement){
        final ArrayList<Element> titleList = new ArrayList<>();
        final ArrayList<Double> titleSim = new ArrayList<>();
        final AtomicInteger contentIndex = new AtomicInteger();
        final String metaTitle = doc.title().trim();
        if (!metaTitle.isEmpty()) {
            doc.body().traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int i) {
                    if (node instanceof Element) {
                        Element tag = (Element) node;
                        if (tag == contentElement) {
                            contentIndex.set(titleList.size());
                            return;
                        }
                        String tagName = tag.tagName();
                        if (Pattern.matches("h[1-6]", tagName)) {
                            String title = tag.text().trim();
                            double sim = strSim(title, metaTitle);
                            titleSim.add(sim);
                            titleList.add(tag);
                        }
                    }
                }

                @Override
                public void tail(Node node, int i) {
                }
            });
            int index = contentIndex.get();
            if (index > 0) {
                double maxScore = 0;
                int maxIndex = -1;
                for (int i = 0; i < index; i++) {
                    double score = (i + 1) * titleSim.get(i);
                    if (score > maxScore) {
                        maxScore = score;
                        maxIndex = i;
                    }
                }
                if (maxIndex != -1) {
                    return titleList.get(maxIndex).text();
                }
            }
        }

        Elements titles = doc.body().select("*[id^=title],*[id$=title],*[class^=title],*[class$=title]");
        if (titles.size() > 0) {
            String title = titles.first().text().trim();
            if (title.length() > 5 && title.length() < 40) {
                return titles.first().text();
            }
        }
        return getTitleByEditDistance(contentElement);
    }

    private String getTitleByEditDistance(Element contentElement){
        final String metaTitle = doc.title();
        final ArrayList<Double> max = new ArrayList<>();
        max.add(0.0);
        final StringBuilder sb = new StringBuilder();
        doc.body().traverse(new NodeVisitor() {
            public void head(Node node, int i) {
                if (node instanceof TextNode) {
                    TextNode tn = (TextNode) node;
                    String text = tn.text().trim();
                    double sim = strSim(text, metaTitle);
                    if (sim > 0) {
                        if (sim > max.get(0)) {
                            max.set(0, sim);
                            sb.setLength(0);
                            sb.append(text);
                        }
                    }
                }
            }

            public void tail(Node node, int i) {
            }
        });
        if (sb.length() > 0) {
            return sb.toString();
        }
        return metaTitle;
    }

    private int lcs(String x, String y) {
        int M = x.length();
        int N = y.length();
        if (M == 0 || N == 0) {
            return 0;
        }
        int[][] opt = new int[M + 1][N + 1];

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }
        return opt[0][0];
    }

    private int editDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                if (c1 == c2) {
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }
        return dp[len1][len2];
    }

    public static HtmlArticle getArticle(String html){
        return getArticle(new Html(html));
    }

    public static HtmlArticle getArticle(String html, String baseUrl){
        return getArticle(new Html(html, baseUrl));
    }

    private static final Pattern imgPattern = Pattern.compile("<img[^>]+?src=['\"]([^\"']+)");
    public static HtmlArticle getArticle(Html html){
        String baseUri = html.getDocument().baseUri();
        Document document = html.getDocument();
        ContentExtractor extractor = new ContentExtractor(document);
        HtmlArticle article = new HtmlArticle();
        if (StrUtil.isNotBlank(baseUri) && baseUri.startsWith("http")){
            article.setId(SecureUtil.md5(baseUri));
            article.setUrl(baseUri);
            article.setDomain(UrlUtils.getDomain(baseUri));
        }
        article.setTitle(extractor.getTitle(document));
        article.setTime(extractor.getTime(document));
        Element contentElement = extractor.getContentElement();
        try {
            article.setHtml(fixUrl(baseUri, contentElement.html()));
        } catch (MalformedURLException e) {
            article.setHtml(contentElement.html());
        }
        article.setContent(cleanHtml(article.getHtml()));
        article.setKeyword(CollUtil.join(HanLP.extractPhrase(article.getContent(), 5), "；"));
        article.setSummary(CollUtil.join(HanLP.extractSummary(article.getContent(), 3, "。？！!?"), ""));
        List<String> imgList = new ArrayList<>();
        Matcher matcher = imgPattern.matcher(article.getHtml());
        while (matcher.find()){
            imgList.add(matcher.group(1));
        }
        article.setImages(CollUtil.join(imgList, "；"));
        return article;
    }

    /**
     * 基于 baseUrl，补全 html 代码中的链接
     * @param baseUrl 网页源代码地址
     * @param html 网页源代码
     * @throws MalformedURLException URL Exception
     */
    private static String fixUrl(String baseUrl, String html) throws MalformedURLException {
        Pattern pattern = Pattern.compile("(href|src)=[\"']([^\"']+)[\"']",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        URL absoluteUrl = new URL(baseUrl);
        while (matcher.find()){
            String link = matcher.group(2);
            if (link.startsWith("//")){
                html = html.replace(link, (baseUrl.toLowerCase().startsWith("https")? "https:":"http:")+link);
                continue;
            }
            if (link.startsWith("http") || link.startsWith("#") || link.startsWith("javascript")){
                continue;
            }
            URL parseUrl = new URL(absoluteUrl, link);
            html = html.replace(link, parseUrl.toString());
        }
        return html;
    }

    private static String cleanHtml(String html){
        html = HtmlUtil.unescape(html);
        html = HtmlUtil.cleanHtmlTag(html);
        html = html.replaceAll("[ \\t]+"," ").replaceAll("[\\r\\n]+","\n");
        html = html.replaceAll("( *\\n *)+","\n");
        return html.trim();
    }
}
