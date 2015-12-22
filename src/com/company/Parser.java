package com.company;


import org.htmlcleaner.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class Parser {

    public static void main(String[] args) {
        try {
//
            TagNode tagNode = new HtmlCleaner().clean(
                    "<div><table><td id='1234 foo 5678'>Hello</td>");
            org.w3c.dom.Document doc = new DomSerializer(
                    new CleanerProperties()).createDOM(tagNode);


            XPath xpath = XPathFactory.newInstance().newXPath();
            String str = (String) xpath.evaluate("//div//td[contains(@id, 'foo')]/text()",
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
}
