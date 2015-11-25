import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Nick on 11/24/2015.
 */
public class BuildVocab {

    //Connect to Mongo instance and  get reference to db
    private static MongoClient mongoClient = new MongoClient();
    private static MongoDatabase db = mongoClient.getDatabase("swyfu-db");

    public static ArrayList<String> run() {
        return pathTree("bama");
    }

    private static ArrayList<String> pathTree(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Trying to find pathTree for null item");
        }
        ArrayList<ArrayList<String>> hydra = new ArrayList<>();
        while (s.length() > 1) {
            ArrayList<String> myPaths = new ArrayList<>();
            myPaths = getSwipings(s.charAt(0), s.charAt(1));
            hydra.add(myPaths);
            s = s.substring(1);
        }
        ArrayList<String> r = new ArrayList<String>();
        boolean first = true;
        for (ArrayList<String> a : hydra) {
            if (first) {
                r = a;
                first = false;
            } else {
                ArrayList<String> temp = (ArrayList<String>) r.clone();
                r.clear();
                for (String i : temp) {
                    for (String j : a) {
                        if (j.charAt(0) == i.charAt(i.length() - 1)) {
                            j = j.substring(1);
                        }
                        r.add(i + j);
                    }
                }
            }
        }
        return r;
    }

    private static ArrayList<String> getSwipings(char a, char b){
        if (!Character.isLetter(a) || !Character.isLetter(b)) {
            throw new IllegalArgumentException("You can only sweep between characters. " +
                    "You're trying to sweep between " + a + " and " + b);
        }
        a = Character.toUpperCase(a);
        b = Character.toUpperCase(b);
        FindIterable<Document> iterable = db.
                getCollection("keyPaths").find(new Document("keyPair", a + "" + b));
        Document document = iterable.first();
        if (document != null) {
            return (ArrayList<String>) document.get("set");
        }
        return new ArrayList<>();
    }
}
