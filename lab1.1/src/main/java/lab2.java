import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class lab2{
    private static ArrayList<Integer> stack = new ArrayList<Integer>();

    private static ArrayList<Integer> sort (ArrayList<Integer> input){
        Collections.sort(input);
        return input;
    }

    private static void fifo (int number){
        if (stack.size() == 8){
            stack.remove(stack.size()-1);
        }
        stack.add(0, number);
        System.out.print(stack + "->");
        ArrayList<Integer> tmp = new ArrayList<Integer>(stack);
        Collections.sort(stack);
        if (stack.size()%2 ==0){
            int middle = stack.size()/2;
            System.out.println(stack + "->" +(double) (stack.get(middle)+stack.get(middle-1))/2);
        } else {
            System.out.println(stack + "->" + stack.get(stack.size()/2));
        }
        stack = tmp;
    }

    public static void main(String[] args) {
        for (int i = 0; i< 100; i++){
            fifo(new Random().nextInt(10));
        }
    }
}
