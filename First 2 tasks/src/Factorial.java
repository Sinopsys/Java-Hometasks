import java.util.*;

public class Factorial {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number > 0");
        long k = -1;
        try{
        k = scanner.nextLong();
        }
        catch (Exception e){
            System.out.println("Enter a number");
        }
        if(k < 0){
            System.out.println("Enter a number > 0");
        }
        else{
            System.out.println("Factorial of " + k + " is " + fac(k));
        }
    }
    
    private static long fac(long n){
        if(n == 0)
            return 1;
        if(n > 0){
            long temp = 1;
            for (int i = 2; i <= n; ++i){
                System.out.println("f(" + (i-1) + ") = " + temp);
                temp *= i;
            }
            return temp;
        }
        return -1;
    }
}