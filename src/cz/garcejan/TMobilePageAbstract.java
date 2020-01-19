package cz.garcejan;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import cz.garcejan.TMobileExceptions.InvalidTableException;

import java.util.*;

public abstract class TMobilePageAbstract {

    protected Map<String, List<TMobileData>> data = new HashMap<>();

    /**
     * Abstract method. Is implemented in TMobileServiceSettings & TMobileMyTariff
     *
     * @param tables
     * @throws InvalidTableException
     */
    public abstract void saveDataFromTables(List<HtmlTable> tables) throws InvalidTableException;

    /**
     * Returns all data get from table and save them to Map<String, List<TMobileData>> data
     *
     * @param table
     * @throws InvalidTableException when table is not recognized
     */
    protected void saveDataFromTable(HtmlTable table) throws InvalidTableException {

        Map<String, List<TMobileData>> tmpMap = new HashMap<>();
        List<TMobileData> list = new ArrayList<>();

        String title = "";
        boolean isFirst = true;

        for (HtmlTableRow tableRow : table.getRows()) {
            switch (tableRow.getCells().size()) {
                case 1:
                    if (!isFirst) {
                        List<TMobileData> tmp = new ArrayList<>(list);
                        tmpMap.put(title, tmp);
                        list.clear();
                    }

                    title = tableRow.getCell(0).asText();
                    isFirst = false;
                    break;
                case 5:
                    list.add(parseRow(tableRow));
                    break;
                case 0:
                    break;
                default:
                    throw new InvalidTableException("Unknown table while saving data from table " + table.getCaptionText());

            }
        }

        tmpMap.put(title, list);
        data.putAll(tmpMap);
    }

    /**
     * Parse tableRow to TMobileData. Called from saveDataFromTable
     *
     * @param tableRow
     * @return data gained from tableRow
     */
    private TMobileData parseRow(HtmlTableRow tableRow) {
        //Name of service
        String name = tableRow.getCell(0).asText();

        //service is enabled
        boolean enabled = true;
/*
        //bubble bubble-top bubble-red a bubble bubble-top bubble-green //div[contains(@class, 'date')]
         List<HtmlElement> elements = tableRow.getCell(2).getByXPath("/tr/td/div[contains(@class, 'bubble-red')]");
        System.out.println(  tableRow.getCell(2).asText() + " cellxxx " + tableRow.getCell(2));
        if ( elements.size() > 0){
            System.out.println("element size" + elements.size());
            enabled = false;
        }*/
        if (tableRow.getCell(4).asText().contains("Aktivovat")
                || tableRow.getCell(4).asText().contains("Odblokovat")) {
            enabled = false;
        }

        //price of service
        String price = tableRow.getCell(3).asText();

        if (price.equals("Zdarma"))
            return new TMobileData(name, 0, enabled);
        return new TMobileData(name, parsePrice(price), enabled);

    }

    /**
     * Called from parseRow
     *
     * @return parsed double from @param string
     */
    private double parsePrice(String string) {
        Scanner scanner = new Scanner(string);
        if (scanner.hasNextDouble())
            return scanner.nextDouble();
        if (scanner.hasNextInt())
            return scanner.nextInt();
        return 0;
    }

    @Override
    public abstract String toString();

    public Map<String, List<TMobileData>> getData() {
        return data;
    }

}
