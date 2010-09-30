/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    $('body').click(function() {
    	hideAdminMenu();
    });
});


function showConfirmDialog(t,f){
    $("#confirm_dialog").dialog('destroy');
    $( "#proccess_confirm_text" ).html(t);
    $( "#confirm_dialog" ).dialog({
        resizable: false,
        height:140,
        modal: true,
        buttons: {
            Ok: function() {
                $(this).dialog('destroy');
                f();
                //return true;
            },
            Cancel: function() {
                $(this).dialog('destroy');
                //return false;
            }
        }

    });
}

function showAdminMenu() {
	var headerPosition = $("#header").offset();
	var headerWidth = $("#header").width()
	var admimMenuWidth = $("#adminMenu").width();
	
	var position = $("#adminHref").offset();
	
	
	$("#adminMenu").css("left",(headerPosition.left+headerWidth) - admimMenuWidth-2);
	$("#adminMenu").css("top",position.top);
	$("#adminMenu").css("display","block");
}

function hideAdminMenu() {
	$("#adminMenu").css("display","none");
}

var _processDialog; // dialog na zobrazovani proceus
function openProcessDialog() {
	if (_processDialog) {
		_processDialog.dialog('open');
	} else {
    	_processDialog = $("#processes").dialog({
	        bgiframe: true,
	        width: 700,
	        height: 600,
	        modal: true,
	        title: dictionary['administrator.menu.dialogs.lrprocesses.title'],
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	}
}
function processes(){
	var url = "dialogs/_processes_data.jsp?offset=0&size=20&type=DESC";
	$.get(url, function(data) {
		openProcessDialog();
		$("#processes").html(data);
	});
}

function modifyProcessDialogData(ordering, offset, size, type) {
	var url = "dialogs/_processes_data.jsp?ordering="+ordering+"&offset="+offset+"&size="+size+"&type="+type;
	$.get(url, function(data) {
		$("#processes").html(data);
	});
}

function killAndRefresh(url,ordering, offset, size, type) {
	$.get(url, function(fdata) {
		refreshProcesses(ordering, offset, size, type);
	});
}

function refreshProcesses(ordering, offset, size, type) {
	var refreshurl = "dialogs/_processes_data.jsp?ordering="+ordering+"&offset="+offset+"&size="+size+"&type="+type;
	$.get(refreshurl, function(sdata) {
		$("#processes").html(sdata);
	});
}

/**
 * Promenne ve scriptu
 */
// Command pattern
var _texts=function() {
	var intArr = new Array(); {
		intArr["[static_export_CD]WAITING"]='administrator.dialogs.waitingexport';
		intArr["[static_export_CD]PLANNED"]='administrator.dialogs.exportrunning';
		intArr["[static_export_CD]FAILED"]='administrator.dialogs.exportfailed';

		intArr["[reindex]WAITING"]='administrator.dialogs.waitingreindex';
		intArr["[reindex]PLANNED"]='administrator.dialogs.reindexrunning';
		intArr["[reindex]FAILED"]='administrator.dialogs.reindexfailed';

		
		intArr["[replikator_monographs]WAITING"]='administrator.dialogs.waitingmonographimport';
		intArr["[replikator_monographs]PLANNED"]='administrator.dialogs.monographimportrunning';
		intArr["[replikator_monographs]FAILED"]='administrator.dialogs.monographimportfailed';
		
		intArr["[replikator_periodicals]WAITING"]='administrator.dialogs.waitingperiodicsimport';
		intArr["[replikator_periodicals]PLANNED"]='administrator.dialogs.periodicsimportrunning';
		intArr["[replikator_periodicals]FAILED"]='administrator.dialogs.periodicsimportfailed';
		
		intArr["[enumerator]WAITING"]='administrator.dialogs.waitingenumerator';
		intArr["[enumerator]PLANNED"]='administrator.dialogs.enumeratorrunning';
		intArr["[enumerator]FAILED"]='administrator.dialogs.enumeratorfailed';
				
		intArr["[replicationrights]WAITING"]="administrator.dialogs.waitingreplicationrights";
		intArr["[replicationrights]PLANNED"]="administrator.dialogs.replicationrightsrunning";
		intArr["[replicationrights]FAILED"]="administrator.dialogs.replicationrightsfailed";

		intArr["[delete]WAITING"]="administrator.dialogs.waitingdelete";
		intArr["[delete]PLANNED"]="administrator.dialogs.deleterunning";
		intArr["[delete]FAILED"]="administrator.dialogs.deletefailed";


		intArr["[replicationrights]WAITING"]="administrator.dialogs.waitingreplicationrights";
		intArr["[replicationrights]PLANNED"]="administrator.dialogs.replicationrightsrunning";
		intArr["[replicationrights]FAILED"]="administrator.dialogs.replicationrightsfailed";

		intArr["[setpublic]WAITING"]="administrator.dialogs.waitingchangevisflag";
		intArr["[setpublic]PLANNED"]="administrator.dialogs.setprivaterunning";
		intArr["[setpublic]FAILED"]="administrator.dialogs.setprivatefailed";
		
		intArr["[setprivate]WAITING"]="administrator.dialogs.waitingchangevisflag";
		intArr["[setprivate]PLANNED"]="administrator.dialogs.setpublicrunning";
		intArr["[setprivate]FAILED"]="administrator.dialogs.setpublicfailed";

		intArr["[export]WAITING"]="administrator.dialogs.waitingexport";
		intArr["[export]PLANNED"]="administrator.dialogs.exportrunning";
		intArr["[export]FAILED"]="administrator.dialogs.exportfailed";

		intArr["[convert]WAITING"]="administrator.dialogs.waitingconvert";
		intArr["[convert]PLANNED"]="administrator.dialogs.convertrunning";
		intArr["[convert]FAILED"]="administrator.dialogs.convertfailed";

		intArr["[import]WAITING"]="administrator.dialogs.waitingimport";
		intArr["[import]PLANNED"]="administrator.dialogs.importrunning";
		intArr["[import]FAILED"]="administrator.dialogs.importfailed";

	}
	return intArr;
}(); //akce ze servletu




function importMonographs() {
    showConfirmDialog('Confirm import monografii', function(){
		var url = "lr?action=start&def=replikator_monographs&out=text";
		if (_commonDialog) {

			$("#common_started_ok").hide();
	    	$("#common_started_failed").hide();
	    	$("#common_started_waiting").show();
	
	    	_commonDialog.dialog('open');
	
		} else {
	
	    	$("#common_started_waiting").show();
	    	_commonDialog = $("#common_started").dialog({
		        bgiframe: true,
		        width: 400,
		        height: 100,
		        modal: true,
		        title:'',
		        buttons: {
		            "Close": function() {
		                $(this).dialog("close"); 
		            } 
		        } 
		    });
		}
		$("#common_started_text").text(dictionary['administrator.dialogs.waitingmonographimport']);
		$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.importMonograph.title']);
	
		_startProcess(url);
    });
}


function importPeriodicals() {
    showConfirmDialog('Confirm import periodik', function(){
		var url = "lr?action=start&def=replikator_periodicals&out=text";

		if (_commonDialog) {

			$("#common_started_ok").hide();
	    	$("#common_started_failed").hide();
	    	$("#common_started_waiting").show();
	
	    	_commonDialog.dialog('open');
		} else {
	    	$("#common_started_waiting").show();
	    	_commonDialog = $("#common_started_waiting").dialog({ 
		        bgiframe: true,
		        width: 400,
		        height: 100,
		        modal: true,
		        title:'',
		        buttons: {
		            "Close": function() {
		                $(this).dialog("close"); 
		            } 
		        } 
		    });
		}

		$("#common_started_text").text(dictionary['administrator.dialogs.waitingperiodicsimport']);
		$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.importPeriodical.title']);

		_startProcess(url);
    });
}

function replaceAll(txt, replace, with_this) {
	  return txt.replace(new RegExp(replace, 'g'),with_this);
}

/**
 * Reindexace
 * @param level
 * @return
 */
function reindex(level, model) {
	hideAdminOptions(level);
	var uuid = $("#tabs_"+level).attr('pid');
	var title = $("#info-"+model+" li.value").text();
	//var escapedTitle = replaceAll(title, ',', '\\');

	var url = "lr?action=start&def=reindex&out=text&params=reindexDoc,"+uuid+","+title;
    if (_commonDialog) {

		$("#common_started_ok").hide();
    	$("#common_started_failed").hide();
    	$("#common_started_waiting").show();

    	_commonDialog.dialog('open');
	} else {
    	$("#common_started_waiting").show();
    	_commonDialog = $("#common_started").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title:'',
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	
	}
    
	$("#common_started_text").text(dictionary['administrator.dialogs.waitingreindex']);
	$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.reindex.title']);

	_startProcess(url);
}

function _startProcess(url) {
	$.get(url, function(data) {
		var text = _texts[data];
		var t = dictionary[text];
		if (data.match("PLANNED$")=="PLANNED") {
			_processTextOk(t);
			setTimeout(_processStarted, 3000);
		} else {
			_processFailed(t);
			setTimeout(_processFailed, 3000);
		}

	});
}



function exportTOFOXML(level)  {
	hideAdminOptions(level);
	var pid = $("#tabs_"+level).attr('pid');
	var pidpath = COMMON.pidpath(level);
	var url = "lr?action=start&def=export&out=text&params="+pid;
	if (_commonDialog) {
    	$("#common_started_ok").hide();
    	$("#common_started_failed").hide();
    	$("#common_started_waiting").show();
    	_commonDialog.dialog('open');
	} else {
    	$("#common_started_waiting").show();
    	_commonDialog = $("#common_started").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title: '',
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	}

	$("#common_started_text").text(dictionary['administrator.dialogs.waitingfoexport']);
	$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.foexport.title']);

	_startProcess(url);
}


function noParamsProcess(process)  {
	var url = "lr?action=start&def="+process+"&out=text";
	if (_commonDialog) {
    	$("#common_started_ok").hide();
    	$("#common_started_failed").hide();
    	$("#common_started_waiting").show();
    	_commonDialog.dialog('open');
	} else {
    	$("#common_started_waiting").show();
    	_commonDialog = $("#common_started").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title: '',
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	}

	$("#common_started_text").text(dictionary['administrator.dialogs.waiting'+process]);
	$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.'+process+'.title']);
	_startProcess(url);
}


function deleteUuid(level, model)  {
	hideAdminOptions(level);
	showConfirmDialog(dictionary['administrator.dialogs.deleteconfirm'], function(){
		var pid = $("#tabs_"+level).attr('pid');
		var pidpath = COMMON.pidpath(level);
		var url = "lr?action=start&def=delete&out=text&params="+pid+","+pidpath;
		if (_commonDialog) {
	    	$("#common_started_ok").hide();
	    	$("#common_started_failed").hide();
	    	$("#common_started_waiting").show();
	    	_commonDialog.dialog('open');
		} else {
	    	$("#common_started_waiting").show();
	    	_commonDialog = $("#common_started").dialog({
		        bgiframe: true,
		        width: 400,
		        height: 100,
		        modal: true,
		        title: '',
		        buttons: {
		            "Close": function() {
		                $(this).dialog("close"); 
		            } 
		        } 
		    });
		}
	
		$("#common_started_text").text(dictionary['administrator.dialogs.waitingdelete']);
		$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.deleteuuid']);
	
		_startProcess(url);
	});
}

var _checkDialog;
function changeFlag(level)  {
	hideAdminOptions(level);
	if (_checkDialog) {
		_checkDialog.dialog('open');
	} else {
		_checkDialog = $("#check_private_public").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title: dictionary['administrator.menu.dialogs.changevisflag.title'],
	        buttons: {
				"Close": function() {
						$(this).dialog("close"); 
	            }, 
	            // nevim jak lokalizovat button ?
	            "Aplikuj": function() {
					$(this).dialog("close"); 
					
					var flag = $('#flag').val();
	            	var pid = $("#tabs_"+level).attr('pid');
	            	var url = "lr?action=start&def=set"+flag+"&out=text&params="+pid;
					if (_commonDialog) {
				    	$("#common_started_ok").hide();
				    	$("#common_started_failed").hide();
				    	$("#common_started_waiting").show();
				    	_commonDialog.dialog('open');
					} else {
				    	$("#common_started_waiting").show();
				    	_commonDialog = $("#common_started").dialog({
					        bgiframe: true,
					        width: 400,
					        height: 100,
					        modal: true,
					        title: dictionary['administrator.menu.dialogs.changevisflag.title'],
					        buttons: {
					            "Close": function() {
					                $(this).dialog("close"); 
					            } 
					        } 
					    });
					}

 					$("#common_started_text").text(dictionary['administrator.dialogs.waitingchangevisflag']);
					$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.changevisflag.title']);

					_startProcess(url);
				
		        }
        	}
	    });
		
	}

}



