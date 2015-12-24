package com.company;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException {


        //пример ввода для теста
        String[] args1 = new String[3];
        args1[0] ="http://rozetka.com.ua/";
        args1[1] ="2000";
        args1[2] ="2500";


// после теста в этой строчке args1 поменять на args
        Arguments arguments = new Arguments(args1);

        if(!arguments.isValidArguments()){
            System.out.println("Неправильные аргументы. Необходимые аргументы: ");
            System.out.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.out.println(" цена от(целое число): 1000");
            System.out.println(" цена до(целое число): 1100");
            return;
        }

        String url = arguments.getArg(0);


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
