

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Robert on 18.02.2016.
 */
public class Generator {

    private HashMap<Character,Character> substitutions = new HashMap<>(11);
    private ArrayList<String> passwords = new ArrayList<>();
    private ArrayList<String> dictionary = new ArrayList<>();
    private FileIOExample io  = new FileIOExample();

    public Generator(ArrayList<String> passwords, ArrayList<String> dictionary){
        substitutions.put('a','@');
        substitutions.put('i','1');
        substitutions.put('e','3');
        substitutions.put('o','0');
        substitutions.put('a','@');
        substitutions.put('s','5');
        this.passwords = passwords;
        this.dictionary = dictionary;

        //if it finds a previous output with cracked passwords, delete it
        try{
            File notCrackedFile = new File("NotCracked.txt");
            File outputFile = new File("output.txt");
            if(notCrackedFile .delete())
                System.out.println("Deleted NotCracked.txt");
            if(outputFile .delete())
                System.out.println("Deleted output.txt");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    public String hash(String toHash){
        try {
            // MessageDigest object is used to create hash. getInstance requires
            // the hashing algorithm to be defined.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convert the string into a byte array (getBytes()) and pass into
            // the digest object, which returns the hash as a byte array.
            byte[] hash = digest.digest(toHash.getBytes("UTF-8"));
            // using a string buffer, convert the byte array to a string
            // containing the hexadecimal representation
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            // Print the output. Output string can be obtained by calling the
            // builders toString() method
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle case where unknown algorithm is entered into the message
            // digest
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //derivate word by replacing characters such as a with @ and o with 0 and so on
    //the start and end variables mark the position from where to start cracking passwords
    //from the psw list
    public ArrayList<String> derivate(int start, int end, ArrayList<String> psw){

        ArrayList<String> cracked = new ArrayList<>();
        ArrayList<String> notCrackedList = new ArrayList<>();
      
        for (int i = start; i < end; i++){
            String[] parts = psw.get(i).split(":");
            boolean found = false;
            for(String word: dictionary){

                int charToReplace  = 0;
                //array list with character positions that have to be replaced
                ArrayList<Integer> positions = new ArrayList<>();

                for(int j = 0; j < word.length(); j++){
                    if(substitutions.containsKey(word.charAt(j))){
                        charToReplace += 1;
                        positions.add(j);
                    }
                }
                if(charToReplace == 0)
                    continue;
                int possibilities = (int) Math.pow(2,charToReplace);
                String pBinaryArray[] = new String[possibilities];

                //create array with all possibilities for characters
                //these are represented as an array of binary values
                for (int j = 0; j < possibilities; j++){
                    pBinaryArray[j] = String.format("%" + charToReplace + "s", Integer.toBinaryString(j)).replace(" ", "0");
                    //System.out.println(pBinaryArray[j]);
                }
                char[] wordCharArray = word.toCharArray();
                String[] allDerivations = new String[possibilities];

                //loop through all characters and replace character if it's in the substitutions list
                // and it's equivalent position from the binary list array is equal to 1
                for (int j = 0; j < possibilities; j++){
                    for (int k =0; k < charToReplace; k++){
                        int charPos = positions.get(k);
                        if(substitutions.containsKey(wordCharArray[charPos]) && pBinaryArray[j].charAt(k) == '1'){
                            wordCharArray[charPos] = substitutions.get(wordCharArray[charPos]);
                        }
                    }

                    allDerivations[j] = new String(wordCharArray);
                    wordCharArray = word.toCharArray();
                }
                //hash all derivations of the word
                for (int j = 0; j < allDerivations.length; j++){
                    if(hash(allDerivations[j]).equals(parts[1])){
                      
                        System.out.println(parts[0] + ":" + allDerivations[j]);
                        cracked.add(parts[0] + ":" + allDerivations[j]);
                        found = true;
                        break;
                    }
                }
            }
            if(!found){
                notCrackedList.add(psw.get(i));
            }
        }
        io.writeToFile("output.txt", cracked);
        return notCrackedList;
    }
    //simple dictionary attack
    //takes word from dictionary, then hashes it and compares it to the passwords hash
    public void dictionaryAttack(int start, int end){
        ArrayList<String> cracked = new ArrayList<>();
        ArrayList<String> notCrackedList = new ArrayList<>();
        for (int i = start; i < end; i++){
            String[] parts = passwords.get(i).split(":");
            boolean found = false;
            for (String word: dictionary) {
                if (hash(word).equals(parts[1])) {
                    System.out.println(parts[0] + ":" + word);
                    cracked.add(parts[0] + ":" + word);
                    found = true;
                    break;
                }
            }
            if(!found){
                notCrackedList.add(passwords.get(i));
            }
        }
        //write cracked passwords to cracked and the unsuccesful ones to NotCracked
        io.writeToFile("output.txt", cracked);
        io.writeToFile("NotCracked.txt", notCrackedList);
    }
    //this function adds numbers to the beginning and end of the word
    //then it hashes the result, comparing it to the passwords hash
    public ArrayList<String> alterWords(int start, int end, ArrayList<String> psw){
        ArrayList<String> cracked = new ArrayList<>();
        ArrayList<String> notCrackedList = new ArrayList<>();
      
        for (int i = start; i < end; i++){
            String[] parts = psw.get(i).split(":");
            boolean found = false;
            for (String word: dictionary){
                for (int j = 0; j < 100; j++){
                    //add number to end of word
                    if(hash(word + j).equals(parts[1])){
                        System.out.println(parts[0] + ":" + word + j);
                        cracked.add(parts[0] + ":" + word + j);
                        found = true;
                        break;
                    }
                    //add number to end of uppercased word
                    else if(hash(word.toUpperCase() + j).equals(parts[1])){
                      
                        System.out.println(parts[0] + ":" + word.toUpperCase() + j);
                        cracked.add(parts[0] + ":" + word.toUpperCase() + j);
                        found = true;
                        break;
                    }
                    //add number to end of word where first letter is uppercase
                    else if(word.length() != 0 && hash(String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1) + j).equals(parts[1])){
                        System.out.println(parts[0] + ":" + String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1) + j);
                        cracked.add(parts[0] + ":" + String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1) + j);
                        found = true;
                        break;
                    }
                    //add number to beginning of word
                    else if(hash(j + word).equals(parts[1])){
                        System.out.println(parts[0] + ":" + j + word);
                        cracked.add(parts[0] + ":" + j + word);
                        found = true;
                        break;
                    }
                    //add number to beginning of uppercased word
                    else if(hash(j + word.toUpperCase()).equals(parts[1])){
                        System.out.println(parts[0] + ":" + j + word.toUpperCase());
                        cracked.add(parts[0] + ":" + j + word.toUpperCase());
                        found = true;
                        break;
                    }
                    //add number to beginning of word where first letter is uppercase
                    else if(word.length() != 0 && hash(j + String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1)).equals(parts[1])){
                        System.out.println(parts[0] + ":" + j + String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1));
                        cracked.add(parts[0] + ":" + j + String.valueOf(Character.toUpperCase(word.charAt(0)))+ word.substring(1));
                        found = true;
                        break;
                    }
                }
            }
            if(!found){
                notCrackedList.add(psw.get(i));
            }
        }
        io.writeToFile("output.txt", cracked);
        return notCrackedList;
    }
    //this method concatenates strings from the dictionary
    //the start and end variables mark the position from where to start cracking passwords
    //from the psw list
    public ArrayList<String> concatenate(int start, int end, ArrayList<String> psw){
        long startTime = System.currentTimeMillis() % 1000;
        for (int a = start; a<end; a++){
            System.out.println(psw.get(a));
        }
        ArrayList<String> cracked = new ArrayList<>();
        ArrayList<String> notCrackedList = new ArrayList<>();
      
        for (int i = start; i < end; i++){
            String[] parts = psw.get(i).split(":");
            Collections.shuffle(dictionary);
            boolean found = false;
            for (String first: dictionary){
                for (String second: dictionary){
                    // simple concatenation of 2 words 
                    if(hash(first + second).equals(parts[1])){
                        System.out.println(parts[0] + ":" + first + second);
                        cracked.add(parts[0] + ":" + first + second);
                        found = true;
                        break;
                    }
                    // concatenate 2 uppercase words
                    else if(hash(first.toUpperCase() + second.toUpperCase()).equals(parts[1]) ){
                        System.out.println(parts[0] + ":" + first.toUpperCase() + second.toUpperCase());
                        cracked.add(parts[0] + ":" + first.toUpperCase() + second.toUpperCase());
                        found = true;
                        break;
                    }
                    // concatenate 2 words where first letter is upper for both
                    else if((first.length() != 0 && second.length() != 0) && hash(String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                                        String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1)).equals(parts[1])){
                      
                        System.out.println(parts[0] + ":" + String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                                               String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1));

                        cracked.add(parts[0] + ":" + String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1));
                        found = true;
                        break;
                    }
                    // this loop will concatenate numbers from 0 - 100 to the words generated using the same methods
                    // as above. It will add at the beginning and at the end
                    for(int j = 0; j < 101; j++){
                        
                        if(hash(first + second + j).equals(parts[1])){
                            System.out.println(parts[0] + ":" + first + second + j);
                            cracked.add(parts[0] + ":" + first + second + j);
                            found = true;
                            break;
                        }
                        else if(hash(first.toUpperCase() + second.toUpperCase() + j).equals(parts[1]) ){
                            System.out.println(parts[0] + ":" + first.toUpperCase() + second.toUpperCase() + j);
                            cracked.add(parts[0] + ":" + first.toUpperCase() + second.toUpperCase() + j);
                            found = true;
                            break;
                        }
                        else if((first.length() != 0 && second.length() != 0) && hash(String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1) + j).equals(parts[1])){
                          
                            System.out.println(parts[0] + ":" + String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                    String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1) + j);

                            cracked.add(parts[0] + ":" + String.valueOf(Character.toUpperCase(first.charAt(0)))+ first.substring(1) +
                                    String.valueOf(Character.toUpperCase(second.charAt(0)))+ second.substring(1) + j);
                            found = true;
                            break;
                        }
                    }
                }
            }
            if(!found){
                notCrackedList.add(psw.get(i));
            }
        }
        io.writeToFile("output.txt", cracked);
        return notCrackedList;
    }
    //this function will generated numbers from 0 to 1000000
    public ArrayList<String> generateNumbers(int start, int end, ArrayList<String> psw){
        long startTime = System.currentTimeMillis() % 1000;
        //this.dictionary.txt = io.readFromFile("dictionary.txt.txt");
        ArrayList<String> cracked = new ArrayList<>();
        ArrayList<String> notCrackedList = new ArrayList<>();
      
        for (int i = start; i < end; i++){
            String[] parts = psw.get(i).split(":");
            boolean found = false;
            for (int j = 0; j < 1000000; j++){
                if(hash(String.valueOf(j)).equals(parts[0])){
                  
                    System.out.println(parts[0] + ":" + j);
                    cracked.add(parts[0] + ":" + j);
                    found = true;
                    break;
                }
            }
            if(!found){
                notCrackedList.add(psw.get(i));
            }
        }
        io.writeToFile("output.txt",cracked);
        return notCrackedList;
    }
    public void crack(){


        class Cracker implements Runnable{
            private int start;
            private int end;
            private int strategy;
            private ArrayList<String> psw;
            public Cracker(int start, int end, int strategy, ArrayList<String> psw){
                this.start = start;
                this.end = end;
                this.strategy = strategy;
                this.psw = psw;
            }
            @Override
            // this method will run on the threads, each thread running a different strategy
            public void run(){
                if(strategy == 0){
                    dictionaryAttack(start, end);
                }
                else if(strategy == 1){
                    ArrayList<String> remains =  alterWords(start, end, psw);
                    remains = derivate(0, remains.size(), remains);
                    remains = concatenate(0, remains.size(), remains);
                    remains = generateNumbers(0, remains.size(), remains);
                }
                else if(strategy == 2){

                    ArrayList<String> remains =  derivate(start, end, psw);
                    remains = concatenate(0, remains.size(), remains);
                    remains = generateNumbers(0, remains.size(), remains);
                    remains = alterWords(0, remains.size(), remains);
                }
                else if(strategy == 3) {
                    ArrayList<String> remains =  concatenate(start, end, psw);
                    remains = generateNumbers(0, remains.size(), remains);
                    remains = alterWords(0, remains.size(), remains);
                    remains = derivate(0, remains.size(), remains);
                }
                else if(strategy == 4){
                    ArrayList<String> remains =  derivate(start, end, psw);
                    remains = generateNumbers(0, remains.size(), remains);
                    remains = alterWords(0, remains.size(), remains);
                    remains = concatenate(0, remains.size(), remains);
                }
            }
        }
        //first phase is to split the list of passwords into the number of threads and put each set on a different thread
        //to crack using strategy 0, the dictionary attack
        //second phase reads the remaining passwords and splits them into the number of cores. Each thread will run a different strategy
        //to crack the passwords
        for(int phase = 0; phase < 2; phase++){

            int cores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(cores);

            if(phase == 1){
                this.passwords = io.readFromFile("NotCracked.txt");
                this.dictionary = io.readFromFile("phase1_dict.txt");
                try{
                    File f = new File("NotCracked.txt");
                    f.delete();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            int step = passwords.size() / cores;
            Runnable worker = null;
            for (int i = 0; i < cores; i++) {
                if(i == 0){
                    //at phase 0 each thread executes strategy 1
                    if(phase == 0) {
                        worker = new Cracker(0,step,0,passwords);
                    }
                    //at phase 1 apply different cracking strategies
                    else if(phase == 1){
                        worker = new Cracker(0, step, 1, passwords);
                    }
                }
                else if(i == cores-1){
                    //at phase 0 each thread executes strategy 1
                    if(phase == 0){
                        worker = new Cracker((step*i), passwords.size(),0, passwords);
                    }
                    //at phase 1 apply different cracking strategies
                    else if(phase == 1){
                        if(i % 4 == 0){
                            worker = new Cracker((step*i), passwords.size(),1, passwords);
                        }
                        else if(i % 3 == 0){
                            worker = new Cracker((step*i), passwords.size(),2, passwords);
                        }
                        else if(i % 2 == 0){
                            worker = new Cracker((step*i), passwords.size(),3, passwords);
                        }
                        else {
                            worker = new Cracker((step*i), passwords.size(),4, passwords);
                        }
                    }
                }
                else {
                    //at phase 0 each thread executes strategy 1
                    if(phase == 0){
                        worker = new Cracker((step*i), (step * (i + 1)),0, passwords);
                    }
                    //at phase 1 apply different cracking strategies
                    else if(phase == 1){
                        if(i % 4 == 0){
                            worker = new Cracker((step*i)+1, (step * (i + 1)),1, passwords);
                        }
                        else if(i % 3 == 0){
                            worker = new Cracker((step*i)+1, (step * (i + 1)),2, passwords);
                        }
                        else if(i % 2 == 0){
                            worker = new Cracker((step*i)+1, (step * (i + 1)),3, passwords);
                        }
                        else {
                            worker = new Cracker((step*i)+1, (step * (i + 1)),4, passwords);
                        }
                    }
                }
                executor.execute(worker);
            }
            executor.shutdown();
            //wait for all jobs to complete
            while (!executor.isTerminated()) {
            }
        }
        System.out.println("Operations completed!");
    }
}
