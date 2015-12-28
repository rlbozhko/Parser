package com.company;

import com.company.entities.Item;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;


public class Main {

    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c\\d*(/filter/|/)");

    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException, ExecutionException, InterruptedException {
        //TODO delete
        String d = new Date(System.currentTimeMillis()).toString();
        System.out.println(d);

        //TODO delete counter
        int counter = 0;

        boolean bContinue = true;

        final LinkedTransferQueue<Item> transferQueue = new LinkedTransferQueue<>();
        final ConcurrentSkipListSet mainCacheItems = new ConcurrentSkipListSet<>();

        final Set<String> cacheUrls = new HashSet<>();
        final Set<String> newUrls = new HashSet<>();
        final Set<String> oldUrls = new HashSet<>();

        final List<String> badUrls = new ArrayList<>();


        Arguments arguments = new Arguments(args);

        if (!arguments.isValidArguments()) {
            System.out.println(" Неправильные аргументы. Необходимые аргументы:");
            System.out.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.out.println(" цена от(целое число): 1000");
            System.out.println(" цена до(целое число): 1100");
            System.out.println(" Пример аргументов: http://rozetka.com.ua/ 1000 1100");
            return;
        }

        //TODO DELETE
        //arguments.getArg(0)
        //  "http://rozetka.com.ua/equipment/c161187/"
        //  "http://rozetka.com.ua/pressboards/c185692/";
        //  "http://rozetka.com.ua/svarochnoe-oborudovanie/c152563/"
        newUrls.add(arguments.getArg(0));

        ExecutorService service = Executors.newFixedThreadPool(20);
        ArrayList<Future> futures =
                new ArrayList<>();
        Consumer queueConsumer = new Consumer(transferQueue);
        new Thread(queueConsumer).start();

        while (bContinue || newUrls.size() > 0 || cacheUrls.size() > 0) {


            //TODO убрать строчку она только для статистики   cacheUrls.removeAll(oldUrls);
            cacheUrls.removeAll(oldUrls);
            //TODO DELETE
            //counter = newUrls.size();
            newUrls.addAll(cacheUrls);
            newUrls.removeAll(oldUrls);
            //+1
            //TODO DELETE
            //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            //System.out.println("cacheUrls.size =" + cacheUrls.size());
            //System.out.println("oldUrls.size   =" + oldUrls.size());
            //System.out.println("newUrls.size   =" + newUrls.size());
            //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            //System.out.println("Вставили" + (newUrls.size() - counter));
            counter = 0;
            cacheUrls.clear();
            for (String urlBrowse : newUrls) {
                if (oldUrls.add(urlBrowse)) {
                    //TODO DELETE
                    //System.out.println("Качаем страницу 1 ");
                    Parser browsePage = new Parser(urlBrowse);
                    //TODO DELETE
                    //System.out.println("Выкачали страницу 1 ");
                    if (browsePage.getDom() == null) {
                        badUrls.add(browsePage.getUrl());
                    } else {
                        //TODO DELETE
                        //System.out.println("new BIGPAGE ");
                        if ((Boolean) browsePage.jaxp("//*[@id=\"sort_price\"]", XPathConstants.BOOLEAN)) {
                            //TODO DELETE
                            //System.out.println("7777777777777777777 " + urlBrowse);
                            //TODO cacheItems.addALL(parseSortPrice(browsePa......
                            Future tmpFuture =
                                    service.submit(new Producer(browsePage.getUrl(), arguments.getArg(1), arguments.getArg(2), transferQueue, mainCacheItems));
                            futures.add(tmpFuture);
                            //  parseSortPrice(browsePage.getUrl(), arguments.getArg(1), arguments.getArg(2), cacheItems);
                            //TODO DELETE
                            //System.out.println("GOODS Pages Stop");
                            bContinue = false;
                            for (Future future : futures) {
                                if (!future.isDone()) {
                                    bContinue = true;
                                }
                            }
                        } else {
                            //
                            //TODO DELETE
                            //System.out.println("new Pages Start Нет фильтра цен " + urlBrowse);
                            // TODO cacheUrls.addALL(getNewLinks(Parser browsePage));
                            // cacheUrls = Set<String> getNewLinks(Parser browsePage);
                            getNewLinks(cacheUrls, browsePage);
                            //TODO DELETE
                            //System.out.println("new Pages Stop");
                        }
                    }
                }
            }


            bContinue = false;
            for (Future future : futures) {
                if (!future.isDone()) {
                    bContinue = true;
                }
            }
            //TODO DELETE
            //System.out.println("bContinue   = " + bContinue);
        }

        System.out.println("bContinue   = " + bContinue);
        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        System.out.println("cacheUrls.size = " + cacheUrls.size());
        System.out.println("oldUrls.size   = " + oldUrls.size());
        System.out.println("newUrls.size   = " + newUrls.size());
        for (String s : newUrls) {
            System.out.println(s);
        }

        System.out.println("maincacheItems.size()   = " + mainCacheItems.size());
        System.out.println("ROZETKA_CATEGORY   = " + ROZETKA_CATEGORY);
        System.out.println("arguments   = " + arguments);

        System.out.println("badUrls.size   = " + badUrls.size());
        for (
                String s1
                : badUrls)

        {
            System.out.println(s1);
        }

        System.out.println("args   = " + args);
        for (
                String s1
                : args)

        {
            System.out.println(s1);
        }

        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");

        System.out.println(d);
        System.out.println(new

                Date(System.currentTimeMillis()

        ));
        System.out.println("DEBUG   =");
        service.shutdown();
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


}

