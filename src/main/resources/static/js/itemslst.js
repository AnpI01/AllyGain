
$('body').on('click','#itemsresultsb', function(){
 $("#itemsresultsb").hide();
 $("#itemlistdim").show();
 var formData = $("#itemresultsform").serializeArray();
var URL = $("#itemresultsform").attr("action");
$.post(URL,
formData,
function(data, textStatus, jqXHR)
{    
	if(textStatus == 'success'){ 			    
	var newcurvl = $($.parseHTML(data)).filter("#curnewval").val();
	if(newcurvl == 'end'){
		$("#itemsresultsb").hide();
		$("#itemlistdim").hide();
	}else{
		$("#itemlistdim").hide();
 	    $("#itemsresultsb").show();
	}
	$("#curval").val(newcurvl);

	$('#cpnresults').append(data);
	}else{
		$("#itemlistdim").hide();
 	    $("#itemsresultsb").show();
	}
}).fail(function(jqXHR, textStatus, errorThrown) 
{$("#itemlistdim").hide();
 $("#itemsresultsb").show();  });
}); 

$('body').on('change','input[type=radio]', function(){
	$.fn.procFiltDta();
});

$.fn.procFiltDta = function() { 

	if($("input:radio[name='itmcat']").is(":checked")){
		 $("#catgry").val($('input[name = "itmcat"]:checked').val());
	}else{
		$("#catgry").val('');
	}   

    $("#curval").val('');

    var frmfildata = $("#itemresultsform").serializeArray();
    var URL = $("#itemresultsform").attr("action");
    $.post(URL,
    frmfildata,
    function(data, textStatus, jqXHR)
    {    
    	if(textStatus == 'success'){ 			    
    	var newcurvl = $($.parseHTML(data)).filter("#curnewval").val();
    	if(newcurvl == 'end' || typeof newcurvl == "undefined"){
    		$("#itemsresultsb").hide();
    		$("#itemlistdim").hide();
    	}else{
    		$("#itemlistdim").hide();
     	    $("#itemsresultsb").show();
    	}
    	$("#curval").val(newcurvl);
    	$('#cpnresults').html(data);
    	}else{
    		$("#itemlistdim").hide();
     	    $("#itemsresultsb").show();
    	}
    }).fail(function(jqXHR, textStatus, errorThrown) 
    {$("#itemlistdim").hide();
     $("#itemsresultsb").show();  });   
};
$(".nano").nanoScroller({ alwaysVisible: true, sliderMaxHeight: 80 });
function cleraprdsfltrs(){
    $('input[type=radio]').each(function (){
    	$(this).prop('checked', false);
});
}
