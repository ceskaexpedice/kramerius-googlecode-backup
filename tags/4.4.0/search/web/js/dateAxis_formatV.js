
function formatSelectedTime(){
    $('#selectDiv').html("");
    var html = "";
    html += fromStr + " " + formatDate(selectStart);
    html += "<br/> " + toStr + " " + formatDate(selectEnd); 
    $('#selectDiv').html(html);
    $("#" + fromField).val("01.01."+selectStart);
    $("#" + toField).val("12.31."+selectEnd);
    //$("#" + fromField).val(formatDate(selectStart));
    //$("#" + toField).val(formatDate(selectEnd));

}

//Format textu v filtru
function formatBreadCrumb(){
    if($("#breadDateFilter")){
      var html = "";
      if(selectStart == selectEnd){
        html += " <b>" + selectStart + "</b> ";;
          
      }else{
        html += fromStr + " <b>" + selectStart;
        html += "</b> " + toStr + " <b>" + selectEnd 
      }
      $("#breadDateFilter span").html(html);
    }
}

function isValidDate(strString){
    return true;
     var strValidChars = "0123456789";
     var strChar;
     var blnResult = true;
  
     if (strString.length != 4) return false;
  
     //  test strString consists of valid characters listed above
     for (i = 0; i < strString.length && blnResult == true; i++)
        {
        strChar = strString.charAt(i);
        if (strValidChars.indexOf(strChar) == -1)
           {
           blnResult = false;
           }
        }
     return blnResult;
   }

function formatBarTitle(intDate){
    if(currentLevel==0){
    // decade
    var dekada = intDate.substring(2,3);
    var d = parseInt(dekada) + 1;
    
    return d + ". dekáda";
  }else if(currentLevel==1){
    //day
    return "rok " + intDate;
  }
  return intDate;

}                                
function formatGroupTitle(intDate){
  if(currentLevel==0){
    // stoleti
    return intDate.substring(0,4);
  }else if(currentLevel==1){
    //dekada
    //var dekada = intDate.substring(2,3);
    //var d = parseInt(dekada) + 1;
    //return d + ". dekáda";
    var dekada = intDate.substring(0,3);
    return intDate + "-" + dekada + "9";
  }
  return intDate;
}



var romanNumerals = ['I','II','III','IV','V','VI','VII','VIII','IX','X','XI','XII'];
function formatDate(intDate){ 
  return '01.01.'+intDate;
}

function indexOf(value, array){
    for(var i=0;i<array.length;i++){
        if(array[i]==value){
            return i;
        }
    }
    return null;
}

function decodeDate(strDate){
  return strDate;
}