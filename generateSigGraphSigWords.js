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

var lineReader3 = require('readline').createInterface({
    input: require('fs').createReadStream('wordSig2.txt')
});

var cpt3 = 0;
var phrase3 = [];
var corr3 = [];
lineReader3.on('line', function (line) {
    //console.log('Line from file:', line);
    if(line === "0.0"){
        //console.log("ici")
    }else{
        corr3.push(parseFloat(line));
        phrase3.push(cpt3);
    }
    ++cpt3;

});

setTimeout(function(){ 
    var trace1 = {
        x: phrase,
        y: corr,
        mode: "markers",
        type: "scatter",
        name: "The",
        marker: {
            size: 12,
            line: {
            color: "yellow",
            width: 0.5
            }
        }
    };
    var trace2 = {
        x: phrase2,
        y: corr2,
        mode: "markers",
        type: "scatter",
        name: "Fibonacci",
        marker: {
            size: 12,
            line: {
            color: "green",
            width: 0.5
            }
        }
    };
    var trace3 = {
        x: phrase3,
        y: corr3,
        mode: "markers",
        type: "scatter",
        name: "Ford",
        marker: {
            size: 12,
            line: {
            color: "blue",
            width: 0.5
            }
        }
    };
    var data = [trace1, trace2, trace3];
    //console.log(data);
    var graphOptions = {filename: "Sig words", fileopt: "overwrite"};
    plotly.plot(data, graphOptions, function (err, msg) {
        console.log(msg);
    }); 


}, 5000);