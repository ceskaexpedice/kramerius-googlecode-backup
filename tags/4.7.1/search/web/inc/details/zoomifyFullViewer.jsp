<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style type="text/css">

    #zoomifyFullImageContainer>div.header{
        height:25px;
        padding-top:5px;
        padding-right:10px;
        border-bottom:1px solid #E66C00;
        background-color: white;
    }
    #zoomifyFullImageContainer>div.header>div.title{
        float:left;
        padding-left: 30px;
        font-size: 1.2em;
    }
    #zoomifyFullImageContainer>div.header>div.buttons{
        float:right;
        
    }

    #zoomifyFullImageContainer>div.header>div.buttons>a{
        float:left;
    }
     
         
</style>

<div class="header">
    <div class="title"></div>
    <div class="buttons">
	
       <a id="fullZoomifyPlusButton" onclick="javascript:fullZoomInit.plus();" style="z-index: 1002"><span class="ui-icon ui-icon-plus" >+</span></a>
       <a id="fullZoomifyMinusButton" onclick="javascript:fullZoomInit.minus();" style="z-index: 1002"><span class="ui-icon ui-icon-minus" >-</span></a>
       <a id="fullZoomifyButtonPrev" onclick="javascript:previousImage();" style="z-index: 1002"><span class="ui-icon ui-icon-arrowthick-1-w" >prev</span></a>
       <a id="fullZoomifyButtonNext" onclick="javascript:nextImage();" style="z-index: 1002"><span class="ui-icon ui-icon-arrowthick-1-e" >next</span></a>
	
	    <a href="javascript:hideFullZoomify();"><span class="ui-icon ui-icon-closethick">close</span></a>
    </div>
</div>

<div class="fullContent" style="width:100%;overflow:auto;">

    <div id="full-ol-wrapper" style="width:100%; height:100%;">
        <div id="full-ol-image" style="width: 100%; height: 100%"></div>
    </div>

    <div id="imgContainer" style="display:none;position:relative;" align="center">
        <img id="loadingFull" src="img/loading11.gif" style="display:none;margin-top:30px;" />
    </div>

</div>

<script type="text/javascript">
 	var fullZoomInit = new ZoomifyViewerInitObject();
 	fullZoomInit.init({
		"ol-image":"full-ol-image",
		"ol-wrapper":"full-ol-wrapper",
		"ol-overview":"full-ol-overview"
 	});

    $(document).ready(function(){
     	
        $('#zoomifyFullImageContainer.viewer').bind('viewReady', function(event, viewerOptions){
			updateZoomifyFullImage();
        });
        $('#zoomifyFullImageContainer.viewer').bind('viewChanged', function(event, id){
            $('#imgFullImage').attr('src', 'img/empty.gif');
            $("#loadingFull").show();
            //hideAltoFull();
        });
    });
     
    function updateZoomifyFullImage(){
        if($('#zoomifyFullImageContainer').is(":visible")){
			console.log("display zoomify");
			var fullUrl = "img?uuid="+viewerOptions.uuid+"&stream=IMG_FULL&action=GETRAW";
			var p = '';
			for(var i=0; i<k4Settings.selectedPathTexts.length; i++){
			    p += '<span style="float:left;" class="ui-icon ui-icon-triangle-1-e">folder</span><span style="float:left;">' + k4Settings.selectedPathTexts[i] + '</span>' ;
			}
			$('#zoomifyFullImageContainer>div.header>div.title').html(p);
			
			//viewer options must exists			
            console.log("open fullZoomInit");
            if (viewerOptions.hasAlto) {
			
                fullZoomInit.open(viewerOptions.uuid, viewerOptions.alto);
            } else {
                fullZoomInit.open(viewerOptions.uuid);
            }
        }else{
            var fullUrl = "img/empty.gif";
        }
    }
    
</script>