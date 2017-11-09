/**
 * Created by sarala on 08/11/2017.
 */

/*var idorgrest = "http://localhost:8090/";*/
var idorgrest = "//identifiers.org/rest/";
var NoResultsLabel = "No results found.";
//var idorgrest = "//localhost:8090/";

$( function() {
    $("#resolve").keypress(function(e) {
        if(e.which == 13) {
            if(validateField($("#resolve").val())){
                $( "#progressbar" ).progressbar({
                    value: false
                });
                $.ajax({
                    url: idorgrest+"identifiers/validate/"+$("#resolve").val(),
                    success: function(result) {
                        validationMessage = result.url;
                        window.open(result.url,'_blank');
                        $("#validate-result").html("<a href=\""+validationMessage+"\">"+validationMessage+"</a>");
                        $("#validate-result").removeClass("invalid-input").addClass("valid-input");
                        $( "#progressbar" ).hide();
                    },
                    error: function (xhr, ajaxOptions, thrownError){

                        $("#validate-result").removeClass("valid-input").addClass("invalid-input");
                        jsonValue = jQuery.parseJSON(xhr.responseText);
                        $("#validate-result").html(jsonValue.message);
                        $( "#progressbar" ).hide();
                    }
                });
            }else {
                $("#validate-result").removeClass("valid-input").addClass("invalid-input");
                $("#validate-result").html("Please enter prefix:identifier.");
            }

        }
    });

    function validateField(inputValue){
        if(inputValue == ""){
            return false;
        }
        return true;
    }

    $(document).ready(function(){
        $.ajax({
            url: idorgrest+"collections/summary",
            success: function(result) {
                $('#collections').text(result.collections);
                $('#resources').text(result.resources);
                $('#modified').text(result.lastModifiedDate);
            },
            error: function (xhr, ajaxOptions, thrownError){
                $('#collections').text(NoResultsLabel);
                $('#resources').text(NoResultsLabel);
                $('#modified').text(NoResultsLabel);
            }
        });
    });

} );