import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        String[] items = sc.nextLine().split("\\s+");
        for (String item : items) {
            list.add(Integer.parseInt(item));
        }
        int n = sc.nextInt();
        int distance = Integer.MAX_VALUE;
        List<Integer> result = new ArrayList<>();
        for (Integer number : list) {
            if (Math.abs(n - number) < distance) {
                distance = Math.abs(n - number);
                result.clear();
                result.add(number);
            } else if (Math.abs(n - number) == distance) {
                result.add(number);
            }
        }
        Collections.sort(result);
        for (Integer num : result) {
            System.out.printf("%d ", num);
        }
    }
}
