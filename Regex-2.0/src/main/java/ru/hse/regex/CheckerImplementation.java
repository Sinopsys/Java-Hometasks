package ru.hse.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kirill on 09.11.16.
 */

class CheckerImplementation implements Checker {
    @Override
    public Pattern getPLSQLNamesPattern() {
        return Pattern.compile("\\p{Alpha}([\\p{Alpha}\\d_$]){0,29}");
    }

    @Override
    public Pattern getHrefURLPattern() {
        return Pattern.compile("<\\s*(?i)(a)\\s+(?i)(href)\\s*=\\s*((\"([\\p{Alnum} :/_\\.-])" +
                "+(\\.[\\p{Alnum} ])+([/\\p{Alpha} ?.])*\")|(([\\p{Alnum}:/_\\.-])+(\\.[\\p{Alnum}])+([/\\p{Alpha}?.])*))\\s*/?>");
    }

    @Override
    public Pattern getEMailPattern() {
        return Pattern.compile("[\\p{Alnum}][\\p{Alnum}_.-]{0,20}[\\p{Alnum}]@" +
                "([\\w\\d][\\w\\d-]*[\\w\\d]\\.)+((ru)|(com)|(net)|(org))");
    }

    @Override
    public boolean checkAccordance(String inputString, Pattern pattern)
            throws IllegalArgumentException {
        if (inputString == null && pattern == null)
            return true;
        // check if ONLY arg is a null value
        if (inputString == null ^ pattern == null) {
            throw new IllegalArgumentException("One of the parameters is null");
        }
        Matcher matcher = pattern.matcher(inputString);
        return matcher.matches();
    }

    @Override
    public List<String> fetchAllTemplates(StringBuffer inputString, Pattern pattern)
            throws IllegalArgumentException {
        // check if at least one arg is a null value
        if (inputString == null || pattern == null)
            throw new IllegalArgumentException("At least one of the parameters is null");
        // create a list for storing the matches
        List<String> res = new ArrayList<>();
        Matcher m = pattern.matcher(inputString.toString());
        // write matches to list
        while (m.find()) {
            if (m.group().length() != 0) {
                res.add(m.group());
            }
        }
        return res;
    }
}
