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
        String name = tableRow.getCell(0).asText(); // service name
        String price = tableRow.getCell(3).asText(); // price of service
        boolean enabled = true;

        /*
        // better way to control enabled service, but I couldn't get current bubble color - colors are bubble bubble-top bubble-red & bubble bubble-top bubble-green
        // (run out of ideas)
        List<HtmlElement> elements = tableRow.getCell(2).getByXPath("/tr/td/div[contains(@class, 'bubble-red')]");
        if ( elements.size() > 0)
            enabled = false;
        */

        if (tableRow.getCell(4).asText().contains("Aktivovat")
                || tableRow.getCell(4).asText().contains("Odblokovat")) {
            enabled = false;
        }


        if (price.equals("Zdarma"))
            return new TMobileData(name, 0, enabled);
        return new TMobileData(name, parsePrice(price), enabled);

    }

    /**
     * Called from parseRow
     *
     * @return parsed price from @param string
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
