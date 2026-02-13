import DiagramProcessing.Comparer;
import ObjectTypes.BoxedObject;
import ObjectTypes.LineObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaCodeGenerator {

    private List<BoxedObject> classes;
    private List<BoxedObject> interfaces;
    private List<LineObject> lines;
    private Comparer comparer;

    public JavaCodeGenerator(List<BoxedObject> classes, List<BoxedObject> interfaces, List<LineObject> lines) {
        this.classes = classes;
        this.interfaces = interfaces;
        this.lines = lines;
        this.comparer = new Comparer();
    }

    public void generate(String outputDir) {
        new File(outputDir).mkdirs();

        for (BoxedObject cls : classes) {
            writeToFile(outputDir, cls.name + ".java", buildClassContent(cls));
        }
        for (BoxedObject iface : interfaces) {
            writeToFile(outputDir, iface.name + ".java", buildInterfaceContent(iface));
        }
    }

    // --- LOGIC FOR CLASSES ---
    private String buildClassContent(BoxedObject source) {
        StringBuilder sb = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        List<String> implementsList = new ArrayList<>();
        List<String> extendsList = new ArrayList<>();

        // 1. Scan ALL lines to sort them into "Implements", "Extends", or "Fields"
        for (LineObject line : lines) {
            if (comparer.isPointOnBox(source, line.getSourceX(), line.getSourceY())) {
                BoxedObject target = findTarget(line);

                if (target != null) {
                    // RULE 1: Class -> Interface = IMPLEMENTS
                    if (isInterface(target)) {
                        implementsList.add(target.name);
                    }
                    // RULE 2: Class -> Class = ASSOCIATION (Field)
                    // (Unless you add specific arrow-check logic for 'extends')
                    else {
                        String varName = target.name.substring(0, 1).toLowerCase() + target.name.substring(1);
                        fields.append("    private ").append(target.name).append(" ").append(varName).append(";\n");
                    }
                }
            }
        }

        // 2. Write Header
        sb.append("public class ").append(source.name);

        // Add EXTENDS (if any)
        if (!extendsList.isEmpty()) {
            sb.append(" extends ").append(String.join(", ", extendsList));
        }

        // Add IMPLEMENTS (The fix you asked for)
        if (!implementsList.isEmpty()) {
            sb.append(" implements ").append(String.join(", ", implementsList));
        }

        sb.append(" {\n\n");

        // 3. Write Fields (Associations)
        sb.append(fields);

        // 4. (Optional) Auto-generate stub methods for interfaces?
        // For now, we leave it empty as per your request.

        sb.append("}");
        return sb.toString();
    }

    // --- LOGIC FOR INTERFACES ---
    private String buildInterfaceContent(BoxedObject source) {
        StringBuilder sb = new StringBuilder();
        List<String> extendsList = new ArrayList<>();
        StringBuilder methods = new StringBuilder();

        for (LineObject line : lines) {
            if (comparer.isPointOnBox(source, line.getSourceX(), line.getSourceY())) {
                BoxedObject target = findTarget(line);
                if (target != null) {
                    // Interface -> Interface = EXTENDS
                    if (isInterface(target)) {
                        extendsList.add(target.name);
                    }
                    // Interface -> Class = Dependency (Method return type)
                    else {
                        methods.append("    ").append(target.name).append(" get").append(target.name).append("();\n");
                    }
                }
            }
        }

        sb.append("public interface ").append(source.name);

        if (!extendsList.isEmpty()) {
            sb.append(" extends ").append(String.join(", ", extendsList));
        }

        sb.append(" {\n\n");
        sb.append(methods);
        sb.append("}");
        return sb.toString();
    }

    // --- HELPERS ---

    private BoxedObject findTarget(LineObject line) {
        for (BoxedObject b : classes) {
            if (comparer.isPointOnBox(b, line.getTargetX(), line.getTargetY())) return b;
        }
        for (BoxedObject b : interfaces) {
            if (comparer.isPointOnBox(b, line.getTargetX(), line.getTargetY())) return b;
        }
        return null;
    }

    private boolean isInterface(BoxedObject obj) {
        return interfaces.contains(obj);
    }

    private void writeToFile(String dir, String name, String content) {
        try (FileWriter fw = new FileWriter(new File(dir, name))) {
            fw.write(content);
            System.out.println("Generated: " + name);
        } catch (IOException e) { e.printStackTrace(); }
    }
}