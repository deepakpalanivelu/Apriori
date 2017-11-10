import java.io.*;
import java.util.*;

/**
 * Created by deepakrtp on 29/09/17.
 */

public class InputReader {

    private BufferedReader br;
    private Map<String,Integer> map;
    private ArrayList<ArrayList<Integer>> baskets;
    private List<Integer> itemQuantity;
    private int minSupport;
    private int distinctItem = 1;

    InputReader()  {

        try {
           br = new BufferedReader(new FileReader("/Users/deepakrtp/Documents/Apriori/retail.dat"));
        }
        catch(Exception e ) {

        }
        map = new HashMap<String, Integer>();
        baskets = new ArrayList<ArrayList<Integer>>();
        itemQuantity = new ArrayList<Integer>();
        parseInput();
    }

    public void parseInput() {
        try {
            /* Removing all comments and checking if the first line contains the minimum support value */
            String input = br.readLine().trim();
            while (input.startsWith("#")) {
                input = br.readLine();
            }
                minSupport = Integer.parseInt(input);

        } catch (Exception e) {
            System.out.println(" Minimum Support value is missing in the input file or is in wrong format ");
            System.exit(0);
        }

        String cart = "";
        itemQuantity.add(-1);
        try {
            /* Iterating each bucket from the bucket list and trimming all necessary white spaces  and mapping it to a integer */
            while ((cart = br.readLine()) != null) {
                if(!cart.trim().startsWith("#")) {
                    ArrayList<Integer> basket = new ArrayList<Integer>();
                    String items[] = cart.split(",");
                    for (String item : items) {
                        item = item.trim();

                        if (!map.containsKey(item)) {
                            map.put(item, distinctItem);
                            itemQuantity.add(1);
                            basket.add(distinctItem);
                            distinctItem++;
                        } else if (!item.equals("")) {
                            int mappedInteger = map.get(item);
                            if(!basket.contains(mappedInteger)) {
                                basket.add(mappedInteger);
                            }
                            itemQuantity.set(mappedInteger, itemQuantity.get(mappedInteger) + 1);
                        }

                    }
                    baskets.add(basket);
                }
            }
        } catch(IOException e) {
            System.out.println("Error while parsing the buckets");
            System.exit(0);
        }
    }

    public int getMinSupport() {
        return minSupport;
    }

    public ArrayList<ArrayList<Integer>> getBaskets() {
        return baskets;
    }

    public List<Integer> getItemQuantity() {
        return itemQuantity;
    }

    public Map<Integer,String> getMap() {
        Map <Integer,String> reverseMap = new HashMap<>();
        for (Map.Entry<String,Integer> entry : map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        /* clearing the map so the java garbage collection will collect it */
        map = null;
        return reverseMap;
    }

    public int getDistinctItem() {
        return distinctItem - 1;
    }
}
