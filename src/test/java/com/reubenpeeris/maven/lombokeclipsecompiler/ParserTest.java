package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.util.Collection;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//https://github.com/jenkinsci/warnings-plugin
@Ignore
public class ParserTest {

    private static final String input = "----------\n"
            + "1. WARNING in /home/reuben/workspace/lombok-compiler/ta/src/main/java/com/reubenpeeris/test/testapp/App.java (at line 19)\n"
            + "    private void unused() {\n" + "                 ^^^^^^^^\n"
            + "The method unused() from the type App is never used locally\n"
            + "----------\n";

    private static final String input2 = "multiple Class-Path headers in manifest of jar file: /home/reuben/.m2/repository/com/sun/xml/ws/webservices-rt/2.0.1/webservices-rt-2.0.1.jar\n"
            + "multiple Class-Path headers in manifest of jar file: /home/reuben/.m2/repository/javax/xml/webservices-api/2.0.1/webservices-api-2.0.1.jar\n"
            + "----------\n"
            + "1. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/NuBasePage.java (at line 24)\n"

			// Actual format uses leading tab
			+ "\tpublic class NuBasePage extends WebPage {\n"
			+ "\t             ^^^^^^^^^^\n"

			+ "The serializable class NuBasePage does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "----------\n"
			+ "2. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/VanillaLocalDateFieldPicker.java (at line 13)\n"
			+ "        public class VanillaLocalDateFieldPicker extends FormComponentPanel<LocalDate> {\n"
			+ "                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class VanillaLocalDateFieldPicker does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "3. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/VanillaLocalDateFieldPicker.java (at line 21)\n"
			+ "        IModel<Date> model = new IModel<Date>() {\n"
			+ "                                 ^^^^^^^^^^^^^^\n"
			+ "The serializable class  does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "4. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/VanillaLocalDateFieldPicker.java (at line 42)\n"
			+ "        DatePicker datePicker = new DatePicker() {\n"
			+ "                                    ^^^^^^^^^^^^\n"
			+ "The serializable class  does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "5. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/VanillaLocalDateFieldPicker.java (at line 54)\n"
			+ "        dateTextField.add(new OnChangeAjaxBehavior() {\n"
			+ "                              ^^^^^^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class  does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "----------\n"
			+ "6. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/roleadmin/RoleManagementDTO.java (at line 21)\n"
			+ "        public class RoleManagementDTO implements Serializable {\n"
			+ "                     ^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class RoleManagementDTO does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "----------\n"
			+ "7. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/security/SAMLSecurityContextAdapter.java (at line 11)\n"
			+ "        public class SAMLSecurityContextAdapter implements NuSecurityContext {\n"
			+ "                     ^^^^^^^^^^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class SAMLSecurityContextAdapter does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "----------\n"
			+ "8. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/ui/LocalDateTimeLabel.java (at line 6)\n"
			+ "        public class LocalDateTimeLabel extends Label {\n"
			+ "                     ^^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class LocalDateTimeLabel does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n"
			+ "----------\n"
			+ "9. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/wssecurity/NuLocator.java (at line 58)\n"
			+ "        InputStream inputStream = null;\n"
			+ "                    ^^^^^^^^^^^\n"
			+ "Resource 'inputStream' should be managed by try-with-resource\n"
			+ "----------\n"
			+ "10. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/wssecurity/NuLocator.java (at line 125)\n"
			+ "        FileOutputStream fos = new FileOutputStream(trustStore);\n"
			+ "                         ^^^\n"
			+ "Potential resource leak: 'fos' may not be closed\n"
			+ "----------\n"
			+ "----------\n"
			+ "11. WARNING in /home/reuben/workspace/nucleus-nuclear/nuclear-web-core/src/main/java/com/nucleusfinancial/nuclear/service/util/WicketOutputStreamer.java (at line 34)\n"
			+ "        ResourceStreamResource resource = new ResourceStreamResource(new AbstractResourceStreamWriter() {\n"
			+ "                                                                         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n"
			+ "The serializable class  does not declare a static final serialVersionUID field of type long\n"
			+ "----------\n" + "11 problems (11 warnings)\n";

    @Test
    public void test() throws Exception {
        Collection<CompilerMessage> messages = Parser.parse(input);
    }

    @Test
    public void test2() throws Exception {
        Collection<CompilerMessage> messages = Parser.parse(input2);
        assertEquals(11, messages.size());
    }

    @Test
    public void test3() throws Exception {
        List<CompilerMessage> messages = Parser
                .parse("2. WARNING in /home/reuben/workspace/github/test-project/src/main/java/TestClass.java (at line 4)\n"
                        + "    private String unusedStringField;\n"
                        + "                   ^^^^^^^^^^^^^^^^^\n"
                        + "The value of the field TestClass.unusedStringField is not used\n");
        assertEquals(20, messages.get(0).getStartColumn());
        assertEquals(36, messages.get(0).getEndColumn());
    }

    @Test
    public void test4() throws Exception {
        List<CompilerMessage> messages = Parser
                .parse("1. WARNING in /home/reuben/workspace/github/test-project/src/main/java/TestClass.java (at line 3)\n"
                        +"    public class TestClass implements Serializable {\n"
                        +"                 ^^^^^^^^^\n"
                        +"The serializable class TestClass does not declare a static final serialVersionUID field of type long\n");
        assertEquals(18, messages.get(0).getStartColumn());
        assertEquals(26, messages.get(0).getEndColumn());
    }

    @Test
    public void test5() {
        CompilerMessage parseMessage =
                Parser.parseMessage("1. ERROR in /home/reuben/workspace/nucleus-nuclear/nuclear-core/src/test/java/com/nucleusfinancial/nuclear/dao/entity/NuEntityTest.java (at line 9)\n"
                        + "        private static class TestNuEntity extends NuEntity {\n"
                        + "                             ^^^^^^^^^^^^\n"
                        + "The serializable class TestNuEntity does not declare a static final serialVersionUID field of type long\n");
        parseMessage.toString();
    }
}
