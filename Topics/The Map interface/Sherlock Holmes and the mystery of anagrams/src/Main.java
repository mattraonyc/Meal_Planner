import java.util.*;

class Main {
    public static void main(String[] args) {
        System.out.print(result());
    }

    public static String result() {
        
        Scanner sc = new Scanner(System.in);
        String first = sc.next().toLowerCase();
        String second = sc.next().toLowerCase();
        if (first.length() != second.length()) {
            return "no";
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < first.length(); i++) {
            map.put(first.charAt(i) - 'a', map.getOrDefault(first.charAt(i) - 'a', 0) + 1);
        }
        Map<Integer, Integer> compare = new HashMap<>();
        for (int x = 0; x < second.length(); x++) {
            compare.put(second.charAt(x) - 'a', compare.getOrDefault(second.charAt(x) - 'a', 0) + 1);
        } 
        if (Objects.equals(map, compare)) {
            return "yes";
        }
        return "no";
    }
}
