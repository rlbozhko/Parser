package com.company;

import com.company.entities.Item;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TransferQueue;


public class Producer implements Runnable {

    private String url;
    private String minPrice;
    private String maxPrice;
    private TransferQueue<Item> transferQueue;
    private ConcurrentSkipListSet mainCacheItems;

    public Producer(String url, String minPrice, String maxPrice, TransferQueue<Item> transferQueue, ConcurrentSkipListSet mainCacheItems) {
        this.url = url;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.transferQueue = transferQueue;
        this.mainCacheItems = mainCacheItems;
    }

    @Override
    public void run() {
        try {
            parseSortPrice(url, minPrice, maxPrice);
        } catch (ParserConfigurationException | XPathExpressionException | XPatherException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void parseSortPrice(String url, String minPrice, String maxPrice) throws ParserConfigurationException, XPatherException, XPathExpressionException, InterruptedException {
        int page = 0;
        Parser mainPage;
        TagNode blockWithGoods;

        do {
            page++;
            String sortedUrl = url + "page=" + page + ";" + "price=" + minPrice.trim() + "-" + maxPrice.trim() + "/";
            //TODO DELETE
            //System.out.println(sortedUrl);
            //TODO DELETE
            //System.out.println("WКачаем страницу с уст фильтром ");
            mainPage = new Parser(sortedUrl);
            if (mainPage.getDom() == null) {
                blockWithGoods = null;
            } else {
                //TODO DELETE
                //System.out.println("WВыкачали страницу с уст фильтром ");

                blockWithGoods = mainPage.findOneNode("//*[@id='block_with_goods']/div[1]");
                if (blockWithGoods != null) {
                    //TagNode[] goods = mainPage.findAllNodes("//div[@class="g-i-tile-i-title clearfix"]/a/text()", blockWithGoods);
                    NodeList nodes = (NodeList) mainPage.jaxp("//a[contains(@onclick,'goodsTitleClick')]", XPathConstants.NODESET);
                    TagNode[] prices = mainPage.findAllNodes("//div[@class='g-price-uah']", blockWithGoods);
                    String name;
                    String price;


                    for (int i = 0; i < nodes.getLength(); i++) {
                        name = (nodes.item(i).getTextContent()).trim().replaceAll("\n", "");
                        price = mainPage.findText("/text()", prices[i]).trim().replaceAll("&thinsp;", "").replaceAll("\n", "");
                        //TODO DELETE
                        // System.out.println(name);
                        // System.out.println(price);
                        Item item = new Item(name, price);
                        if (mainCacheItems.add(item)) {
                            if (transferQueue.size() < 100) {
                                transferQueue.add(item);
                            } else {
                              //  transferQueue.transfer(item);
                            }
                        }


                    }
                }
            }
        }
        // Продолжать цикл если на странице есть //div[@name="more_goods"]
        while (blockWithGoods != null && mainPage.findOneNode("//div[@name=\"more_goods\"]", blockWithGoods) != null);
        //  while (blockWithGoods != null && (Boolean) mainPage.jaxp("//*[@id='block_with_goods']/div[1]//div[@name=\"more_goods\"]", XPathConstants.BOOLEAN));
    }


}
