<%--
  Created by IntelliJ IDEA.
  User: sarala
  Date: 23/08/2016
  Time: 09:58
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Identifiers.org | search</title>
</head>

<body>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<script type="text/javascript" src="${resource(dir: 'js', file: 'search.js')}"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>


%{--<script>
    $( function() {
        var availableTags = [
            "ActionScript",
            "AppleScript",
            "Asp",
            "BASIC",
            "C",
            "C++",
            "Clojure",
            "COBOL",
            "ColdFusion",
            "Erlang",
            "Fortran",
            "Groovy",
            "Haskell",
            "Java",
            "JavaScript",
            "Lisp",
            "Perl",
            "PHP",
            "Python",
            "Ruby",
            "Scala",
            "Scheme"
        ];
        $( "#tags" ).autocomplete({
            source: availableTags
        });
    } );
</script>--}%

<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: scroll;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
    height: 200px;
}
</style>

<script>
    $( function() {
        $( "#tags" ).autocomplete({
            minLength: 3,
            source: function(request, response){
                $.ajax({
                    /*type: "POST",*/
                    url: "${createLink(controller: 'restws',action: 'index')}",
                    data: request,
                    success: function (result) {
                        var resources = [];
                        for(var i=0;i < result.item.length; i++){
                            resources[i] = result.item[i].name + " " + result.item[i].uri;
                        }
                        response(resources);
                    },
                    error: function (msg) {
                        alert(msg.status + ' ' + msg.statusText);
                    }
                })
            }/*,

            select: function (event, ui) {
                $("#tags").val(ui.item.tags);
                return false;
            }*/
        });
    } );
</script>



<div class="ui-widget">
    <label for="tags">Available resources: </label>
    <input id="tags">
</div>



</body>
</html>