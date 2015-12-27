package com.company;

import com.company.entities.Item;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;


public class Main {
    public final static Set<String> newUrls = Collections.synchronizedSet(new HashSet<>());
    public final static Set<String> oldUrls = Collections.synchronizedSet(new HashSet<>());


    public final static Set<Item> mainCacheItems = Collections.synchronizedSet(new HashSet<>());


    private static final Pattern ROZETKA_CATEGORY = Pattern.compile(".*/c\\d*(/filter/|/)");

    private static List<String> badUrls = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException, ExecutionException, InterruptedException {
        //TODO delete counter
        int counter = 0;
        boolean bContinue = true;
        Set<String> cacheUrls = Collections.synchronizedSet(new HashSet<>());
        String d = new Date(System.currentTimeMillis()).toString();
        System.out.println(d);
        List<String> badUrls = Collections.synchronizedList(new ArrayList<>());

        Arguments arguments = new Arguments(args);

        if (!arguments.isValidArguments()) {
            System.out.println(" Неправильные аргументы. Необходимые аргументы:");
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

        ExecutorService service = Executors.newCachedThreadPool();
        List<Future<Set<Item>>> futures =
                new ArrayList<>();


        while (bContinue||newUrls.size() > 0) {

            //TODO убрать строчку она только для статистики   cacheUrls.removeAll(oldUrls);
            cacheUrls.removeAll(oldUrls);
            counter = newUrls.size();
            newUrls.addAll(cacheUrls);
            newUrls.removeAll(oldUrls);
            //+1
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("cacheUrls.size =" + cacheUrls.size());
            System.out.println("oldUrls.size   =" + oldUrls.size());
            System.out.println("newUrls.size   =" + newUrls.size());
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Вставили" + (newUrls.size() - counter));
            counter = 0;
            cacheUrls.clear();
            for (String urlBrowse : newUrls) {
                if (oldUrls.add(urlBrowse)) {
                    System.out.println("Качаем страницу 1 ");
                    Parser browsePage = new Parser(urlBrowse);
                    System.out.println("Выкачали страницу 1 ");
                    if (browsePage.getDom() == null) {
                        badUrls.add(browsePage.getUrl());
                    } else {
                        System.out.println("new BIGPAGE ");
                        if ((Boolean) browsePage.jaxp("//*[@id=\"sort_price\"]", XPathConstants.BOOLEAN)) {
                            System.out.println("GOODS Pages Start");
                            System.out.println("7777777777777777777 " + urlBrowse);
                            //TODO cacheItems.addALL(parseSortPrice(browsePa......
                            Future<Set<Item>> future =
                                    service.submit(new ThreadWorker(browsePage.getUrl(), arguments.getArg(1), arguments.getArg(2)));
                            futures.add(future);
                            //  parseSortPrice(browsePage.getUrl(), arguments.getArg(1), arguments.getArg(2), cacheItems);
                            System.out.println("GOODS Pages Stop");

                            for (Future<Set<Item>> future1 : futures) {
                                bContinue = false;
                                if(future1.isDone()){
                                    mainCacheItems.addAll(future1.get());
                                }else {
                                    bContinue = true;
                                }

                            }



                        } else {
                            //
                            System.out.println("new Pages Start Нет фильтра цен " + urlBrowse);
                            // TODO cacheUrls.addALL(getNewLinks(Parser browsePage));
                            // cacheUrls = Set<String> getNewLinks(Parser browsePage);
                            getNewLinks(cacheUrls, browsePage);
                            System.out.println("new Pages Stop");
                        }
                    }
                }
            }


            for (Future<Set<Item>> future1 : futures) {
                bContinue = false;
                if(future1.isDone()){
                    mainCacheItems.addAll(future1.get());
                }else {
                     bContinue = true;
                }

            }





            System.out.println("bContinue   = " + bContinue);
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
        for (String s1 : badUrls) {
            System.out.println(s1);
        }
        System.out.println("args   = " + args);
        for (String s1 : args) {
            System.out.println(s1);
        }

        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");

        System.out.println(d);
        System.out.println(new Date(System.currentTimeMillis()));
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

