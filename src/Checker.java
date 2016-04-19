import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Checker {
	HashMap<String, String> solutions;
	HashMap<String, String> submission;

	public Checker() {
		solutions = new HashMap<String, String>();
		submission = new HashMap<String, String>();
	}

	public void readInFiles() {
		try {
			BufferedReader solutionReader = new BufferedReader(new FileReader("raw.txt"));
			String line;
			while ((line = solutionReader.readLine()) != null) {
				System.out.println(line);
				String[] split = line.trim().split(":");
				solutions.put(split[0], split[1]);
			}
			solutionReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedReader submissionReader = new BufferedReader(new FileReader("output.txt"));
			String line;
			while ((line = submissionReader.readLine()) != null) {
				System.out.println(line);
				String[] split = line.trim().split(":");
				submission.put(split[0], split[1]);
			}
			submissionReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Read in both files
 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkAnswers() {
		int correctCount = 0;
		int incorrectCount = 0;
		int missingCount=0;
		for (String user : solutions.keySet()) {
			if(submission.containsKey(user)){
				if(submission.get(user).equals(solutions.get(user))){
					correctCount++;
				}else{
					incorrectCount++;
					System.out.println(user);
				}
			}else{
				missingCount++;
			}
		}
		
		System.out.println("Correct: " + correctCount +  "/" + solutions.keySet().size());
	}

	public static void main(String[] args) {
		Checker checker = new Checker();
		checker.readInFiles();
		checker.checkAnswers();
	}
}
