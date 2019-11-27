import java.util.Scanner;

public class task1{
    private static int count(double a, double b, double c){
        if (a==0 && b==0){
            if (c==0){
                System.out.println("Empty input");
                return 0;
            }
            System.out.println("Invailid input");
            return 0;
        }
        if (a==0){
            System.out.println("x = " + -c/b);
            return 0;
        }

        double d = b*b - 4*a*c;
        if (d<0){
            System.out.println("Invailid input");
            return 0;
        } else if (d==0){
            System.out.println("x = "+ -b/(2*a));
            return 0;
        } else{
            double x1 = (-b + Math.sqrt(d))/(2*a);
            double x2 = (-b - Math.sqrt(d))/(2*a);
            System.out.println("x1 = "+ x1 + "; x2 = " + x2);
            return 0;
        }
    }


    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        double a = keyboard.nextDouble();
        double b = keyboard.nextDouble();
        double c = keyboard.nextDouble();
        count(a, b, c);
    }   
}
