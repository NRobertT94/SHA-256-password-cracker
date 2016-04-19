

import java.util.ArrayList;

/**
 * Created by Robert on 18.02.2016.
 */
public class main {

    public static void main(String args[]){
        FileIOExample io = new FileIOExample();

        //Put path of passwords file below
        ArrayList<String> hashes = io.readFromFile("password.txt");

        ArrayList<String> dictionary = io.readFromFile("phase0_dict.txt");

        Generator g = new Generator(hashes,dictionary);
        g.crack();
    }
}

