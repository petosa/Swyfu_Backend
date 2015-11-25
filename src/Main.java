import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Nick on 11/21/2015.
 */
public class Main {

    public static void main(String[] args) {
      //BuildPaths.run();

        check();
    }

    public static void check() {

        ArrayList<String> arr = BuildVocab.run();
        HashMap<String,Boolean> h = new HashMap<>();
        for (String s:arr) {
            h.put(s,true);
        }
        System.out.println("check");
        for (;;) {
            Scanner in = new Scanner(System.in);
            String s = in.nextLine();
            s = s.replaceAll("([a-zA-Z])(\\1{2,})", "$1").replaceAll("[^a-zA-Z]", "").toLowerCase();
            //s = new StringBuilder(s).reverse().toString();
            if (h.get(s) != null)
                System.out.println(true);
            else
                System.out.println(false);
        }
    }
}
