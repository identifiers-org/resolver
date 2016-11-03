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
        render(view:"intro")
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
}
