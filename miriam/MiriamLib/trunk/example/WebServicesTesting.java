/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


import uk.ac.ebi.miriam.lib.*;


/**
 * Sample program in order to show the use of MiriamLib for accessing Web Services
 * 
 * @author Camille Laibe
 * @version 20060807
 * 
 */
public class WebServicesTesting
{

	/**
	 * Sample testing program
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		System.out.println("> Miriam Web Services Library Testing...");
		
		MiriamLink link = new MiriamLink();
		
		// sets the Web Services address
		ws.setAddress("http://web62-node2:9951/miriamws-alpha/MiriamWebServices");
		
		System.out.println("Test 0:");
		System.out.print("Is 'http://www.ebi.ac.uk/IntEnz/' deprecated? ");
		System.out.println(link.isDeprecated("http://www.ebi.ac.uk/IntEnz/"));
		System.out.println("Official URI: " + link.getOfficialURI("http://www.ebi.ac.uk/IntEnz/"));
		
		System.out.println("Test 1:");
		System.out.print("Is 'http://www.who.int/classifications/icd/' deprecated? ");
		System.out.println(link.isDeprecated("http://www.who.int/classifications/icd/"));
		System.out.println("Official URI: " + link.getOfficialURI("http://www.who.int/classifications/icd/"));
		
		System.out.println("> End of the test.");
	}

}
