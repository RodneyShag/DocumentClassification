package twoPoint2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MAP {
	int numCategories;
	
	/* Priors, Likelihoods, Posteriors */
	double [] priors;
	ArrayList<HashMap<String, Double>> likelihoods;
	double [][] posteriors;
	
	/* Other Stuff */
	double[][] confusionMatrix;
	
	/* Constructor */
	public MAP(Data trainingData, Data testData){
		numCategories = trainingData.numCategories;
		
		/* Initialize Priors, Likelihoods, Posteriors */
		priors = new double[numCategories];
		likelihoods = new ArrayList<HashMap<String, Double>>(numCategories);
		for (int i = 0; i < numCategories; i++){
			HashMap<String, Double> map = new HashMap<>();
			likelihoods.add(map);
		}
		posteriors = new double[testData.numEntries][numCategories];
		
		/* Initialize Other Stuff */
		confusionMatrix = new double[numCategories][numCategories];
	}
	
	public void calculateStuff(Data trainingData, Data testData){
        calculatePriors(trainingData);
        calculateLikelihoods(trainingData, 1);
        calculatePosteriors(trainingData, testData);
        /* Uncomment the 2 lines below and replace the above 2 lines with them for Bernoulli method */
//        calculateLikelihoodsBernoulli(trainingData, 0.01); //do this version of Likelihoods for "Bernoulli" method
//        calculatePosteriorsBernoulli(trainingData, testData);

        printPriors();
        printLikelihoods();
        //printPosteriors0();
        
        evaluate(testData);
	}
	
	public void calculatePriors(Data trainingData){
		for (int i = 0; i < numCategories; i++){
			priors[i] = (double) trainingData.categoryEntries[i] / trainingData.numEntries;
		}
	}
	
	/* From Lecture 12, Slide 26 */
	/* Note: Our vocabulary for SPAM words and NOT SPAM words is a joint vocabulary.
	 *       Example: there are words in "trainingData.spamWords" with 0 likelihood (before Laplace smoothing) */
	public void calculateLikelihoods(Data trainingData, int k){
		double occurrences;
		double likelihood;
		int V = trainingData.uniqueWords;
		
		for (String word : trainingData.wordsAll.keySet()){
			for (int i = 0; i < numCategories; i++){
				if (trainingData.wordsCategory.get(i).containsKey(word))
					occurrences = trainingData.wordsCategory.get(i).get(word);
				else
					occurrences = 0;
				likelihood = (double) (occurrences + k) / (trainingData.totalCategoryWords[i] + k*V);
				HashMap<String, Double> map = likelihoods.get(i);
				map.put(word, likelihood);
			}
		}	
	}
	
	public void calculateLikelihoodsBernoulli(Data trainingData, double k){
		double occurrences;
		double likelihood;
		int V = 2;
		
		/* Each HashMap will keep track of # of documents with a specific word */
		ArrayList<HashMap<String, Integer>> maps = new ArrayList<HashMap<String, Integer>>(numCategories);
		for (int i = 0; i < numCategories; i++){
			HashMap<String, Integer> map = new HashMap<>();
			maps.add(map);
		}
		
		for (int i = 0; i < numCategories; i++){
			HashMap<String, Integer> map = maps.get(i);
			for (String word: trainingData.wordsAll.keySet()){
				map.put(word, 0);
			}
			for (HashMap<String, Integer> entry: trainingData.entriesCategory.get(i)){
				for (String word : entry.keySet()){
					map.put(word, map.get(word) + 1);
				}
			}
			
			for (String word : trainingData.wordsAll.keySet()){
				occurrences = map.get(word);
				likelihood = (double) (occurrences + k) / (trainingData.categoryEntries[i] + k*V);
				likelihoods.get(i).put(word, likelihood);
			}
		}
	}
	
	public void calculatePosteriors(Data trainingData, Data testData){
		double likelihood;
		for (int category = 0; category < numCategories; category++){
			for (int i = 0; i < testData.numEntries; i++){
				posteriors[i][category] = Math.log(priors[category]);
				
				HashMap<String, Integer> entry = testData.entries.get(i);
				for (String word : entry.keySet()){
					if (trainingData.wordsAll.containsKey(word)){ //this skips words not in our vocabulary.
						int occurrences = entry.get(word);
						
						likelihood = likelihoods.get(category).get(word);
						posteriors[i][category] += occurrences * Math.log(likelihood);
					}
				}
			}
		}
	}
	
	/* We loop through all vocab words here */
	public void calculatePosteriorsBernoulli(Data trainingData, Data testData){
		double likelihood;
		for (int category = 0; category < numCategories; category++){
			for (int i = 0; i < testData.numEntries; i++){
				posteriors[i][category] = Math.log(priors[category]);
				
				HashMap<String, Integer> entry = testData.entries.get(i);
				for (String word : trainingData.wordsAll.keySet()){
					if (entry.containsKey(word)) 
						likelihood = likelihoods.get(category).get(word);
					else
						likelihood =  1 - likelihoods.get(category).get(word);
					posteriors[i][category] += Math.log(likelihood);
				}
			}
		}
	}
	
	public void evaluate(Data testData){
		double overallPrediction = 0;
		double [] categoryPrediction = new double[numCategories];
		
		for (int entry = 0; entry < testData.numEntries; entry++){
			int actualCategory = testData.categories.get(entry);
			int likelyCategory = max(posteriors[entry]);
			confusionMatrix[actualCategory][likelyCategory]++;
			if (likelyCategory == actualCategory){
				overallPrediction++;
				categoryPrediction[likelyCategory]++;
			}
		}
		
		/* Get Percentages from raw counts */
		overallPrediction /= (double) testData.numEntries;
		for (int i = 0; i < numCategories; i++){
			categoryPrediction[i] /= testData.categoryEntries[i];
		}
		for (int i = 0; i < numCategories; i++){
			for (int j = 0; j < numCategories; j++){
				confusionMatrix[i][j] /= testData.categoryEntries[i];
			}
		}
		
		/* Print info */
		printClassificationRate(overallPrediction, categoryPrediction);
		printConfusionMatrix();
		printHighestLikelihoodWords();
	}
	
	/* Gets max from array */
	private int max(double[] posteriors){
		int likelyCategory = 0;
		double maxPosterior = posteriors[0];
		for (int i = 1; i < numCategories; i++){
			if (posteriors[i] > maxPosterior){
				maxPosterior = posteriors[i];
				likelyCategory = i;
			}
		}
		return likelyCategory;
	}
	
	public void printClassificationRate(double overallPrediction, double[] categoryPrediction){
		System.out.printf("\nOverall Prediction Accuracy = %.3f \n", overallPrediction);
		System.out.println("\n*** Classification Rates ***");
		for (int i = 0; i < numCategories; i++){
			System.out.printf("categoryPrediction[" + i + "] = %.3f \n", categoryPrediction[i]);
		}
	}
	
	public void printConfusionMatrix(){
		System.out.println("\n*** Confusion Matrix ***\n" + "     0      1      2      3      4      5      6      7");
		for (int i = 0; i < numCategories; i++){
			System.out.print(i + ": ");
			for (int j = 0; j < numCategories; j++){
				System.out.printf("%.3f  ", confusionMatrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public void printHighestLikelihoodWords(){
		/* Sorting HashMap learned from: http://java2novice.com/java-interview-programs/sort-a-map-by-value/ */
		for (int category = 0; category < numCategories; category++){
			LinkedList<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(likelihoods.get(category).entrySet());
			Collections.sort(list, new frequencyComparator<Map.Entry<String, Double>>());
			System.out.println("\n*** 20 most common words. Category: " + category + " ***");
			for (int i = 0; i < 20; i++){
				System.out.println(list.get(i).getKey());
			}
		}		
	}
	
	/****************************/
	/* Optional Print Functions */
	/****************************/
	
	public void printPriors(){
		System.out.println("\n*** Priors (for debugging) ***");
		for (int i = 0; i < numCategories; i++){
			System.out.println(" priors[" + i + "] = " + priors[i]);
		}
        System.out.println();
	}
	
	/* Prints a ton of info, which doesn't fit in "Console" */
	public void printLikelihoods(){
		double [] sums = new double[numCategories];
		for (int i = 0; i < numCategories; i++){
			HashMap<String, Double> map = likelihoods.get(i);
			for (String word : map.keySet()){
				sums[i] += map.get(word);
			}
		}
		System.out.println("*** Likelihoods (for debugging) ***");
		for (int i = 0; i < numCategories; i++){
			System.out.println("likelihoods[" + i +"] (sum) (should be ~1 for Mult. N. Bayes) = " + sums[i]);
		}
	}
	
	/* If this prints out the same number each time, there's an error in our posteriors calculation */
	public void printPosteriors0(){
		System.out.println("\n*** Posteriors ***");
		for (int i = 0; i < posteriors.length; i++){
			System.out.println(posteriors[i][0]);
		}
	}
}
