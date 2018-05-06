const fs = require('fs');
const plotly = require('plotly')('Azhenot', 'uhhwGhKuE31FZl05XGz9');
//var natural = require('natural');




var lineReader = require('readline').createInterface({
    input: require('fs').createReadStream('wordSig0.txt')
});

var cpt = 0;
var phrase = [];
var corr = [];
lineReader.on('line', function (line) {
    //console.log('Line from file:', line);
    if(line === "0.0"){
        //console.log("ici")
    }else{
        corr.push(parseFloat(line));
        phrase.push(cpt);
    }
    ++cpt;

});

var lineReader2 = require('readline').createInterface({
    input: require('fs').createReadStream('wordSig1.txt')
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

setTimeout(function(){ 
    var trace1 = {
        x: phrase,
        y: corr,
        mode: "markers",
        type: "scatter",
        name: "WordOne"
    };
    var trace2 = {
        x: phrase2,
        y: corr2,
        mode: "markers",
        type: "scatter",
        name: "WordTwo"
    };
    var data = [trace1, trace2];
    //console.log(data);
    var graphOptions = {filename: "Sig Words 2", fileopt: "overwrite"};
    plotly.plot(data, graphOptions, function (err, msg) {
        console.log(msg);
    }); 


}, 5000);