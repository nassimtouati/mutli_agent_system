package dsc;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

	private static final String DOMAINS_FILE = "dom.txt";
    private static final String VARIABLES_FILE = "var.txt";
    private static final String CONSTRAINTS_FILE = "ctr.txt";
    private static final String OUTPUT_FILE = "4.xml";

    public static void main(String[] args) {
        int agentsNumber = 4;
        try {
            Element instance = new Element("instance");
            Element presentation = createPresentation();
            Element agents = defineAgents(agentsNumber);
            Element domains = defineDomains();
            Element variables = defineVariables(agentsNumber);
            Element constraints = defineConstraints();

            Document document = new Document(instance);
            document.getRootElement().addContent(presentation);
            document.getRootElement().addContent(agents);
            document.getRootElement().addContent(domains);
            document.getRootElement().addContent(variables);
            document.getRootElement().addContent(constraints);

            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileOutputStream(new File(OUTPUT_FILE)));
//            xmlOutput.output(document, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Element defineConstraints() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(CONSTRAINTS_FILE), Charset.defaultCharset());
        Element constraints = new Element("constraints");
        constraints.setAttribute(new Attribute("nbConstraints", String.valueOf(lines.size())));
        int i = 0;
        for (String line : lines) {
            String[] elements = line.trim().split("[\\s]+");
            Element constraint = new Element("constraint");
            constraint.setAttribute(new Attribute("name", "C" + (1 + i++)));
            constraint.setAttribute(new Attribute("scope", "X" + elements[0] + " X" + elements[1]));
            constraint.setAttribute(new Attribute("arity", "2"));
            String operator = "eq";
            String value = "";
            if (elements[3].equals(">")) {
                operator = "gt";
                value = elements[4];
            }
            constraint.setAttribute(new Attribute("reference", operator));
            Element param = new Element("parameters");
            param.setText("X" + elements[0] + " X" + elements[1] + " " + value);
            constraint.addContent(param);
            constraints.addContent(constraint);
        }
        return constraints;
    }

    private static Element defineVariables(int agentsNumber) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(VARIABLES_FILE), Charset.defaultCharset());
        Element variables = new Element("variables");
        variables.setAttribute(new Attribute("nbVariables", String.valueOf(agentsNumber)));
        for (String line : lines) {
            String[] elements = line.trim().split("[\\s]+");
            Element variable = new Element("variable");
            variable.setAttribute(new Attribute("name", "X" + elements[0]));//why X
            variable.setAttribute(new Attribute("domain", elements[1]));
            variable.setAttribute(new Attribute("agent", "a" + elements[0]));
            variables.addContent(variable);
        }
        return variables;
    }

    private static Element defineDomains() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(DOMAINS_FILE), Charset.defaultCharset());
        lines.remove(0);//removes dummy domain
        Element domains = new Element("domains");
        domains.setAttribute(new Attribute("nbDomains", String.valueOf(lines.size())));
        for (String line : lines) {
            String[] elements = line.trim().split("[\\s]+", 3);
            Element domain = new Element("domain");
            domain.setAttribute(new Attribute("name", elements[0]));
            domain.setAttribute(new Attribute("nbValues", elements[1]));
            domain.setText(elements[2]);
            domains.addContent(domain);
        }
        return domains;
    }

    private static Element defineAgents(int agentsNumber) {
        //TODO: define agents number (optimization)
        Element agents = new Element("agents");
        agents.setAttribute(new Attribute("nbAgents", String.valueOf(agentsNumber)));
        for (int i = 0; i < agentsNumber; i++) {
            Element agent = new Element("agent");
            agent.setAttribute(new Attribute("name", "a" + (i + 1)));
            agents.addContent(agent);
        }
        return agents;
    }

    private static Element createPresentation() {
        Element presentation = new Element("presentation");
        presentation.setAttribute("name", "Celar");
        presentation.setAttribute("format", "XCSP 2.1_FRODO");
        presentation.setAttribute("maxConstraintArity", "2");
        presentation.setAttribute("maximize", "false");
        //TODO: understand and add stats tags
        return presentation;
    }
}