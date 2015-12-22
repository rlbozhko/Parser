package com.company;

import org.htmlcleaner.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Parser {

    public final static String targetUrl ="http://hard.rozetka.com.ua/ssd/c80109/price=1000-3000/";

    public static void main(String[] args) {
        try {
            String html = getPageHtml(targetUrl);
            TagNode tagNode = new HtmlCleaner().clean(
                    html);
            org.w3c.dom.Document doc = new DomSerializer(
                    new CleanerProperties()).createDOM(tagNode);


            XPath xpath = XPathFactory.newInstance().newXPath();
            String str = (String) xpath.evaluate("//*[@id=\"block_with_goods\"]/div[1]",
                    doc, XPathConstants.STRING);
            System.out.println(str);

    /*        HtmlCleaner cleaner = new HtmlCleaner();
            TagNode html = cleaner.clean(new File("source.html"));

            // ������ XPath � �������� title, �������� ������� /html �� �����������
            // ��� ��� ����� � ��� ������������
            String title = html.evaluateXPath("/head/title/text()")[0].toString().trim();
            System.out.println("title\t: " + title);

            // ������� �������� <div> ��� �������� class="menu" � ����� �����
            // � ������� ���� ������� <h1>. ����� ��������� ������ ����, ����� ������
            String header = html.evaluateXPath("//div[@class='menu']/h1/text()")[0].toString().trim();
            System.out.println("header\t: " + header);

            // ������� �������� <div> ��� �������� class="menu" � ����� �����
            // � ������� ���� ������� <a>. ����� ��������� �����, ������� ���.
            Object[] tags = html.evaluateXPath("//div[@class='menu']/a");
            for (Object tag : tags) {
                TagNode aTag = (TagNode) tag;
                String href = aTag.getAttributeByName("href").trim();
                String content = aTag.getText().toString().trim();
                System.out.println("link\t: " + content + "[" + href + "]");
            }

            // ������� ����� �������� <div class="content">
            // TagNode.getText() �� �������� ��� ��� �� ������� ����� ���� ��������
            // ���������
            TagNode contentNode = (TagNode) html.evaluateXPath("//div[@class='content']")[0];
            String contentText = getContent(contentNode).trim();
            System.out.println("content\t: " + contentText);

            // ������� ��� �������� <li>
            tags = html.evaluateXPath("//div[@class='content']/ul/li/text()");
            for (Object tag : tags) {
                System.out.println("color\t: " + tag.toString().trim());
            }

            // ������� ������ �� ��������
            String imageHref = html.evaluateXPath("//div[@class='content']/img[@class='preview']/@src")[0].toString().trim();
            System.out.println("image\t: " + imageHref);

            // ���� ����� �������, ����� �������� ��������� ���� <div class="bottom">
            TagNode bottomNode = (TagNode) html.evaluateXPath("//div[@class='bottom']")[0];
            String bottom = bottomNode.getText().toString().trim();
            System.out.println("bottom\t: " + bottom);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getContent(TagNode node) {
        StringBuilder result = new StringBuilder();
        for (Object item : node.getChildren()) {
            if (item instanceof ContentNode) {
                result.append(((ContentNode) item).getContent());
            }
        }
        return result.toString();
    }

    public static String getPageHtml(String url) {
        String html = "";
        try {
            URL siteUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) siteUrl.openConnection();
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream, "UTF-8");
            html = scanner.useDelimiter("\\A").next();
            scanner.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return html;
    }


}