/**
 * Generovani staticke exportu
 * @param level
 * @return
 */
function generateStatic(level, exportType, imgUrl, i18nUrl,iso3Country, iso3Lang){
	hideAdminOptions(level);
	var pid = $("#tabs_"+level).attr('pid');
	var url = "lr?action=start&def="+exportType+"&out=text&params="+pid+","+imgUrl+","+i18nUrl+","+iso3Country+","+iso3Lang;
	if (_commonDialog) {
    	$("#common_started_ok").hide();
    	$("#common_started_failed").hide();
    	$("#common_started_waiting").show();
    	_commonDialog.dialog('open');
	} else {
    	$("#common_started_waiting").show();
    	_commonDialog = $("#common_started").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title: "",
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	}
	
	$("#common_started_text").text(dictionary['administrator.dialogs.waitingexport']);
	$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.staticPDF.title']);

	_startProcess(url);
}

/**
 * Enumerator
 * @param level
 * @return
 */
var _commonDialog; //cekaci dialog na spusteni procesu


function _processTextOk(text) {
	$("#common_started_text_ok").text(text);
}

function _processStarted() {
	$("#common_started_ok").show();
	$("#common_started_failed").hide();
	$("#common_started_waiting").hide();
}

function _processTextFailed(text) {
	$("#common_started_text_failed").text(text);
}

