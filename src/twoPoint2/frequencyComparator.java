package twoPoint2;

import java.util.Comparator;
import java.util.Map;

/* Sort HashMap in DESCENDING order by value */
public class frequencyComparator<T> implements Comparator<Map.Entry<String, Double>>{
	@Override
	public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2){
		return m2.getValue().compareTo(m1.getValue()); //for descending order
	}
}
