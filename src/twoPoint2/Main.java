package twoPoint2;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main (String [] args) throws FileNotFoundException, IOException{
		Data trainingData = new Data("news", "8category.training", 8);
		Data testData = new Data("news", "8category.testing", 8);
		MAP map = new MAP(trainingData, testData);
		map.calculateStuff(trainingData, testData);
	}
}