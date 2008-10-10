/**
 * @author Camille Laibe
 * @version 20060509
 * @copyright EMBL-EBI, Computational Neurobiology Group
 * 
 * to test the web services via a simple program
 */


import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.encoding.XMLType;


/*
 * Java client for testing Miriam Web Services 
 */
public class MiriamWSJavaClient
{
	public static void main(String arg[])
	{
		System.out.println("Hi!");
		String param1 = "http://www.geneontology.org/";   // http://www.geneontology.org/, gene ontology, urn:lsid:uniprot.org go
		//String param2 = "01234567";
		
		System.out.println("param1: " + param1);
	
		try
		{
		    // address to access to the service
		    String endpoint = "http://grolsch.ebi.ac.uk:8080/axis/services/MiriamWebServices";
		    
		    // Call object represents the dynamic call of the service
		    ServiceFactory serviceFactory = ServiceFactory.newInstance();
		    Service service = serviceFactory.createService(new QName("MiriamWebServices"));
		    Call    call    = (Call) service.createCall();
		    
		    // access point for the call
		    call.setTargetEndpointAddress(endpoint);
		    
		    // name of the method called
		    call.setOperationName(new QName("getNames"));
		    
		    // return type
		    call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
		    //call.addParameter("param2", XMLType.XSD_STRING, ParameterMode.IN);
		    call.setReturnType(XMLType.SOAP_ARRAY);   // XSD_STRING, SOAP_ARRAY   XMLType.SOAP_ARRAY
		    
		    // at least, the invocation
		    //String ret = (String) call.invoke( new Object[] { } );
		    System.out.print("invok...");
		    //String ret = (String) call.invoke( new Object[] { new String(param1), new  String(param2) } );
		    //String ret = (String) call.invoke( new Object[] { new String(param1) } );
		    String[] ret = (String[]) call.invoke( new Object[] { new String(param1) } );
		    System.out.println(" done.");
		    
		    System.out.println("Requete : " + call.getOperationName() + "()");
		    
		    if (ret != null)
		    {
		    	/**/
			    for (int i=0; i<ret.length; ++i)
			    {
			    	System.out.println("Reponse (" + i + "): " + ret[i]);	
			    }
			    /*
			    System.out.println("Response: >" + ret + "<");
			    */
		    }
		    else
		    {
		    	System.out.println("No answer!");
		    }
		    System.out.println("This is the end.");
		    
		}
		catch (Exception e)
		{
		    System.err.println(e.toString());
		}

	}

}
