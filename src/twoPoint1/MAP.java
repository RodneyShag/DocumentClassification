package twoPoint1;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MAP {
	
	/* Priors, Likelihoods, Posteriors */
	double priorSpam;
	double priorNotSpam;
	HashMap<String, Double> likelihoodsSpam;
	HashMap<String, Double> likelihoodsNotSpam;
	double [] posteriorSpam;
	double [] posteriorNotSpam;
	
	/* Constructor */
	public MAP(Data trainingData, Data testData){
		/* Initialize Priors, Likelihoods, Posteriors */
		priorSpam    = 0;
		priorNotSpam = 0;
		likelihoodsSpam    = new HashMap<>();
		likelihoodsNotSpam = new HashMap<>();
		posteriorSpam    = new double[testData.numEntries];
		posteriorNotSpam = new double[testData.numEntries];
	}
	
//	public void ii(){
//		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
//		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/my_text_file.txt"));
//
//		final WordCloud wordCloud = new WordCloud(600, 600, CollisionMode.PIXEL_PERFECT);
//		wordCloud.setPadding(2);
//		wordCloud.setBackground(new CircleBackground(300));
//		wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
//		wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
//		wordCloud.build(wordFrequencies);
//		wordCloud.writeToFile("output/datarank_wordcloud_circle_sqrt_font.png");
//	}
	
	public void calculateStuff(Data trainingData, Data testData){
        calculatePriors(trainingData);
        calculateLikelihoods(trainingData, 1);
        calculatePosteriors(trainingData, testData);
        /* Uncomment the 2 lines below and replace the above 2 lines with them for Bernoulli method */
//        calculateLikelihoodsBernoulli(trainingData, 1); //do this version of Likelihoods for "Bernoulli" method
//        calculatePosteriorsBernoulli(trainingData, testData);

        printPriors();
        printLikelihoods();
        //printPosteriors();
        
        evaluate(testData);
	}
	
	/* Note: The Priors are going to be 0.5, 0.5 since that's the training data we were given */
	public void calculatePriors(Data trainingData){
		priorSpam = (double) trainingData.numSpamEntries / trainingData.numEntries;
		priorNotSpam = (double) trainingData.numNotSpamEntries / trainingData.numEntries;
	}
	
	/* From Lecture 12, Slide 26 */
	/* Note: Our vocabulary for SPAM words and NOT SPAM words is a joint vocabulary.
	 *       Example: there are words in "trainingData.spamWords" with 0 likelihood (before Laplace smoothing) */
	public void calculateLikelihoods(Data trainingData, int k){
		double occurrences;
		double likelihood;
		int V = trainingData.uniqueWords;
		
		for (String word : trainingData.wordsNoCategory.keySet()){
			/* Calculate likelihoods for SPAM words */
			if (trainingData.spamWords.containsKey(word))
				occurrences = trainingData.spamWords.get(word);
			else
				occurrences = 0;
			likelihood = (double) (occurrences + k) / (trainingData.totalSpamWords + k*V);
			likelihoodsSpam.put(word, likelihood);

			/* Calculate likelihoods for NOT SPAM words */
			if (trainingData.notSpamWords.containsKey(word))
				occurrences = trainingData.notSpamWords.get(word);
			else
				occurrences = 0;
			likelihood = (double) (occurrences + k) / (trainingData.totalNotSpamWords + k*V);
			likelihoodsNotSpam.put(word, likelihood);
		}	
	}
	
	public void calculateLikelihoodsBernoulli(Data trainingData, double k){
		double occurrences;
		double likelihood;
		int V = 2;
		
		HashMap<String, Integer> mapSpam    = new HashMap<>(); //keeps track of # of documents with a specific word.
		HashMap<String, Integer> mapNotSpam = new HashMap<>(); //keeps track of # of documents with a specific word.
		
		for (String word: trainingData.wordsNoCategory.keySet()){
			mapSpam.put(word, 0);
			mapNotSpam.put(word, 0);
		}
		for (HashMap<String, Integer> entry: trainingData.spamEntries){
			for (String word : entry.keySet()){
				mapSpam.put(word, mapSpam.get(word) + 1);
			}
		}
		for (HashMap<String, Integer> entry: trainingData.notSpamEntries){
			for (String word : entry.keySet()){
				mapNotSpam.put(word, mapNotSpam.get(word) + 1);
			}
		}
		
		for (String word : trainingData.wordsNoCategory.keySet()){
			/* Calculate likelihoods for SPAM words */
			occurrences = mapSpam.get(word);
			likelihood = (double) (occurrences + k) / (trainingData.numSpamEntries + k*V);
			likelihoodsSpam.put(word, likelihood);

			/* Calculate likelihoods for NOT SPAM words */
			occurrences = mapNotSpam.get(word);
			likelihood = (double) (occurrences + k) / (trainingData.numNotSpamEntries + k*V);
			likelihoodsNotSpam.put(word, likelihood);
		}	
	}
	
	public void calculatePosteriors(Data trainingData, Data testData){
		double likelihood;
		for (int i = 0; i < testData.numEntries; i++){
			posteriorSpam[i] = Math.log(priorSpam);
			posteriorNotSpam[i] = Math.log(priorNotSpam);
			
			HashMap<String, Integer> entry = testData.entries.get(i);
			for (String word : entry.keySet()){
				if (trainingData.wordsNoCategory.containsKey(word)){ //this skips words not in our vocabulary.
					int occurrences = entry.get(word);
					
					likelihood = likelihoodsSpam.get(word);
					posteriorSpam[i] += occurrences * Math.log(likelihood);
					
					likelihood =  likelihoodsNotSpam.get(word);
					posteriorNotSpam[i] += occurrences * Math.log(likelihood);
				}
			}
		}
	}
	
	/* We loop through all vocab words here */
	public void calculatePosteriorsBernoulli(Data trainingData, Data testData){
		double likelihood;
		for (int i = 0; i < testData.numEntries; i++){
			posteriorSpam[i] = Math.log(priorSpam);
			posteriorNotSpam[i] = Math.log(priorNotSpam);
			
			HashMap<String, Integer> entry = testData.entries.get(i);
			for (String word : trainingData.wordsNoCategory.keySet()){
				if (entry.containsKey(word)) 
					likelihood = likelihoodsSpam.get(word);
				else
					likelihood =  1 - likelihoodsSpam.get(word);
				posteriorSpam[i] += Math.log(likelihood);
				
				if (entry.containsKey(word)) 
					likelihood = likelihoodsNotSpam.get(word);
				else
					likelihood =  1 - likelihoodsNotSpam.get(word);
				posteriorNotSpam[i] += Math.log(likelihood);
			}
		}
	}
	
	public void evaluate(Data testData){
		double overallPrediction = 0;
		double spamPrediction = 0;
		double notSpamPrediction = 0;
		for (int i = 0; i < testData.numEntries; i++){
			boolean category = testData.isSpam.get(i);
			boolean prediction;
			if (posteriorSpam[i] > posteriorNotSpam[i])
				prediction = true;
			else
				prediction = false;
			if (category == prediction){
				overallPrediction++;
				if (category == true)
					spamPrediction++;
				else
					notSpamPrediction++;
			}
		}
		overallPrediction /= (double) testData.numEntries;
		spamPrediction /= testData.numSpamEntries;
		notSpamPrediction /= testData.numNotSpamEntries;
		printClassificationRate(overallPrediction, spamPrediction, notSpamPrediction);
		printHighestLikelihoodWords();
	}
	
	public void printClassificationRate(double overallPrediction, double spamPrediction, double notSpamPrediction){
		System.out.println("\n*** Classification Rates ***");
		System.out.printf("Overall Prediction Accuracy = %.3f \n", overallPrediction);
		System.out.printf("Spam Prediction Accuracy = %.3f \n", spamPrediction);
		System.out.printf("Not Spam Prediction Accuracy = %.3f \n", notSpamPrediction);
	}
	
	public void printHighestLikelihoodWords(){
		/* Sorting HashMap learned from: http://java2novice.com/java-interview-programs/sort-a-map-by-value/ */
		LinkedList<Map.Entry<String, Double>> spamList = new LinkedList<Map.Entry<String, Double>>(likelihoodsSpam.entrySet());
		LinkedList<Map.Entry<String, Double>> notSpamList = new LinkedList<Map.Entry<String, Double>>(likelihoodsNotSpam.entrySet());
		Collections.sort(spamList, new frequencyComparator<Map.Entry<String, Double>>());
		Collections.sort(notSpamList, new frequencyComparator<Map.Entry<String, Double>>());
		
		
		System.out.println("\n*** 20 most common SPAM (or POSITIVE REVIEW) words ***");
		for (int i = 0; i < 20; i++){
			System.out.println(spamList.get(i).getKey());
		}
		System.out.println("\n*** 20 most common NOT SPAM (or NEGATIVE REVIEW) words ***");
		for (int i = 0; i < 20; i++){
			System.out.println(notSpamList.get(i).getKey());
		}
	}
	
	/****************************/
	/* Optional Print Functions */
	/****************************/
	
	public void printPriors(){
		System.out.println("\n*** Priors (for debugging) ***");
		System.out.println(" priorSpam = " + priorSpam);
		System.out.println(" priorNotSpam = " + priorNotSpam);
        System.out.println();
	}
	
	/* Prints a ton of info, which doesn't fit in "Console" */
	public void printLikelihoods(){
		double sum1 = 0;
		double sum2 = 0;
		for (String word : likelihoodsSpam.keySet()){
			sum1 += likelihoodsSpam.get(word);
		}
		for (String word : likelihoodsNotSpam.keySet()){
			sum2 += likelihoodsNotSpam.get(word);
		}
		System.out.println("*** Likelihoods (for debugging) ***");
		System.out.println("likelihoodsSpam    (sum) (should be ~1 for Mult. N. Bayes) = " + sum1);
		System.out.println("likelihoodsNotSpam (sum) (should be ~1 for Mult. N. Bayes) = " + sum2);
	}
	
	public void printPosteriors(){
		System.out.println("\n*** Posteriors ***");
		for (int i = 0; i < posteriorSpam.length; i++){
			System.out.println(posteriorSpam[i]);
		}
	}
}
