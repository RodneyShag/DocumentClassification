package twoPoint2;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/* Saves a ton of necessary info, excellently organized, into this class
 * Tricky to code these data structures (like ArrayList<ArrayList<HashMap>>>) */
public class Data {
	int numCategories;
	
	int numEntries;
	int[] categoryEntries;
	
	HashMap<String, Integer> wordsAll;
	ArrayList<HashMap<String, Integer>> wordsCategory;
	
	int totalWords;
	int[] totalCategoryWords;
	
	int uniqueWords;
	int[] uniqueCategoryWords;
	
	ArrayList<HashMap<String, Integer>> entries;
	ArrayList<ArrayList<HashMap<String, Integer>>> entriesCategory;
	ArrayList<Integer> categories;
	
	/* Constructor: Efficient but ugly code, since updating a bunch of variables at once */
	public Data(String directory, String filename, int numCategories) throws FileNotFoundException, IOException{
		/* Initialize Variables */
		this.numCategories = numCategories;
		
		numEntries = 0;
		categoryEntries = new int[numCategories];
		
		wordsAll = new HashMap<>();
		wordsCategory = new ArrayList<HashMap<String, Integer>>(numCategories);
		for (int i = 0; i < numCategories; i++){
			HashMap<String, Integer> map = new HashMap<>();
			wordsCategory.add(map);
		}
		
		totalWords = 0;
		totalCategoryWords = new int[numCategories];
		
		uniqueWords = 0;
		uniqueCategoryWords = new int[numCategories];
		
		entries = new ArrayList<HashMap<String, Integer>>();
		entriesCategory = new ArrayList<ArrayList<HashMap<String, Integer>>>(numCategories);
		for (int i = 0; i < numCategories; i++){
			ArrayList<HashMap<String, Integer>> currentList = new ArrayList<HashMap<String, Integer>>();
			entriesCategory.add(currentList);
		}
		categories = new ArrayList<Integer>(numEntries);
		
		countEntries(directory, filename);
		mapWords(directory, filename);
		saveEntries(directory, filename);
        calculateNumWords();
        //printSomeData();
	}
	
	public void countEntries(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	int category = Character.getNumericValue(line.charAt(0));
        	categoryEntries[category]++;
        	numEntries++;
        }
        br.close();
	}

	public void mapWords(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	int category = Character.getNumericValue(line.charAt(0));
        	HashMap<String, Integer> map = wordsCategory.get(category);
        	String words[] = line.split(" ");
        	for (int i = 1; i < words.length; i++){	//important: start at i=1 since i=0 is just a category indicator (see .txt files)
        		String word = words[i].substring(0, words[i].length() - 2);
        		int occurrences = Integer.parseInt(words[i].substring(words[i].length() - 1));
    			if (!map.containsKey(word))
    				map.put(word, occurrences);
    			else
    				map.put(word, map.get(word) + occurrences); //updates its value by adding to it.
        		if (!wordsAll.containsKey(word))
        			wordsAll.put(word, occurrences);
    			else
    				wordsAll.put(word, wordsAll.get(word) + occurrences); //updates its value by adding to it.
        	}
        }
        br.close();
	}
	
	public void calculateNumWords(){
		for (int i = 0; i < wordsCategory.size(); i++){
			HashMap<String, Integer> map = wordsCategory.get(i);
			for (String word : map.keySet()){
				totalCategoryWords[i] += map.get(word);
				uniqueCategoryWords[i]++;
			}
		}
		for (String word : wordsAll.keySet()){
			totalWords += wordsAll.get(word);
		}
		uniqueWords = wordsAll.size();
	}
	
	public void saveEntries(String directory, String filename) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(directory + "/" + filename + ".txt"));	//can throw FileNotFoundException
		String line;
        while ((line = br.readLine()) != null) { //can throw IOException
        	int category = Character.getNumericValue(line.charAt(0));
        	categories.add(category);
        	
        	HashMap<String, Integer> map = new HashMap<>();
        	String words[] = line.split(" ");
        	for (int i = 1; i < words.length; i++){	//important: start at i=1 since i=0 is just a category indicator (see .txt files)
        		String word = words[i].substring(0, words[i].length() - 2);
        		int occurrences = Integer.parseInt(words[i].substring(words[i].length() - 1));
        		map.put(word, occurrences);
        	}
        	ArrayList<HashMap<String, Integer>> currentCategory = entriesCategory.get(category);
        	currentCategory.add(map);
        	entries.add(new HashMap<String,Integer>(map));	//shallow copy of map
        }        
        br.close();
	}
	
	/* For debugging */
	public void printSomeData(){
		System.out.println();
		System.out.println("totalWords = " + totalWords);
		for (int i = 0; i < numCategories; i++){
			System.out.println("\tcategory " + i + ": " + totalCategoryWords[i]);
		}
		System.out.println("uniqueWords = " + uniqueWords);
		for (int i = 0; i < numCategories; i++){
			System.out.println("\tcategory " + i + ": " + uniqueCategoryWords[i]);
		}
	}
}