function _processFailed() {
	$("#common_started_waiting").css("display","none");
	$("#common_started_ok").css("display","block");

}



var _indexerDialog;
/**
 * Zobrazuje spravu indexace
 */
function showIndexerAdmin(){
    hideAdminMenu();
    var url = "dialogs/_indexer_data.jsp?offset=0";
    $.get(url, function(data) {
        $("#indexerContent").html(data);
    });
    if (_indexerDialog) {
        _indexerDialog.dialog('open');
    } else {
    	_indexerDialog = $("#indexer").dialog({
            bgiframe: true,
            width: 700,
            height: 400,
            modal: true,
	        title: dictionary['administrator.menu.dialogs.indexDocuments.title'],
            buttons: {
                "Close": function() {
                    $(this).dialog("close"); 
                } 
            } 
        });
    }
}

function loadFedoraDocuments(model, offset, sort, sort_dir){
    var url = "dialogs/_indexer_data.jsp?model="+model+"&offset="+offset+"&sort="+sort+"&sort_dir="+sort_dir;
    $.get(url, function(data) {
        $("#indexerContent").html(data);
        //$("tr."+model).toggleClass('selected');
    });
}

function getIndexerStatus(){
    
}

function deletefromindex(level){
	hideAdminOptions(level);
    showConfirmDialog('Confirm delete dokument from index', function(){
      var pid = $("#tabs_"+level).attr('pid');
      var pid_path = "";
      for(var i = level; i>0; i--){
          pid_path = $('#tabs_'+i).attr('pid') + pid_path;
          if(i>1) {pid_path = '/' + pid_path};
      }

  	if (_commonDialog) {
    	$("#common_started_ok").hide();
    	$("#common_started_failed").hide();
    	$("#common_started_waiting").show();
    	_commonDialog.dialog('open');
	} else {
    	$("#common_started_waiting").show();
    	_commonDialog = $("#common_started").dialog({
	        bgiframe: true,
	        width: 400,
	        height: 100,
	        modal: true,
	        title: '',
	        buttons: {
	            "Close": function() {
	                $(this).dialog("close"); 
	            } 
	        } 
	    });
	}

	$("#common_started_text").text(dictionary['administrator.dialogs.waitingdelindex']);
	$("#common_started" ).dialog( "option", "title",  dictionary['administrator.menu.dialogs.delindex.title']);

      var url = "lr?action=start&def=reindex&out=text&params=deleteDocument,"+pid_path+","+pid;
      _startProcess(url);
    });
}
function indexDoc(pid, title){
    showConfirmDialog('Confirm index dokumentu', function(){
      var prefix = "info:fedora/uuid:";
      var uuid = pid.substr(prefix.length);
      var url = "lr?action=start&def=reindex&out=text&params=fromKrameriusModel,"+uuid+","+title;
      _startProcess(url);
    });
}

function indexModel(model){
    showConfirmDialog('Confirm index cely model', function(){
      var url = "lr?action=start&def=reindex&out=text&params=krameriusModel,"+model+","+model;
      _startProcess(url);
    });
}