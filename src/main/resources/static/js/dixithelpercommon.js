/**
 * common used functions for dixithelper client pages start.html and game.html
 */
String.prototype.reverse = function() {
    return this.split("").reverse().join("");
}

function setCookie(cname, cvalue, exdays) {
      var d = new Date();
      d.setTime(d.getTime() + (exdays*24*60*60*1000));
      var expires = "expires="+ d.toUTCString();
      document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function getRabbit(size, color, top, left, stroke="none", strokeWidth ="0px", display = "inline-block") {
    return rabbitSVG.replace(/SIZE/g,size).
        replace("COLOR",color).
        replace("TOP",top).
        replace("LEFT",left).
        replace("STROKE-WIDTH",strokeWidth).
        replace("STROKE",stroke).
        replace("DISPLAY",display);
}

function getCircle(color,size,pos) {
    var circle = circleSVG.replace("COLOR",color).replace(/SIZE/g,size);
    if (pos==TOP_RIGHT_POS) {
        circle = circle.replace("POS","top:10px;right:10px");
    } else {
        circle = circle.replace("POS", pos);
    }
    return circle;
}

function alertIt(text,state) {
    if (text!=null && text!="" ) {
        alert(text);
    }
    errorHappened = true;
}

function sendRest(service, callBack, errorCallBack = alertIt, post=false, postData=null) {
    var xhttp = new XMLHttpRequest();
    errorHappened = false;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                callBack(xhttp.responseText)
            } else {
                errorCallBack(xhttp.responseText, this.status);
            }
        }
    };
    if (post) {
        xhttp.open("POST", myAddress+service, true);
        xhttp.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
        xhttp.send(JSON.stringify(postData));
    } else {
        xhttp.open("GET", myAddress+service, true);
        xhttp.send();
    }
}

function getEl(elId) {
    return document.getElementById(elId);
}

function hideEl(elId) {
    getEl(elId).style.display = "none";
}

function showEl(elId, displayMode="block") {
    getEl(elId).style.display = displayMode;
}
