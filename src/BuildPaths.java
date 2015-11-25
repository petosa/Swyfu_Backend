import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Nick on 11/24/2015.
 */
public class BuildPaths {

    private static MongoClient mongoClient = new MongoClient();
    private static MongoDatabase db = mongoClient.getDatabase("swyfu-db");

    public static void run() {

        //Decision tree
        boolean skip = false;
        Scanner in = new Scanner(System.in);
        System.out.println("Only show unmapped? (y/n)");
        String bool = in.nextLine().toLowerCase();
        if (bool.equals("y")) {
            skip = true;
        }
        System.out.println("Seek? (y/n)");
        String decision = in.nextLine().toLowerCase();
        int origI = 65;
        int origJ = 65;
        if (decision.equals("y")) {
            System.out.println("On press letter:");
            char ic = in.nextLine().charAt(0);
            ic = Character.toUpperCase(ic);
            System.out.println("On release letter:");
            char jc = in.nextLine().charAt(0);
            jc = Character.toUpperCase(jc);
            origI = ic;
            origJ = jc;
        }

        //Control structure
        for (int i = origI; i < 91; i++) {
            for (int j = origJ; j < 91; j++) {
                if (i < j) {
                    String key = (char) i + "" + (char) j;
                    String antikey = (char) j + "" + (char) i;
                    HashSet<String> set = new HashSet<>();

                    //See if set already exists for this key
                    FindIterable<Document> iterable = db.
                            getCollection("keyPaths").find(new Document("keyPair", key));
                    Document document = iterable.first();
                    if (document != null) {
                        set = new HashSet<String>(((ArrayList<String>) document.get("set")));
                    }
                    if(set.size() != 0 && skip) {
                        continue;
                    }
                    Toolkit.getDefaultToolkit().beep();
                    System.out.println((char) i + " to " + (char) j);
                    System.out.println(set);

                    int reps = 50;
                    for (int k = 0; k < reps; k++) {
                        String input = in.nextLine();
                        input = input.toLowerCase();
                        if (input.charAt(0) == '*') {
                            k += reps;
                        } else if (input.charAt(0) == Character.toLowerCase(i) &&
                                input.charAt(input.length() - 1) == Character.toLowerCase(j)) {
                            set.add(input);
                        } else {
                            k--;
                            System.err.println("Started or ended with wrong letter, try again.");
                        }
                    }
                    db.getCollection("keyPaths").deleteOne(new Document("keyPair", key));
                    db.getCollection("keyPaths")
                            .insertOne(new Document("keyPair", key).append("set", set));

                    db.getCollection("keyPaths").deleteOne(new Document("keyPair", antikey));
                    HashSet<String> antiset = new HashSet<>();
                    for (String s : set) {
                        antiset.add(new StringBuilder(s).reverse().toString());
                    }
                    db.getCollection("keyPaths")
                            .insertOne(new Document("keyPair", antikey).append("set", antiset));
                }
            }
        }
    }
}
