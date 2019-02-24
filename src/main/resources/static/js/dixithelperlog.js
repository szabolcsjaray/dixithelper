var log;
var ct;
var width;
var height;
var lastR;

function gameLog() {
    showEl("log");
    let c = getEl("logCanvas");
    width = c.offsetWidth;
    c.width = width;
    height = c.offsetHeight;
    c.height = height;
    ct = c.getContext("2d");
    ct.lineWidth=2;
    ct.strokeStyle = "black";
    ct.beginPath();
    ct.moveTo(5, height-10);
    ct.lineTo(width-10,height-10);
    ct.stroke();
    
    ct.moveTo(10,10);
    ct.lineTo(10,height);
    ct.stroke();
    
    sendRest("getlog",gotGameLog);
}

function getRowX(row) {
    return 5+Math.round((width-15)*row/lastR);
}

function getMaxPoint() {
    let round = log.rounds[lastR-1];
    let max = 0;
    round.playerRounds.forEach(function(pRound) {
        if (max < pRound.point) {
            max = pRound.point;
        }
    });
    return max;
}

function gotGameLog(logResponse) {
    log = JSON.parse(logResponse);
    if (log.lastRound==0) {
        let lDiv = getEl("logDiv");
        lDiv.innerHTML = "Még nem kezdődött el a játék.";
        return;
    }
    lastR = log.lastRound - (game.state=="ROUND_ENDED" ? 0 :1)
    let maxPoint = getMaxPoint();
    ct.lineWidth=1;
    ct.strokeStyle = "#909090";
    for(let i=1;i<=lastR;i++) {
        let x = getRowX(i);
        ct.beginPath();
        ct.moveTo(x,10);
        ct.lineTo(x,height-10);
        ct.stroke();
    }
}1