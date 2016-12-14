package ru.hse.regex;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

/**
 * Created by kirill on 09.11.16.
 */

public class Main {
    static boolean checkResults(List<String> res, String[] good) {
        return Arrays.equals(res.toArray(), good);
    }

    public static void main(String[] args) {
        // a string of PLSQLNames to check
        String names = "var1  _var2  ?!var  $var  _var  1var  va$_r  va%r " +
                " переменная  vaaaaaaaaaaaaaaaaaaaaaaaaaaaar vaaaaaaaaaaaaaaaaaaaaaaaaaaaaar";
        // filtered manually
        String[] goodNames = new String[]
                {
                        "var1", "var2", "var", "var", "var", "var",
                        "va$_r", "va", "r", "vaaaaaaaaaaaaaaaaaaaaaaaaaaaar",
                        "vaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "r"
                };

        // a string of emails to check
        String emails = "user@gmail.com  us_.-er@gmail.1lol1.com" +
                " _.-user_.-@mail.org  user@a.c.com us3r@gma-2il.com";
        // filtered manually
        String[] goodEmails = new String[]
                {
                        "user@gmail.com", "us_.-er@gmail.1lol1.com", "us3r@gma-2il.com"
                };

        // a string of hrefs to check
        String hrefs = "<a href = \"url.ru\">  <\ta href = \n http://www.url.ru>" +
                "  <a href = \"url.ru/ind ex.php?\"  <a HrEf = url12.c0m>";
        // filtered manually
        String[] goodHrefs = new String[]
                {
                        "<a href = \"url.ru\">", "<\ta href = \n http://www.url.ru>"
                };

        CheckerImplementation checkerImplementation = new CheckerImplementation();
        Pattern eMailPattern = checkerImplementation.getEMailPattern();
        Pattern plsqlNamesPattern = checkerImplementation.getPLSQLNamesPattern();
        Pattern hrefURLPattern = checkerImplementation.getHrefURLPattern();

        List<String> namesList = checkerImplementation.fetchAllTemplates(new StringBuffer(names), plsqlNamesPattern);
        System.out.println(checkResults(namesList, goodNames)? "Names match ok" : "Names do not match");

        List<String> emailsList = checkerImplementation.fetchAllTemplates(new StringBuffer(emails), eMailPattern);
        System.out.println(checkResults(emailsList, goodEmails)? "Emails match ok" : "Emails do not match");

        List<String> hrefList = checkerImplementation.fetchAllTemplates(new StringBuffer(hrefs), hrefURLPattern);
        System.out.println(checkResults(hrefList, goodHrefs)? "Hrefs match ok" : "Hrefs do not match");
    }
}
