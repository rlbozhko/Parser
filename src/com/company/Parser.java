package com.company;

import org.htmlcleaner.*;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class Parser {

    private HtmlCleaner cleaner;
    private Document dom;
    private String url;
    private XPath xpath;
    private TagNode rootHtml;


    public Parser(String url) throws ParserConfigurationException {
        this.url = url;
        cleaner = new HtmlCleaner();
        xpath = XPathFactory.newInstance().newXPath();
        try {
            rootHtml = cleaner.clean(URI.create(url).toURL());
            dom = new DomSerializer(new CleanerProperties()).createDOM(rootHtml);
        } catch (IOException e) {
            System.out.println("BADURL "+url);
            cleaner = null;
            dom = null;
            xpath = null;
            rootHtml = null;
        }

    }

    public String getUrl() {
        return url;
    }

    public Document getDom() {
        return dom;
    }

    public String findText(String xp) throws XPatherException {
        return findText(xp, rootHtml);
    }

    public String findText(String xp, TagNode t) throws XPatherException {
        return t.evaluateXPath(xp)[0].toString();
    }

    public TagNode findOneNode(String xp) throws XPatherException {
        return findOneNode(xp, rootHtml);
    }

    private TagNode findOneNode(String xp, TagNode parent) throws XPatherException {
        Object[] result = parent.evaluateXPath(xp);
        return result != null && result.length > 0 ? (TagNode) result[0] : null;
    }

    public TagNode[] findAllNodes(String xp) throws XPatherException {
        return findAllNodes(xp, rootHtml);
    }

    public TagNode[] findAllNodes(String xp, TagNode parent) throws XPatherException {
        Object[] result = parent.evaluateXPath(xp);
        return result != null && result.length > 0 ? Arrays.asList(result).toArray(new TagNode[]{}) : null;
    }

    public String jaxp(String xp) throws XPathExpressionException {
        return (String) xpath.evaluate(xp,
                getDom(), XPathConstants.STRING);
    }

    public Object jaxp(String xp, QName xPathConstants) throws XPathExpressionException {
        return xpath.evaluate(xp,
                getDom(), xPathConstants);
    }


}
