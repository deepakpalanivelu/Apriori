import java.util.*;

/**
 * Created by deepakrtp on 28/09/17.
 */
public class P1 {

    private InputReader reader;
    private Map<Integer,String> map;
    private ArrayList<ArrayList<Integer>> baskets;
    private List<Integer> itemQuantity;
    private int minSupport;
    private Set<String> itemSet;
    private Map<Integer, Integer> frequentItemSet;
    private ArrayList<ArrayList<Integer>> frequentItemSet1;


    private void findFrequentItemSet() {
        reader = new InputReader();
        minSupport = reader.getMinSupport();
        itemQuantity = reader.getItemQuantity();
        baskets = reader.getBaskets();
        map = reader.getMap();
        itemSet = new LinkedHashSet<>();
        frequentItemSet = new HashMap<>();
        frequentItemSet1 = new ArrayList<>();
        firstPass();
    }

    private void firstPass() {
        int itemNumber = 1;
        /* filtering out the induvidual items which has less frequent count */
        for(int item = 0 ; item < itemQuantity.size(); item++ ) {
            if(! (itemQuantity.get(item) < minSupport) ) {
                String key = map.get(item);
                if(!itemSet.contains(key)) {
                    System.out.println(key);
                    itemSet.add(key);
                }

                frequentItemSet.put(item, itemNumber);
                itemNumber++;
            }
        }
        itemSet.clear();
        if(frequentItemSet.size() > 1) {
            secondPass();
        }
    }

    private void secondPass() {
        /*Using triangular method array for generating pairs */
        int distinctItems = reader.getDistinctItem();
        /* Size of the triangular array will need 2 (m * m) bytes */
        int [] traingularArray = new int[ 2 * ((int) Math.pow( frequentItemSet.size(), 2)/ Integer.BYTES) + 10];
        for (List<Integer> basket : baskets) {
            for(int i = 0; i < basket.size() - 1; i++) {
                if(! frequentItemSet.containsKey(basket.get(i)) ){
                    continue;
                }
                for(int j = i + 1; j < basket.size(); j++) {
                    if(!frequentItemSet.containsKey(basket.get(j))) {
                        continue;
                    }
                    else {
                        ArrayList<Integer> item   = new ArrayList<>();
                        item.add(basket.get(i));
                        item.add(basket.get(j));
                        int k = 0;
                        int firstItem= frequentItemSet.get(item.get(0));
                        int secondItem = frequentItemSet.get(item.get(1));
                        if(firstItem < secondItem) {
                            k = (firstItem - 1 ) * ( frequentItemSet.size() - (firstItem/2) )   + secondItem - firstItem;
                        }
                        else if(secondItem > firstItem )
                        {
                             k = (secondItem - 1 ) * ( frequentItemSet.size() - (secondItem/2) )  + firstItem - secondItem;
                        }
                        traingularArray[ k ]++;
                        if(traingularArray[k] >= minSupport) {
                                Collections.sort(item);
                                String key = map.get(item.get(0)) + "," + map.get(item.get(1));
                                if(!itemSet.contains(key)) {
                                    System.out.println(key);
                                    itemSet.add(key);
                                }
                                if(!frequentItemSet1.contains(item)) {
                                    frequentItemSet1.add(item);
                                }
                        }
                    }
                }
            }
        }
        frequentItemSet = null;
        itemQuantity = null;
        itemSet.clear();

        if(frequentItemSet1.size() > 1) {
            findCandidateSet();
        }
    }

