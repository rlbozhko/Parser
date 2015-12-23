package com.company;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jdk.nashorn.internal.ir.CatchNode;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by Tester on 23.12.2015.
 */
public class ParserHtmlUnit {
    public static void main(String[] args) throws IOException {
        try (final WebClient webClient = new WebClient()) {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.waitForBackgroundJavaScript(10000);
            final HtmlPage page = webClient.getPage("http://rozetka.com.ua/");

            //get list of all divs
          // final List<?> divs = page.getByXPath("//div");
            //System.out.println(divs);
            //get div which has a 'name' attribute of 'John'
            final HtmlDivision div = (HtmlDivision) page.getByXPath("//nav/ul/li[1]/div//ul/li[1]//div/ul[2]/li[4]").get(0);
            System.out.println(div);

        }

    }
}
