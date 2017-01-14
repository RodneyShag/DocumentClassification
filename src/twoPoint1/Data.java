package twoPoint1;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/* Saves a ton of necessary info, excellently organized, into this class */
public class Data {
	int numEntries        = 0;
	int numSpamEntries    = 0;
	int numNotSpamEntries = 0;
	
	HashMap<String, Integer> wordsNoCategory = new HashMap<>();
	HashMap<String, Integer> spamWords       = new HashMap<>();
	HashMap<String, Integer> notSpamWords    = new HashMap<>();
	
	int totalWords        = 0;
	int totalSpamWords    = 0;
	int totalNotSpamWords = 0;
	
	int uniqueWords        = 0;
	int uniqueSpamWords    = 0;
	int uniqueNotSpamWords = 0;
	
	ArrayList<HashMap<String, Integer>> entries        = new ArrayList<HashMap<String, Integer>>();
	ArrayList<HashMap<String, Integer>> spamEntries    = new ArrayList<HashMap<String, Integer>>();
	ArrayList<HashMap<String, Integer>> notSpamEntries = new ArrayList<HashMap<String, Integer>>();
	ArrayList<Boolean> isSpam                          = new ArrayList<Boolean>();
	
	/* Constructor: Efficient but ugly code, since updating a bunch of variables at once */
	public Data(String directory, String filename) throws FileNotFoundException, IOException{
		countSpamEntries(directory, filename);
		mapWords(directory, filename);
		saveEntries(directory, filename);
        calculateNumWords();
        //printSomeData();
	}
	
	public void countSpamEntries(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	boolean spam = (line.charAt(0) == '1');
        	if (spam)
        		numSpamEntries++;
        	else
        		numNotSpamEntries++;
        }
        numEntries = numSpamEntries + numNotSpamEntries;
        br.close();
	}

	public void mapWords(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	boolean spam = (line.charAt(0) == '1');
        	String words[] = line.split(" ");
        	for (int i = 1; i < words.length; i++){	//important: start at i=1 since i=0 is just an indicator of spam/notspam (see .txt files)
        		String word = words[i].substring(0, words[i].length() - 2);
        		int occurrences = Integer.parseInt(words[i].substring(words[i].length() - 1));
        		if (spam){
        			if (!spamWords.containsKey(word))
        				spamWords.put(word, occurrences);
        			else
        				spamWords.put(word, spamWords.get(word) + occurrences); //updates its value by adding to it.
        		}
        		else{
        			if (!notSpamWords.containsKey(word))
        				notSpamWords.put(word, occurrences);
        			else
        				notSpamWords.put(word, notSpamWords.get(word) + occurrences); //updates its value by adding to it.
        		}
        		if (!wordsNoCategory.containsKey(word))
        			wordsNoCategory.put(word, occurrences);
    			else
    				wordsNoCategory.put(word, wordsNoCategory.get(word) + occurrences); //updates its value by adding to it.
        	}
        }
        br.close();
	}
	
	public void calculateNumWords(){
		for (String word : spamWords.keySet()){
			totalSpamWords += spamWords.get(word);
			uniqueSpamWords++;
		}
		for (String word : notSpamWords.keySet()){
			totalNotSpamWords += notSpamWords.get(word);
			uniqueNotSpamWords++;
		}
		for (String word : wordsNoCategory.keySet()){
			totalWords += wordsNoCategory.get(word);
		}
		uniqueWords = wordsNoCategory.size();
	}
	
	public void saveEntries(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	boolean spam = (line.charAt(0) == '1');

        	HashMap<String, Integer> map = new HashMap<>();
        	String words[] = line.split(" ");
        	for (int i = 1; i < words.length; i++){	//important: start at i=1 since i=0 is just an indicator of spam/notspam (see .txt files)
        		String word = words[i].substring(0, words[i].length() - 2);
        		int occurrences = Integer.parseInt(words[i].substring(words[i].length() - 1));
        		map.put(word, occurrences);
        	}
        	if (spam){
        		spamEntries.add(map);
        		isSpam.add(true);
        	}
        	else{
        		notSpamEntries.add(map);
        		isSpam.add(false);
        	}
        	entries.add(new HashMap<String,Integer>(map));	//shallow copy of map
        }        
        br.close();
	}
	
	/* For debugging */
	public void printSomeData(){
		System.out.println();
		System.out.println("totalWords = " + totalWords);
		System.out.println("totalSpamWords = " + totalSpamWords);
		System.out.println("totalNotSpamWords = " + totalNotSpamWords);
		System.out.println("uniqueWords = " + uniqueWords);
		System.out.println("uniqueSpamWords = " + uniqueSpamWords);
		System.out.println("uniqueNotSpamWords = " + uniqueNotSpamWords);
	}
}


