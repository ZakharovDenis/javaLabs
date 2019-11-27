import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class task2{
    private static ArrayList<Integer> stack = new ArrayList<Integer>(); 
    private static void fifo (int number){
        if (stack.size() == 8){
            stack.remove(7);
        }
        stack.add(0, number);
        System.out.print(stack + "->");
        ArrayList<Integer> tmp = stack;
        Collections.sort(stack);
        System.out.println(stack + "->" + stack.get(stack.size()/2));
        stack = tmp;
    }
   
    public static void main(String[] args) {
        for (int i = 0; i< 100; i++){
            fifo(new Random().nextInt(10));
        }
    }   
}
