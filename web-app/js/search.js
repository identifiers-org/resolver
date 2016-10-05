/**
 * Created by sarala on 23/08/2016.
 */

//var idorgrest = "http://localhost:8090/miriamws/rest/resources/";
var idorgrest = "http://www.ebi.ac.uk/miriamws/main/rest/resources/";

$( function() {
    $( "#resources" ).autocomplete({
            source: function(request, response){
                $.ajax({
                    type: "GET",
                    url: idorgrest+request.term,
                    success: function (result) {
                        if(result.item.constructor==Array) {
                            response(result.item);
                        }else{
                            response(new Array(result.item))
                        }
                    },
                    error: function (msg) {
                        console.log(msg.status + ' ' + msg.statusText);
                    }
                })
            },
            select: function (event, ui) {
                $("#resources").val(ui.item.uri);
                window.open(ui.item.uri,'_blank');
                return false;
            }
        })
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
        console.log(item);
        return $( "<li>" )
            .append( "<div><b>" + item['@prefix'] + "</b>&nbsp;&nbsp;" + item.uri + "</div>" )
            .appendTo( ul );
    };

    $("#validate").click(function(){
        if(validateField($("#resIdent").val())){
            $.ajax({
                url: idorgrest+"validate/"+$("#resIdent").val(),
                success: function(result) {
                    var validationMessage = "Invalid prefix:identifier. Unable to find an identifiers.org URI.";
                    if (result != null ) {
                        validationMessage = result.item.uri;
                        $("#validate-result").removeClass("invalid-input").addClass("valid-input");
                    }
                    else {
                        $("#validate-result").removeClass("valid-input").addClass("invalid-input");
                    }
                    $("#validate-result").html(validationMessage);
                }
            });
        }else {
            $("#validate-result").removeClass("valid-input").addClass("invalid-input");
            $("#validate-result").html("Please enter prefix:identifier.");
        }

    });

    $( "#databases" ).autocomplete({
            source: function(request, response){
                $.ajax({
                    type: "GET",
                    url: idorgrest+"identifier/"+request.term,
                    success: function (result) {
                        if(result.item.constructor==Array) {
                            response(result.item);
                        }else{
                            response(new Array(result.item))
                        }
                    },
                    error: function (msg) {
                        console.log(msg.status + ' ' + msg.statusText);
                    }
                })
            },
            select: function (event, ui) {
                $("#databases").val(ui.item.uri);
                window.open(ui.item.uri,'_blank');
                return false;
            }
        })
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
        return $( "<li>" )
            .append( "<div><b>" + item['@prefix'] + "</b>&nbsp;&nbsp;" + item.uri + "</div>" )
            .appendTo( ul );
    };

    function validateField(inputValue){
        if(inputValue == ""){
            return false;
        }
        return true;
    }

} );
