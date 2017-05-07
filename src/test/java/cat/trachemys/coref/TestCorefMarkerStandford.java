/**
 * 
 */
package cat.trachemys.coref;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cat.trachemys.coref.CorefererCommons.CorefDocs;

/**
 * Test class for {@link cat.trachemys.coref.CorefMarkerStandford)}.
 * 
 * @author cristinae
 * @since 08.05.2017
*/
public class TestCorefMarkerStandford {

	private Coreferer cf;

	private final String text = "Barack Obama was born in Honolulu and his wife in Chicago. \n" 
            + "He is the president. She is a lawer.";

	private final String expectedTokSentence1 = 
			"Barack Obama was born in Honolulu and his wife in Chicago . ";
	private final String expectedTokSentence2 = 
			"He is the president . She is a lawer . ";

	private final String expectedJSON = "{\"1\":[{\"restChain\":\" <his> <he>\","
			+ "\"start\":1,\"isHead\":true,\"end\":2,\"tokens\":\"Barack Obama\","
			+ "\"type\":\"PROPER\"},{\"restChain\":\"<Barack Obama>  <he>\","
			+ "\"start\":8,\"isHead\":false,\"end\":8,\"tokens\":\"his\","
			+ "\"type\":\"PRONOMINAL\"}],\"2\":{\"restChain\":\"<Barack Obama> <his> \","
			+ "\"start\":1,\"isHead\":false,\"end\":1,\"tokens\":\"He\",\"type\":\"PRONOMINAL\"}}";

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		CorefererFactory corefererFactory = new CorefererFactory();				
		cf = corefererFactory.loadCoreferer("en");
	}

	/**
	 * 
	 */
	@After
	public void tearDown() throws Exception {		
	}

	/**
	 * Test method for {@link cat.trachemys.coref.CorefMarkerStandford#annotateText(java.lang.String)}.
	 */
	@Test
	public final void testAnnotateText() {
		CorefDocs cd = cf.annotateText(text);
		
		//Tokenised input (non-relevant)
		Assert.assertEquals(expectedTokSentence1, cd.tokSentences.get(0));
		Assert.assertEquals(expectedTokSentence2, cd.tokSentences.get(1));
		// Output of the coreference resolutor
		Assert.assertEquals(expectedJSON, cd.doc.toString());
	}

}
