package com.company;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    public static Map<String, Boolean> browseLinkMap = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, Boolean> addLinkMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c[0-9]*/.*");
    private static int counter = 0;

    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException {


        //пример ввода для теста
        String[] args1 = new String[3];
        args1[0] = "http://rozetka.com.ua/";
        args1[1] = "2000";
        args1[2] = "2500";


// после теста в этой строчке args1 поменять на args
        Arguments arguments = new Arguments(args1);

        if (!arguments.isValidArguments()) {
            System.out.println("Неправильные аргументы. Необходимые аргументы: ");
            System.out.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.out.println(" цена от(целое число): 1000");
            System.out.println(" цена до(целое число): 1100");
            return;
        }

        String url = arguments.getArg(0);
        url = "http://rozetka.com.ua/";
//url = "http://rozetka.com.ua/pressboards/c185692/";
        browseLinkMap.put(url, true);
        Boolean flagContinue;
        do {
            addLinkMap.clear();
            counter=0;
            for (Map.Entry<String, Boolean> entry : browseLinkMap.entrySet()) {
                if (entry.getValue()) {
                    entry.setValue(false);
                    String urlBrowse = entry.getKey();
                    Parser browsePage = new Parser(urlBrowse);
                    if ((Boolean) browsePage.jaxp("//*[@id=\"sort_price\"]", XPathConstants.BOOLEAN)) {
                        System.out.println("7777777777777777777 " + urlBrowse);
                        //TODO:метод сохранения Имя+Цена
                    } else {
                        System.out.println("Нет фильтра цен " + urlBrowse);

                        NodeList nodes = (NodeList) browsePage.jaxp("//a[contains(@href,'rozetka.com.ua')]/@href", XPathConstants.NODESET);
                        for (int i = 0; i < nodes.getLength(); i++) {
                            String href = (nodes.item(i).getNodeValue());
                            if (ROZETKA_CATEGORY.matcher(href).matches()) {
                                addLinkMap.put(href, true);
                                counter++;
                                System.out.println(href + "Вставили" + counter);
                            }
                        }
                        System.out.println("Вставили" + counter);
                    }
                }
            }
            browseLinkMap.putAll(addLinkMap);
        } while (addLinkMap.size()>0);


        Parser mainPage = new Parser(url);

        TagNode mainMenu = mainPage.findOneNode("//nav/ul");
        TagNode[] categories = mainPage.findAllNodes("//li", mainMenu);
        int i = 0;
        for (TagNode category : categories) {
            i++;
            System.out.println(String.format("%d. %s", i, mainPage.findText("//a/text()[last()]", category).trim()));
        }

    }
}
