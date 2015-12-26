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
    public final static Set<String> newUrls = Collections.synchronizedSet(new HashSet<>());
    public final static Set<String> oldUrls = Collections.synchronizedSet(new HashSet<>());


    public final static Set<Item> cacheItems = Collections.synchronizedSet(new HashSet<>());


    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c[0-9]*/[^=]*");




    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException {
        //TODO delete counter
        int counter =0;

        Set<String> cacheUrls = Collections.synchronizedSet(new HashSet<>());
        System.out.println(new Date(System.currentTimeMillis()));
        List<String> badUrls = Collections.synchronizedList(new ArrayList<>());

        Arguments arguments = new Arguments(args);

        if (!arguments.isValidArguments()) {
            System.out.println("Неправильные аргументы. Необходимые аргументы: ");
            System.out.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.out.println(" цена от(целое число): 1000");
            System.out.println(" цена до(целое число): 1100");
            System.out.println(" Пример аргументов: http://rozetka.com.ua/ 1000 1100");
            return;
        }

        //arguments.getArg(0)
        //  "http://rozetka.com.ua/equipment/c161187/"
        //  "http://rozetka.com.ua/pressboards/c185692/";
        //  "http://rozetka.com.ua/svarochnoe-oborudovanie/c152563/"
        newUrls.add(arguments.getArg(0));

        while (newUrls.size() > 0) {

            //TODO убрать строчку она только для статистики   cacheUrls.removeAll(oldUrls);
            cacheUrls.removeAll(oldUrls);
            counter = newUrls.size();
            newUrls.addAll(cacheUrls);
            newUrls.removeAll(oldUrls);
/*          //+1
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("cacheUrls.size =" + cacheUrls.size());
            System.out.println("oldUrls.size   =" + oldUrls.size());
            System.out.println("newUrls.size   =" + newUrls.size());
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Вставили" + (newUrls.size() - counter));*/
            counter = 0;
            cacheUrls.clear();
            for (String urlBrowse : newUrls) {
                if (oldUrls.add(urlBrowse)) {
                    Parser browsePage = new Parser(urlBrowse);
                    if (browsePage.getDom() == null) {
                        badUrls.add(browsePage.getUrl());
                    } else {
                        System.out.println("new BIGPAGE");
                        if ((Boolean) browsePage.jaxp("//*[@id=\"sort_price\"]", XPathConstants.BOOLEAN)) {
                            System.out.println("GOODS Pages Start");
                            // System.out.println("7777777777777777777 " + urlBrowse);
                            //TODO cacheItems.addALL(parseSortPrice(browsePa......
                            parseSortPrice(browsePage.getUrl(), arguments.getArg(1), arguments.getArg(2), cacheItems);
                            System.out.println("GOODS Pages Stop");
                        } else {
                            System.out.println("new Pages Start Нет фильтра цен"+ urlBrowse);
                            // TODO cacheUrls.addALL(getNewLinks(Parser browsePage));
                            // cacheUrls = Set<String> getNewLinks(Parser browsePage);
                            getNewLinks(cacheUrls, browsePage);
                            System.out.println("new Pages Stop");
                        }
                    }
                }
            }

        }
/*        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        System.out.println("cacheUrls.size =" + cacheUrls.size());
        System.out.println("oldUrls.size   =" + oldUrls.size());
        System.out.println("newUrls.size   =" + newUrls.size());
        for (String s : newUrls) {
            System.out.println(s);
        }
        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");*/

        System.out.println(new Date(System.currentTimeMillis()));
    }

    private static void getNewLinks(Set<String> cacheUrls, Parser browsePage) throws XPathExpressionException {
        NodeList nodes = (NodeList) browsePage.jaxp("//a[contains(@href,'rozetka.com.ua')]/@href", XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            String href = (nodes.item(i).getNodeValue());
            if (ROZETKA_CATEGORY.matcher(href).matches()) {
                cacheUrls.add(href);
            }
        }
    }

    public static void parseSortPrice(String url, String minPrice, String maxPrice, Set<Item> cacheItems) throws ParserConfigurationException, XPatherException, XPathExpressionException {
        int page = 0;
        Parser mainPage;
        TagNode blockWithGoods;
        do {
            page++;
            String sortedUrl = url + "page=" + page + ";" + "price=" + minPrice.trim() + "-" + maxPrice.trim() + "/";
            System.out.println(sortedUrl);


            mainPage = new Parser(sortedUrl);

            blockWithGoods = mainPage.findOneNode("//*[@id='block_with_goods']/div[1]");
            if (blockWithGoods != null) {
                //TagNode[] goods = mainPage.findAllNodes("//div[@class="g-i-tile-i-title clearfix"]/a/text()", blockWithGoods);
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

