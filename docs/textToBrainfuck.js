function processInput(inputID, outputID) {
    let input = document.getElementById(inputID).value;
    let outputElement = document.getElementById(outputID);
    
    let values = getUnicodeDiff(unicode(input));
    let output = "";
    
    for (let i = 0; i < values.length; i++) {
        output += brainfuckIncrement(Math.abs(values[i]), 1, (values[i] < 0 ? true : false));
    }
    
    output = removeRedundantShifts(output);
    outputElement.textContent = output + "\n";
}

function factors(n) {
    let a = -1;
    let b = -1;
    for (let i = 1; i <= Math.sqrt(n); i++) {
        if (n % i == 0) {
            a = i;
            b = n / i;
        }
    }
    return [a, b];
}

function unicode(input) {
    let unicode = new Array(input.length + 1);
    for (let i = 0; i < input.length; i++) {
        unicode[i] = input.charCodeAt(i);
    }
    unicode[input.length] = 10;
    return unicode;
}

function factorSearch(n) {
    let factor = [0, Number.MAX_VALUE];
    let adjustment = -1;
    for (let i = -2; i <= 2; i++) {
        if (n + i > 0) {
            let factorsArr = factors(n + i);
            if (Math.abs(factorsArr[0] - factorsArr[1]) < Math.abs(factor[0] - factor[1])) {
                factor = factorsArr;
                adjustment = -i;
            }
        }
    }
    return [factor[0], factor[1], adjustment];
}

function brainfuckIncrement(n, reg, decrement) {
    let output;
    let regForward = ">".repeat(reg);
    let regBackward = "<".repeat(reg);
    let symbol = decrement ? "-" : "+";
    
    if (n > 12) {
        let values = factorSearch(n);
        let outerCount = values[0];
        let innerCount = values[1];
        let adjustment = (decrement ? -1 : 1) * values[2];
        
        output = "+".repeat(outerCount)
            + "["
            + regForward
            + symbol.repeat(innerCount)
            + regBackward
            + "-]"
            + regForward
            + (adjustment < 0
                ? "-".repeat(-adjustment)
                : "+".repeat(adjustment));
    } else {
        output = regForward + symbol.repeat(n);
    }
    
    return output + "." + regBackward;
}

function getUnicodeDiff(unicode) {
    let diff = new Array(unicode.length);
    let a = 0;
    for (let i = 0; i < unicode.length; i++) {
        diff[i] = unicode[i] - a;
        a = unicode[i];
    }
    return diff;
}

function removeRedundantShifts(brainfuck) {
    return brainfuck.replace(/<>|></g, "");
}
