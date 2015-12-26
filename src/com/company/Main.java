package com.company;

import com.company.entities.Item;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Main {
    public static Set<String> newUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> oldUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> cacheUrls = Collections.synchronizedSet(new HashSet<>());

    public static Set<Item> cacheItems = Collections.synchronizedSet(new HashSet<>());


    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c[0-9]*/[^=]*");

    //TODO delete counter
    private static int counter = 0;


    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException {
        System.out.println(new Date(System.currentTimeMillis()));
        List<String> badUrls = Collections.synchronizedList(new ArrayList<>());

        Arguments arguments = new Arguments(args);

        if (!arguments.isValidArguments()) {
            System.out.println("Неправильные аргументы. Необходимые аргументы: ");
            System.out.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.out.println(" цена от(целое число): 1000");
            System.out.println(" цена до(целое число): 1100");
            return;
        }

        String url = arguments.getArg(0);

        //url = "http://rozetka.com.ua/pressboards/c185692/";
        newUrls.add(url);

        Boolean flagContinue;
        do {
            flagContinue = false;
            cacheUrls.clear();
            counter = 0;
            newUrls.removeAll(oldUrls);
            for (String urlBrowse : newUrls) {
                if (oldUrls.add(urlBrowse)) {

                    Parser browsePage = new Parser(urlBrowse);
                    if (browsePage.getDom() == null) {
                        badUrls.add(browsePage.getUrl());
                    } else {
                        if ((Boolean) browsePage.jaxp("//*[@id=\"sort_price\"]", XPathConstants.BOOLEAN)) {
                            //                          System.out.println("7777777777777777777 " + urlBrowse);

                            ParseSortPrice(browsePage, arguments.getArg(1), arguments.getArg(2), cacheItems);
                        } else {
                            //                          System.out.println("Нет фильтра цен " + urlBrowse);

                            NodeList nodes = (NodeList) browsePage.jaxp("//a[contains(@href,'rozetka.com.ua')]/@href", XPathConstants.NODESET);
                            for (int i = 0; i < nodes.getLength(); i++) {
                                String href = (nodes.item(i).getNodeValue());
                                if (ROZETKA_CATEGORY.matcher(href).matches()) {
                                    if (cacheUrls.add(href)) {
                                        counter++;
                                        //                                      System.out.println("кэш+ " + counter + " " + href);
                                        flagContinue = true;
                                    }
                                }
                            }

                        }
                    }
                }
            }
            cacheUrls.removeAll(oldUrls);
            counter = newUrls.size();
            newUrls.addAll(cacheUrls);
/*          //+1
 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("cacheUrls.size =" + cacheUrls.size());
            System.out.println("oldUrls.size   =" + oldUrls.size());
            System.out.println("newUrls.size   =" + newUrls.size());
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Вставили" + (newUrls.size() - counter));*/
        } while (flagContinue && cacheUrls.size() > 0);
/*        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        System.out.println("cacheUrls.size =" + cacheUrls.size());
        System.out.println("oldUrls.size   =" + oldUrls.size());
        System.out.println("newUrls.size   =" + newUrls.size());
        for (String s : newUrls) {
            System.out.println(s);
        }
        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");*/


     /*   Parser mainPage = new Parser(url);

        TagNode mainMenu = mainPage.findOneNode("//nav/ul");
        TagNode[] categories = mainPage.findAllNodes("//li", mainMenu);
        int i = 0;
        for (TagNode category : categories) {
            i++;
            System.out.println(String.format("%d. %s", i, mainPage.findText("//a/text()[last()]", category).trim()));
        }*/
        System.out.println(new Date(System.currentTimeMillis()));
    }

    public static void ParseSortPrice(Parser browsePage, String minPrice, String maxPrice, Set<Item> cacheItems) throws ParserConfigurationException, XPatherException, XPathExpressionException {
        int page = 0;
        Parser mainPage;
        TagNode blockWithGoods;
        do {
            page++;
            String sortedUrl = browsePage.getUrl() + "page=" + page + ";" + "price=" + minPrice.trim() + "-" + maxPrice.trim() + "/";
            System.out.println(sortedUrl);


            mainPage = new Parser(sortedUrl);

            blockWithGoods = mainPage.findOneNode("//*[@id='block_with_goods']/div[1]");
            if (blockWithGoods != null) {
                //     TagNode[] goods = mainPage.findAllNodes("//a[contains(@onclick,'goodsTitleClick')]", blockWithGoods);

                NodeList nodes = (NodeList) mainPage.jaxp("//a[contains(@onclick,'goodsTitleClick')]", XPathConstants.NODESET);
                TagNode[] prices = mainPage.findAllNodes("//div[@class='g-price-uah']", blockWithGoods);
                String name;
                String price;


                for (int i = 0; i < nodes.getLength(); i++) {
                    name = (nodes.item(i).getTextContent()).trim();
                    price = mainPage.findText("/text()", prices[i]).trim().replaceAll("&thinsp;", "");

                    System.out.println(name);
                    System.out.println(price);
                    cacheItems.add(new Item(name, price));
                }
            }
        }
        // Продолжать цикл если на странице есть //div[@name="more_goods"]
        while (blockWithGoods != null && mainPage.findOneNode("//div[@name=\"more_goods\"]", blockWithGoods) != null);
        //  while (blockWithGoods != null && (Boolean) mainPage.jaxp("//*[@id='block_with_goods']/div[1]//div[@name=\"more_goods\"]", XPathConstants.BOOLEAN));
    }
}

