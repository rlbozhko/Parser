package com.company;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {
    public static Set<String> newUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> oldUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> casheUrls = Collections.synchronizedSet(new HashSet<>());
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
        newUrls.add(url);

        Boolean flagContinue;
        do {
            flagContinue = false;
            casheUrls.clear();
            counter = 0;
            newUrls.removeAll(oldUrls);
            for (String urlBrowse : newUrls) {
                if (oldUrls.add(urlBrowse)) {

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
                                casheUrls.add(href);
                                counter++;
                                System.out.println(href + "Вставили" + counter);
                                flagContinue = true;
                            }
                        }
                        System.out.println("Вставили" + counter);
                    }
                }
            }
            casheUrls.removeAll(oldUrls);
            newUrls.addAll(casheUrls);
        } while (flagContinue && casheUrls.size() > 0);


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