    public void findCandidateSet() {
        /* for construction greater than C2 */
        /* Creating sets greater than 3 which are candidates for frequent ItemSet  */
        while (frequentItemSet1.size() > 1) {
            ArrayList<ArrayList<Integer>> candidateSet = new ArrayList<ArrayList<Integer>>();
            for (int candidate = 0; candidate < frequentItemSet1.size() - 1; candidate++) {
                for (int nextcandidate = candidate + 1; nextcandidate < frequentItemSet1.size(); nextcandidate++) {
                    ArrayList<Integer> pattern1 = new ArrayList<Integer> (frequentItemSet1.get(candidate));
                    ArrayList<Integer> pattern2 = new ArrayList<Integer> (frequentItemSet1.get(nextcandidate));
                    for (int candidateItem : pattern2) {
                        if (!pattern1.contains(candidateItem)) {
                            pattern1.add(candidateItem);
                            Collections.sort(pattern1);
                            if (!containsList(candidateSet, pattern1)) {
                                candidateSet.add(pattern1);
                                pattern1 = new ArrayList<Integer>(frequentItemSet1.get(candidate));
                            } else {
                                pattern1.remove(pattern1.size() - 1);
                            }
                        }
                    }
                    for (int candidateItem : pattern1) {
                        if (!pattern2.contains(candidateItem)) {
                            pattern2.add(candidateItem);
                            Collections.sort(pattern2);
                            if (!containsList(candidateSet, pattern2)) {
                                candidateSet.add(pattern2);
                                pattern2 = new ArrayList<Integer> (frequentItemSet1.get(nextcandidate));
                            } else {
                                pattern2.remove(pattern2.size() - 1);
                            }
                        }
                    }
                }
            }
            filterCandidateSet(candidateSet);
        }
    }

    private boolean containsList(ArrayList<ArrayList<Integer>> itemSet, ArrayList<Integer> pattern) {
        for (ArrayList<Integer> item : itemSet) {
            if (item.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    private void findSubsets(List<Integer> arr, int n, int patternSize, int index,
                         int[] data, int k,ArrayList<ArrayList<Integer>> combinationList ) {

        if (index == patternSize)
        {   ArrayList<Integer> pattern = new ArrayList<>();
            Arrays.sort(data);
            for (int j=0; j<patternSize; j++) {
                pattern.add(data[j]);
            }
            combinationList.add(pattern);

            return;
        }


        if (k >= n)
            return ;


        data[index] = (arr.get(k));
        findSubsets(arr, n, patternSize, index+1, data, k+1,combinationList);
        findSubsets(arr, n, patternSize, index, data, k+1,combinationList );

    }

    private void filterCandidateSet(ArrayList<ArrayList<Integer>> candidateSet ) {
        /*filtering sets greater L2 */
        ArrayList<ArrayList<Integer>> tempFrequentItemSet1 = new ArrayList<ArrayList<Integer>>();
        /* A temporary array to store all combination one by one */
        for (ArrayList<Integer> candidate : candidateSet) {
            ArrayList<ArrayList<Integer>> combinationList = new ArrayList<ArrayList<Integer>>();
            findSubsets(candidate, candidate.size(), candidate.size() - 1, 0, new int[candidate.size() - 1], 0, combinationList);
            if ( allSubsetPresent(combinationList, frequentItemSet1)) {

                /* check the bucket list.
                 for each bucket check if the combination is present */
                int count = 0;
                for (ArrayList<Integer> basket : baskets) {
                    if(basket.containsAll(candidate)) {
                        count++;
                        if(count >= minSupport) {
                            String data = "";
                            ArrayList<Integer> tempFrequentItem = new ArrayList<>();
                            for(int item = 0; item < candidate.size();item++) {
                                tempFrequentItem.add(candidate.get(item));
                                data +=map.get(candidate.get(item));
                                if(item != (candidate.size() - 1)) {
                                    data += ",";
                                }
                            }
                            if(!tempFrequentItemSet1.contains(tempFrequentItem)) {
                                tempFrequentItemSet1.add(tempFrequentItem);
                                System.out.println(data);
                                itemSet.add(data);
                            }
                        }
                    }
                }
            }
        }
        frequentItemSet1 = new ArrayList<ArrayList<Integer>> (tempFrequentItemSet1);
        tempFrequentItemSet1.clear();

    }

    private boolean allSubsetPresent(ArrayList<ArrayList<Integer>>combinationList,ArrayList<ArrayList<Integer>> frequentItemSet1 ) {
        for(ArrayList<Integer> list :combinationList) {
            if(!frequentItemSet1.contains(list)) {
                return false;
            }
        }
        return true;
    }


    public static void main(String [] args) {
        P1 p = new P1();
        p.findFrequentItemSet();
    }
}
