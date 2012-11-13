package uk.ac.ebi.miriam.resolver


/**
 * Controller which handles all info pages.
 *
 * Camille Laibe
 * 20110811
 */
class InfoController
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

    def examples = {
        render(view:"examples")
    }

    def about = {
        render(view:"about")
    }
}
