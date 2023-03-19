import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        System.out.println(result());
    }

    public static String result() {

        Scanner sc = new Scanner(System.in);
        String[] avail = sc.nextLine().split(" ");
        String[] note = sc.nextLine().split(" ");
        Map<String, Integer> map = new HashMap<>();
        for (String word : avail) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        Map<String, Integer> line = new HashMap<>();
        for (String word : note) {
            line.put(word, line.getOrDefault(word, 0) + 1);
        }
        for (String key : line.keySet()) {
            if (!map.containsKey(key) || map.get(key) < line.get(key)) {
                return "You are busted";
            }
        }
        return "You get money";
    }
}