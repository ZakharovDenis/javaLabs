import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class lab3 {
    private static Map<Character,Integer> symbolMap = new HashMap<Character, Integer>();
    private static  Map<Character, ArrayList<Integer>> rating = new HashMap<Character, ArrayList<Integer>>();
    public static Map<Character, Integer> sortByValue(final Map<Character, Integer> wordCounts) {
        return wordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<Character, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static void main (String[] args){
        File book = new File("/home/denis/Documents/учеба/java/lab1.1/src/main/resources/avidreaders.ru__voyna-i-mir-tom-1.txt");
        BufferedReader reader = null;
        Map<Character, Integer> sorted_map = new HashMap<Character, Integer>();
        try {
            reader = new BufferedReader(new FileReader(book));
            String text = null;

            while ((text = reader.readLine()) != null) {
                char[] symbols = text.toLowerCase().toCharArray();
                for (char symbol:symbols){
                    if (symbolMap.containsKey(symbol)){
                        symbolMap.put(symbol, symbolMap.get(symbol)+1);
                        sorted_map = sortByValue(symbolMap);
                        ArrayList<Character> keys = new ArrayList<Character>(sorted_map.keySet());
                        for (int i = 0; i<keys.size();i++){
                            if (rating.containsKey(keys.get(i))){
                                ArrayList<Integer> hoy = rating.get(keys.get(i));
                                if (!hoy.get(hoy.size()-1).equals(i)) {
                                    hoy.add(i);
                                }
                                rating.put(keys.get(i), hoy);
                            } else {
                                ArrayList<Integer> hoy = new ArrayList<Integer>();
                                hoy.add(i);
                                rating.put(keys.get(i), hoy);
                            }
                        }
                    } else {
                        symbolMap.put(symbol, 1);
                        sorted_map = sortByValue(symbolMap);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        ArrayList<Character> finalKeys = new ArrayList<Character>(sorted_map.keySet());
        for (int i = 0; i < finalKeys.size(); i++){
            System.out.println("Position: " + i + " Symbol: " + finalKeys.get(i) +" Count: "+sorted_map.get(finalKeys.get(i)) + " Rating history: " + rating.get(finalKeys.get(i)));
        }
    }
}