import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIOExample {


    /**
     * Method to read a password file and extract the hashes
     * @param filename - the file to read from
     * @return
     */
    public ArrayList<String> readFromFile(String filename) {
        ArrayList<String> words = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename), "UTF-8")) {
            while (sc.hasNextLine()) {
                 words.add(sc.nextLine());
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        }catch (Exception e){

        }
        return words;

    }


    /**
     * Method for writing teh contents of an ArrayLIst to a file
     * @param filename - The filename to write to
     * @param words  - An ArrayLIst of string to write to the file
     */
    public void writeToFile(String filename, ArrayList<String> words) {
        // Declare BufferedWriter object
        BufferedWriter writer = null;

                try {
                    FileWriter fw = new FileWriter(new File(filename).getAbsoluteFile(), true);
                    //Instantiate BufferedWriter object, using a FileWriter and the filename passed as parameter to the method
                    writer = new BufferedWriter(fw);

                    synchronized (this) {
                        for (int i = 0; i < words.size(); i++) {
                            //Write the string to the file
                            writer.append(words.get(i).trim());
                            //writer.newLine prints the appropriate newLine character to the file (usually \n)
                            writer.newLine();
                            //writer.flush ensures than anything in the buffer is written to the file. If you do nto call this you may find an empty file. To be safe, you can call this after every write.
                            writer.flush();
                        }
                    }
                    //loop through our Arr
                    // ayList

                } catch (IOException e) {
                    // Catch any IO errors
                    e.printStackTrace();
                }finally {
                    try {
                        //after execution, close the writer
                        if (writer != null)
                            writer.flush();
                           writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

    }

}
