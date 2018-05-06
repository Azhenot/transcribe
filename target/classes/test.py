import nltk
from nltk.stem.porter import PorterStemmer


def preprocessText(text):
	# To lower case
	text = text.lower();

	# Tokenize text
	tokenizedText = nltk.wordpunct_tokenize(text);
	tokenizedText = [w for w in tokenizedText if not w in (',' , '.' , '-' , '(' , ')' , '"' , "'" , ').' , '),' , '/' , ';' , ':' )]
	
	return tokenizedText


def preprocessPseudosentences(pseudosentences):
	processedSentences = []
	for pseudoSentence in pseudosentences:
		processedSentences.append(preprocessPseudosentence(pseudoSentence))
	return processedSentences

def preprocessPseudosentence(tokenizedText):
	# Remove Stopwords
	from nltk.corpus import stopwords
	filtered_words = [w for w in tokenizedText if not w in stopwords.words('english')]

	# Lemmatization Text
	from nltk.stem.wordnet import WordNetLemmatizer
	lemmatizedText = []
	for word in filtered_words:
		lemmatizedText.append( WordNetLemmatizer().lemmatize(word));
	
	return lemmatizedText

def createPseudosentences(text, width=20):
	start = 0
	pseudosentences = []
	while start < len(text):
		pseudosentences.append(text[start:start+width])
		start += width
	return pseudosentences


def blockScores(pseudosentences, printOutput = False):
	import math
	start = 0
	scores = []
	
	while start < len(pseudosentences)-4:
		block1 = list(pseudosentences[start]);
		block1.extend(pseudosentences[start+1]);
		block2 = list(pseudosentences[start+2]);
		block2.extend(pseudosentences[start+3]);
		start+=1
		
		if printOutput: print(block1)
		if printOutput: print(block2)
		
		# Block similarity metric
		terms = list(block1);
		terms.extend(block2);
		terms = list(set(terms));
		
		similarity = 0;
		w1 = 0;
		w2 = 0;
		for term in terms:
			wtb1 = 0
			wtb2 = 0
			for word in block1:
				if term == word:
					wtb1 += 1
			for word in block2:
				if word==term:
					wtb2 += 1
			similarity += wtb1 * wtb2
			w1 += wtb1 * wtb1
			w2 += wtb2 * wtb2
			if (wtb1 * wtb2 > 0):
				if printOutput: print('Common term "'+ term +'" wtb1 ='+str(wtb1)+' wtb2='+str(wtb2))
		score = similarity / math.sqrt((float) (w1 * w2))
		scores.append(score)
		if printOutput: print("Score:"+str(score))
	return scores
  
def vocabularyIntrodctions(pseudosentences, width = 20):
  """
  Calculate vocabulary introductions scores
  """
  from sets import Set
  # First get for each pseudosentence the set of new terms they introduce
  seenWords = Set()
  pseudosentence_new_terms = []
  for sentence in pseudosentences:
    newWords = Set()
    for word in sentence:
      if not (word in seenWords):
        newWords.add(word)
        seenWords.add(word)
    print(newWords )
    pseudosentence_new_terms.append(newWords)
      
  start = 0;
  scores = []
  while start < (len(pseudosentences) - 2):
    block1newTerms = len(pseudosentence_new_terms[start])
    block2newTerms = len(pseudosentence_new_terms[start+1])
    print("new terms in b1="+str(block1newTerms)+" in b2="+str(block2newTerms));
    score = (block1newTerms + block2newTerms) / (2. * width)
    print("Score: "+str(score))
    scores.append(score)
    start += 1
  
  return scores

def calculateDepth(scores):
	depthScores = []
	scIndex = 0;
	while scIndex < len(scores) -3:
		score = scores[scIndex] - 2*scores[scIndex+1] + scores[scIndex+2]
		depthScores.append(score)
		scIndex +=1
	mean = sum(depthScores)/len(depthScores)
	print('Mean:'+str(mean))
	return depthScores

# Retrieve text
f = open("subtitles.txt","r")
text = f.read();
f.close()

preprocessedText = preprocessText(text);

# Pseudosentences
pseudosentences = createPseudosentences(preprocessedText)
pseudosentences = preprocessPseudosentences(pseudosentences)
scores = blockScores(pseudosentences, printOutput = True)
print(scores)

#Depth scores
depthScores = calculateDepth(scores)
print(depthScores)

import matplotlib.pyplot as plt
plt.plot(scores)
plt.ylabel('some numbers')
plt.show()

#print vocabularyIntrodctions(pseudosentences)