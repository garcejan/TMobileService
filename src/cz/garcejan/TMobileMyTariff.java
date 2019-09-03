package cz.garcejan;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import cz.garcejan.TMobileExceptions.InvalidTableException;
import javafx.util.Pair;

import java.util.List;

public class TMobileMyTariff extends TMobilePageAbstract {

    private Pair<String,String> myTariff;

    public Pair<String, String> getMyTariff() {
        return myTariff;
    }

    /**
     * Adds new data to myTariff
     * @param htmlTable = table from where we get data
     * @return true, if table size is correct, else false
     */
    private void setMyTariff(HtmlTable htmlTable) throws InvalidTableException {

        if ( ! htmlTable.getCaptionText().equals("Základní informace o tarifu")
                || htmlTable.getRow(0).getCells().size() != 3
                || htmlTable.getRowCount() != 1  )
            throw new InvalidTableException("Failed while parsing myTariff");

        HtmlTableRow tableRow =  htmlTable.getRow(0);

        int pos0 = tableRow.getCell(0).asText().indexOf("\n");
        String a  = tableRow.getCell(0).asText().substring(0, pos0);
        int pos1 = tableRow.getCell(1).asText().indexOf("\n");
        String b  = tableRow.getCell(1).asText().substring(0, pos1);
        myTariff = new Pair<>( a, b);

/*
        int controlCells = 0;
        String tmpCell = "";

        for ( HtmlTableCell tableCell : tableRow.getCells() ){
            if ( controlCells == 1) {
                myTariff = new Pair<>( tmpCell, tableCell.asText());
                break;
            }
            ++controlCells;
            tmpCell = tableCell.asText();
        }
*/

    }

    public void saveDataFromTables (List<HtmlTable> tables ) throws InvalidTableException {
        setMyTariff(tables.get(0));
        saveDataFromTable(tables.get(1));
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("TMobileMyTariff{" + "myTariff=" + myTariff);


        for ( String key : data.keySet()){
            for ( TMobileData x : data.get(key)){
                sb.append( x.toString());
            }
        }

        sb.append('}');
        return sb.toString();
    }
}
