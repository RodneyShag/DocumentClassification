# DocumentClassification
Spam classification and sentiment analysis.

## Goals:
1) **Spam Classification:** Partition emails into 2 categories depending on whether they contain spam or not.<br/>
2) **Sentiment Analysis:** Partition movie reviews into 2 categories depending on whether they are positive or negative reviews.

## Classifiers:
1) [Multinomial Naive Bayes](https://en.wikipedia.org/wiki/Naive_Bayes_classifier#Multinomial_naive_Bayes)<br/>
2) [Bernoulli Naive Bayes](https://en.wikipedia.org/wiki/Naive_Bayes_classifier#Bernoulli_naive_Bayes)

## Data Formats:
1) **emails:** text documents<br/>
2) **movie reviews:** text documents

## Technique:
The goals above are each accomplished by training a Naive Bayes classifier on a set of training data, and then testing our classifier on a set of test data. We hope to have a high success rate in figuring out which emails contain spam, and whether an unseen movie review is positive or negative.
