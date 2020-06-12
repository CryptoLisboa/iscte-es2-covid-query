import java.io.*;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CovidQuery {

	private static Git git;
	private static Repository repo;

	public static void main(String[] args) throws Exception {

		String gitFilePath = "./ESII1920";
		String selectEnd = "</select>";
		
		String selectSubjectInit = "<label for=\"subject\">Choose subject: </label>\r\n" + 
				"  <select name=\"subject \" id=\"subject\">";
		
		String selectRegionInit = "  <label for=\"region\">Choose region: </label>\r\n" + 
				"  <select name=\"region \" id=\"region\">";
		
		String selectOperatorInit = "  <label for=\"operator\">Choose logical operators: </label>\r\n" + 
				"  <select name=\" operator \" id=\"operator\">";
		
		String selectRegionContent = "";
		
		String selectRegionFinalHTML = "";
		
		try {

			Git.cloneRepository()
			.setURI("https://github.com/vbasto-iscte/ESII1920")
			.setDirectory(new File(gitFilePath))
			.call();

			git = Git.open(new File(gitFilePath));
			repo = git.getRepository();

		} catch (Exception e) {
			//			git.clean();
			//			Git.cloneRepository()
			//			.setURI("https://github.com/vbasto-iscte/ESII1920")
			//			.setDirectory(new File("./ESII1920"))
			//			.call();
		}
		//		Ref tag = null;
		//		RevCommit testCommit = null;
		List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();

		String treeName = "refs/heads/master";
		RevCommit youngestCommit = null;
		Git git = new Git(repo);

		RevWalk walk = new RevWalk(git.getRepository());
		for(Ref branch : branches) {
			if(branch.getName().contains(treeName)) {
				RevCommit commit = walk.parseCommit(branch.getObjectId());
				if(youngestCommit == null || commit.getAuthorIdent().getWhen().compareTo(
						youngestCommit.getAuthorIdent().getWhen()) > 0)
					youngestCommit = commit;
				System.out.println(youngestCommit);
			}
		}
		ObjectId treeId = youngestCommit.getTree();
		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.reset(treeId);
		while (treeWalk.next()) {
			String path = treeWalk.getPathString();
			System.out.println("File path from git: " + path);
			// ...
		}
		treeWalk.close();

		git.clean();
		File file = new File(gitFilePath); 

		if(file.delete()) { 
			System.out.println("File deleted successfully"); 
		} 
		else{
			System.out.println("Failed to delete the file"); 
		} 

		try {	
			File inputFile = new File(gitFilePath + "\\covid19spreading.rdf");    	      	  
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();         

			String query = "/RDF/NamedIndividual/@*";
			System.out.println("Query para obter a lista das regiões: " + query);
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile(query);         
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				String regionName = StringUtils.substringAfter(nl.item(i).getNodeValue(), "#");
				System.out.println(regionName);
				if (selectRegionContent.length() == 0) {
					selectRegionContent = "<option value=\"" + regionName + "\">" + regionName + "</option>";
				} else {
					selectRegionContent += "/n" + "<option value=\"" + regionName + "\">" + regionName + "</option>";
				}
			}
			
			selectRegionFinalHTML = selectRegionInit + selectRegionContent + selectEnd;
			
			System.out.println(selectRegionFinalHTML);

			//Algarve

			query = "//*[contains(@about,'Algarve')]/Testes/text()";  
			System.out.println("Query para obter o número de testes feitos no Algarve: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Algarve')]/Infecoes/text()";
			System.out.println("Query para obter o número de infeções no Algarve: " + query);
			expr = xpath.compile(query);
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Algarve')]/Internamentos/text()";
			System.out.println("Query para obter o número de internamentos no Algarve: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			//Lisboa

			query = "//*[contains(@about,'Lisboa')]/Testes/text()";  
			System.out.println("Query para obter o número de testes feitos no Lisboa: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Lisboa')]/Infecoes/text()";
			System.out.println("Query para obter o número de infeções no Lisboa: " + query);
			expr = xpath.compile(query);
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Lisboa')]/Internamentos/text()";
			System.out.println("Query para obter o número de internamentos no Lisboa: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			//Centro

			query = "//*[contains(@about,'Centro')]/Testes/text()";  
			System.out.println("Query para obter o número de testes feitos no Centro: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Centro')]/Infecoes/text()";
			System.out.println("Query para obter o número de infeções no Centro: " + query);
			expr = xpath.compile(query);
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Centro')]/Internamentos/text()";
			System.out.println("Query para obter o número de internamentos no Centro: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			//Norte

			query = "//*[contains(@about,'Norte')]/Testes/text()";  
			System.out.println("Query para obter o número de testes feitos no Norte: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Norte')]/Infecoes/text()";
			System.out.println("Query para obter o número de infeções no Norte: " + query);
			expr = xpath.compile(query);
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Norte')]/Internamentos/text()";
			System.out.println("Query para obter o número de internamentos no Norte: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			//Alentejo

			query = "//*[contains(@about,'Alentejo')]/Testes/text()";  
			System.out.println("Query para obter o número de testes feitos no Alentejo: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Alentejo')]/Infecoes/text()";
			System.out.println("Query para obter o número de infeções no Alentejo: " + query);
			expr = xpath.compile(query);
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

			query = "//*[contains(@about,'Alentejo')]/Internamentos/text()";
			System.out.println("Query para obter o número de internamentos no Alentejo: " + query);
			expr = xpath.compile(query);     
			System.out.println(expr.evaluate(doc, XPathConstants.STRING));

		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
}