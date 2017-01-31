/**
 * Created by sarala on 18/01/2017.
 */

$( function() {
    $(".collapse").hide();

    $(".expand").click(function () {

        $expand = $(this);
        //getting the next element
        $collapse = $expand.next();
        //open up the content needed - toggle the slide- if visible, slide up, if not slidedown.
        $collapse.slideToggle(0, function () {
            //execute this after slideToggle is done
            //change text of header based on visibility of content div
            $shortdescription = $expand.find(".shortdescription");
                //change text based on condition
            $collapse.is(":visible") ? $shortdescription.hide() : $shortdescription.show();

        });
    });


} );
