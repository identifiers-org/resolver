/**
 * Created by sarala on 18/01/2017.
 */

$( function() {
    $(".collapse").hide();

    $(".expandicon").click(function () {

        $expandicon = $(this);
        $expand = $(this).parent();
        //getting the next element
        $collapse = $expand.next();
        //open up the content needed - toggle the slide- if visible, slide up, if not slidedown.
        $collapse.slideToggle(0, function () {
            //execute this after slideToggle is done
            //change text of header based on visibility of content div
            $shortdescription = $expand.find(".shortdescription");
                //change text based on condition
            if($collapse.is(":visible")){
                $shortdescription.hide();
                $expandicon.attr('data-icon','w');

            }else {
                $shortdescription.show();
                $expandicon.attr('data-icon','u');
            }
        });
    });


} );
