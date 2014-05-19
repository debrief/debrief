import com.icl.saxon.Context;
import com.icl.saxon.expr.Expression;
import com.icl.saxon.expr.StandaloneContext;
import com.icl.saxon.jdom.DocumentWrapper;
import com.icl.saxon.jdom.NodeWrapper;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.om.NodeEnumeration;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

/**
 * A simple example to show how SAXON can be used with a JDOM tree.
 * It is designed to be used with the source document books.xml and the
 * stylesheet total.xsl
 * @author Michael H. Kay
 */
public class JDOMExample {

    /**
     * Method main
     */
    public static void main(String argv[])
            throws TransformerException, TransformerConfigurationException,
                   JDOMException, IOException {

        if (argv.length != 2) {
            System.err.println("Usage: JDOMExample source.xml style.xsl >out.xml");
        } else {
            transform(argv[0], argv[1]);
        }

    }

    /**
     * Show the simplest possible transformation from system id
     * to output stream.
     */

    public static void transform(String sourceID, String xslID)
            throws TransformerException, TransformerConfigurationException,
                   JDOMException, IOException {


        // Build the JDOM document
        SAXBuilder builder = new SAXBuilder("com.icl.saxon.aelfred.SAXDriver");
        Document doc = builder.build(new File(sourceID));

        // Give it a Saxon wrapper
        DocumentWrapper docw = new DocumentWrapper(doc, sourceID);
        NamePool pool = NamePool.getDefaultNamePool();
        docw.setNamePool(pool);

        // Retrieve all the ITEM elements
        Expression exp = Expression.make("//ITEM", new StandaloneContext(pool));
        Context context = new Context();
        context.setContextNode(docw);
        context.setPosition(1);
        context.setLast(1);

        NodeEnumeration enm = exp.enumerate(context, false);

        // For each of these, compute an additional attribute

        while (enm.hasMoreElements()) {
            NodeWrapper node = (NodeWrapper)enm.nextElement();
            Element item = (Element)node.getNode();
            String price = item.getChildText("PRICE");
            String quantity = item.getChildText("QUANTITY");
            try {
                double priceval = Double.parseDouble(price);
                double quantityval = Double.parseDouble(quantity);
                double value = priceval * quantityval;
                item.setAttribute("VALUE", ""+value);
            } catch (NumberFormatException err) {
                item.setAttribute("VALUE", "?");
            }
        }

        // Now do a transformation

        System.setProperty("javax.xml.transform.TransformerFactory",
                           "com.icl.saxon.TransformerFactoryImpl");
        TransformerFactory tfactory = TransformerFactory.newInstance();

        Templates templates = tfactory.newTemplates(new StreamSource(xslID));
        Transformer transformer = templates.newTransformer();

        transformer.transform(docw, new StreamResult(System.out));

    }


}
