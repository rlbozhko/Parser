package com.company;

import com.company.entities.Item;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;



public class ThreadWorker implements Callable<Set<Item>> {

    private String url;
    String minPrice;
    String maxPrice;
    Set<Item> threadCacheItems;


    public ThreadWorker(String url, String minPrice, String maxPrice) {
        this.url = url;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.threadCacheItems = Collections.synchronizedSet(new HashSet<>());;
    }

    public Set<Item> call() throws Exception {


        parseSortPrice(url,  minPrice, maxPrice, threadCacheItems);


        return threadCacheItems;
    }

    public  void parseSortPrice(String url, String minPrice, String maxPrice, Set<Item> cacheItems) throws ParserConfigurationException, XPatherException, XPathExpressionException {
        int page = 0;
        Parser mainPage;
        TagNode blockWithGoods;
        do {
            page++;
            String sortedUrl = url + "page=" + page + ";" + "price=" + minPrice.trim() + "-" + maxPrice.trim() + "/";
            System.out.println(sortedUrl);

            System.out.println("WКачаем страницу с уст фильтром ");
            mainPage = new Parser(sortedUrl);
            if (mainPage.getDom() == null) {
       //         badUrls.add(mainPage.getUrl());
                blockWithGoods = null;
            } else {
                System.out.println("WВыкачали страницу с уст фильтром ");

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
        }
        // Продолжать цикл если на странице есть //div[@name="more_goods"]
        while (blockWithGoods != null && mainPage.findOneNode("//div[@name=\"more_goods\"]", blockWithGoods) != null);
        //  while (blockWithGoods != null && (Boolean) mainPage.jaxp("//*[@id='block_with_goods']/div[1]//div[@name=\"more_goods\"]", XPathConstants.BOOLEAN));
    }


}
