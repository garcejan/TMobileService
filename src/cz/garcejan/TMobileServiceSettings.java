package cz.garcejan;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import cz.garcejan.TMobileExceptions.InvalidTableException;

import java.util.*;

public class TMobileServiceSettings extends TMobilePageAbstract {

    public void saveDataFromTables(List<HtmlTable> tables) {
        for (HtmlTable table : tables) {
            try{
                saveDataFromTable(table);
            }
            catch ( InvalidTableException ite ){
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TMobileServiceSettings{");


        for ( String key : data.keySet()){
            for ( TMobileData x : data.get(key)){
                sb.append( x.toString());
            }
        }

        sb.append('}');
        return sb.toString();
    }
}
