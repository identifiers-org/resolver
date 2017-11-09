package uk.ac.ebi.miriam.resolver


/**
 * Controller which handles all info pages.
 *
 * Camille Laibe
 * 20110811
 */
class GeneralController
{
    static layout = 'main'   // Sitemesh view layouts template
    

    def intro = {
       // render(view:"intro")
        render(view:"home")
    }

    def news = {
        render(view:"news")
    }

    def help = {
        render(view:"help")
    }

    def documentation = {
        render(view:"documentation")
    }

    def about = {
        render(view:"about")
    }

    def requestprefix = {
        redirect(url: "https://docs.google.com/forms/d/e/1FAIpQLSfrvItfMGrBrau-hmSe7l2-Fp7c1j953UUcmhdKb0Q9HUuL8w/viewform")
    }
}
