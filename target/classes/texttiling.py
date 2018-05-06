from nltk import TextTilingTokenizer

ttt = TextTilingTokenizer()
file = open("textFull.txt", "r")
txt = file.read()
file.close()
print(txt)
print(ttt.tokenize(txt))