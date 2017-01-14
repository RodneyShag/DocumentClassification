package twoPoint1;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main (String [] args) throws FileNotFoundException, IOException{
		Data trainingData = new Data("emails", "train_email");
		Data testData = new Data("emails", "test_email");
		MAP map = new MAP(trainingData, testData);
		map.calculateStuff(trainingData, testData);

		Data trainingData2 = new Data("movie_reviews", "rt-train");
		Data testData2 = new Data("movie_reviews", "rt-test");
		MAP map2 = new MAP(trainingData2, testData2);
		map2.calculateStuff(trainingData2, testData2);
	}
}
