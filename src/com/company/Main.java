package com.company;

import com.company.entities.Item;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

static Set<Item>cacheItems=Collections.synchronizedSet(new HashSet<>());

public class Main {
    public static Set<String> newUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> oldUrls = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> cacheUrls = Collections.synchronizedSet(new HashSet<>());

    static Set<Item> cacheItems = Collections.synchronizedSet(new HashSet<>());

    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c[0-9]*/");
    private static int counter = 0;

    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException {
        List<String> badUrls = Collections.synchronizedList(new ArrayList<>());

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
                            System.out.println("7777777777777777777 " + urlBrowse);
                            //TODO:метод сохранения Имя+Цена
                            ParseSortPrice(browsePage, arguments.getArg(1), arguments.getArg(2), HashSet < Item > cacheItems);
                        } else {
                            System.out.println("Нет фильтра цен " + urlBrowse);

                            NodeList nodes = (NodeList) browsePage.jaxp("//a[contains(@href,'rozetka.com.ua')]/@href", XPathConstants.NODESET);
                            for (int i = 0; i < nodes.getLength(); i++) {
                                String href = (nodes.item(i).getNodeValue());
                                if (ROZETKA_CATEGORY.matcher(href).matches()) {
                                    if (cacheUrls.add(href)) {
                                        counter++;
                                        System.out.println("кэш+ " + counter + " " + href);
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
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("cacheUrls.size =" + cacheUrls.size());
            System.out.println("oldUrls.size   =" + oldUrls.size());
            System.out.println("newUrls.size   =" + newUrls.size());
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

            System.out.println("Вставили" + (newUrls.size() - counter));
        } while (flagContinue && cacheUrls.size() > 0);
        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        System.out.println("cacheUrls.size =" + cacheUrls.size());
        System.out.println("oldUrls.size   =" + oldUrls.size());
        System.out.println("newUrls.size   =" + newUrls.size());
        for (String s : newUrls) {
            System.out.println(s);
        }
        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");


     /*   Parser mainPage = new Parser(url);

        TagNode mainMenu = mainPage.findOneNode("//nav/ul");
        TagNode[] categories = mainPage.findAllNodes("//li", mainMenu);
        int i = 0;
        for (TagNode category : categories) {
            i++;
            System.out.println(String.format("%d. %s", i, mainPage.findText("//a/text()[last()]", category).trim()));
        }*/

    }

    public static void ParseSortPrice(Parser browsePage, String minPrice, String maxPrice, HashSet<Item> cachItems) {
        int page = 1;
        String sortedUrl = url + "page=" + page + ";" + "price=" + minPrice + "-" + maxPrice + "/";
        System.out.println(sortedUrl);

        //Todo ParseSortPrice
        // Продолжать цикл если на странице есть //div[@name="more_goods"]

        //вытащить   // "//*[@id=\"block_with_goods\"]/div[1]"
        //вытащить все <div class="g-price-uah">2?255<span class="g-price-uah-sign">?грн</span></div>
        //<a href="http://hard.rozetka.com.ua/transcend_ts256gssd360s/p6553018/" onclick="document.fireEvent('goodsTitleClick', {extend_event: [{name: 'goods_id', value: 6553018}]}); return true">
        //Transcend SSD360S Premium 256GB 2.5" SATA III MLC (TS256GSSD360S)
        //        </a>
    }

}
