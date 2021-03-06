/**
 * Created by sarala on 23/08/2016.
 */

//var idorgrest = "http://localhost:8090/miriamws/rest/resources/";
var idorgrest = "//identifiers.org/rest/";
var NoResultsLabel = "No results found.";
//var idorgrest = "//localhost:8090/";

$( function() {
    $( "#resources" ).autocomplete({
            source: function(request, response){
                $.ajax({
                    type: "GET",
                    url: idorgrest+"collections/name/"+request.term,
                    success: function (result) {
                        response(result)
                    },
                    error: function (msg) {
                        result = [NoResultsLabel];
                        response(result);
                        console.log(msg.status + ' ' + msg.statusText);
                    }
                })
            },
            select: function (event, ui) {
                if (ui.item.label === NoResultsLabel) {
                    event.preventDefault();
                }else{
                    $("#resources").val(ui.item.url);
                    window.open(ui.item.url,'_blank');
                    return false;
                }
            }
        })
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
        if (item.label === NoResultsLabel) {
            return $("<li>")
                .append("<div>" + item.label + "</div>")
                .appendTo(ul);
        }else {
            return $("<li>")
                .append("<div><b>" + item.prefix + "</b>&nbsp;&nbsp;" + item.url + "</div>")
                .appendTo(ul);
        }
    };

    $( "#showall" ).click(function() {
        window.open(idorgrest+"collections/", '_blank');
    });



    $( "#providercodes" ).autocomplete({
        source: function(request, response){
            $.ajax({
                type: "GET",
                url: idorgrest+"collections/provider/"+request.term,
                success: function (result) {
                    response(result)
                },
                error: function (msg) {
                    result = [NoResultsLabel];
                    response(result);
                    console.log(msg.status + ' ' + msg.statusText);
                }
            })
        },
        select: function (event, ui) {
            if (ui.item.label === NoResultsLabel) {
                event.preventDefault();
            }else{
                $("#providercodes").val(ui.item.resourcePrefix);
                return false;
            }
        }
    })
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
        if (item.label === NoResultsLabel) {
            return $("<li>")
                .append("<div>" + item.label + "</div>")
                .appendTo(ul);
        }else {
            return $("<li>")
                .append("<div><b>" + item.resourcePrefix + "</b>&nbsp;&nbsp;" + item.info + "</div>")
                .appendTo(ul);
        }
    };


    $("#resIdent").keypress(function(e) {
        if(e.which == 13) {
            if(validateField($("#resIdent").val())){
                $( "#progressbar" ).progressbar({
                    value: false
                });
                $.ajax({
                    url: idorgrest+"identifiers/validate/"+$("#resIdent").val(),
                    success: function(result) {
                        validationMessage = result.url;
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

} );
