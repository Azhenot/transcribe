from readless.Segmentation import texttiling
segmentation = texttiling.TextTiling()
pathToFile = "C:/conversation.in"
segmentedText = segmentation.segmentFile(pathToFile)