const fs = require('fs');
const plotly = require('plotly')('Azhenot', 'uhhwGhKuE31FZl05XGz9');
//var natural = require('natural');




var lineReader = require('readline').createInterface({
    input: require('fs').createReadStream('sizePhrase.txt')
});

var cpt = 0;
var phrase = [];
var corr = [];
lineReader.on('line', function (line) {
    //console.log('Line from file:', line);
    corr.push(parseFloat(line));
    phrase.push(cpt);
    ++cpt;

});


setTimeout(function(){ 
    var trace1 = {
        x: phrase,
        y: corr,
        type: "scatter",
        name: "Size Phrase",
    };
    var data = [trace1];
    console.log(data);
    var graphOptions = {filename: "Smoothing", fileopt: "overwrite"};
    plotly.plot(data, graphOptions, function (err, msg) {
        console.log(msg);
    }); 


}, 5000);