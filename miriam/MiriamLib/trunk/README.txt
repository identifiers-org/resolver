MIRIAM Web Services Library


VERSIONS:
  13 April 2008 (current version)
  25 June 2007 (old URIs)
  31 July 2006 (Public Beta)


INFORMATION:
  This Java library provides an easy access to the MIRIAM Web Services.
  For more information: http://www.ebi.ac.uk/miriam/


BUILD:
  An Ant file is provided (build.xml) to compile the library (into .jar files) or generate the JavaDoc.


DEPENDENCIES:
  This software uses (with many thanks to these projects) some external libraries:
  axis, commons-discovery, commons-logging, jaxrpc, log4j, saaj, wsdl4j, activation and mail


AUTHOR:
  The original source code contained in this repository was initially developed by (and still maintained by): Camille Laibe <camille.laibe@ebi.ac.uk>, for the Computational Neurobiology Group, at the European Bioinformatics Institute (Cambridge, UK)


COPYRIGHT:
  This work is distributed under the terms of the GNU General Public License.
  See COPYING.txt for more information.


HOWTO USE THE LIBRARY (sample program):
  import uk.ac.ebi.miriam.lib.*;

  public class WebServicesTesting
  {
    public static void main(String[] args)
    {
      // creation of the link to the web services
      MiriamLink link = new MiriamLink();

      // changes the default address
      ws.setAddress("http://www.ebi.ac.uk/compneur-srv/miriam-main/services/MiriamWebServices");

      System.out.println("Test 0:");
      System.out.print("Is 'http://www.ebi.ac.uk/IntEnz/' deprecated? ");
      System.out.println(link.isDeprecated("http://www.ebi.ac.uk/IntEnz/"));
      System.out.println("Official URI: " + link.getOfficialURI("http://www.ebi.ac.uk/IntEnz/"));

      System.out.println("Test 1:");
      System.out.print("Is 'http://www.who.int/classifications/icd/' deprecated? ");
      System.out.println(link.isDeprecated("http://www.who.int/classifications/icd/"));
      System.out.println("Official URI: " + link.getOfficialURI("http://www.who.int/classifications/icd/"));
    }
  }
