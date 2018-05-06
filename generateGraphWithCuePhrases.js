const fs = require('fs');
const plotly = require('plotly')('Azhenot', 'uhhwGhKuE31FZl05XGz9');
//var natural = require('natural');




var lineReader = require('readline').createInterface({
    input: require('fs').createReadStream('outText.txt')
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

var lineReader2 = require('readline').createInterface({
    input: require('fs').createReadStream('outTextMinimums.txt')
});

var cpt2 = 0;
var phrase2 = [];
var corr2 = [];
lineReader2.on('line', function (line) {
    //console.log('Line from file:', line);
    if(line === "0.0"){
        //console.log("ici")
    }else{
        corr2.push(parseFloat(line));
        phrase2.push(cpt2);
    }
    ++cpt2;

});

var lineReader3 = require('readline').createInterface({
    input: require('fs').createReadStream('scoreCuePhrase.txt')
});

var cpt3 = 0;
var phrase3 = [];
var corr3 = [];
lineReader3.on('line', function (line) {
    corr3.push(parseFloat(line));
    phrase3.push(cpt3);
    ++cpt3;

});

setTimeout(function(){ 
    var trace1 = {
        x: phrase,
        y: corr,
        type: "scatter"
    };
    var trace2 = {
        x: phrase2,
        y: corr2,
        mode: "markers",
        name: "Minimums",
        marker: {
            color: "rgb(255, 217, 102)",
            size: 12,
            line: {
            color: "blue",
            width: 0.5
            }
        },
        type: "scatter"
    };
    var trace3 = {
        x: phrase3,
        y: corr3,
        type: "scatter"
    };
    var data = [trace1, trace2, trace3];
    var graphOptions = {filename: "cuePhrase", fileopt: "overwrite"};
    plotly.plot(data, graphOptions, function (err, msg) {
        console.log(msg);
    }); 


}, 5000);