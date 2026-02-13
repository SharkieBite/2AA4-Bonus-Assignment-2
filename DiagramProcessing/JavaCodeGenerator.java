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

    private String buildClassContent(BoxedObject source) {
        StringBuilder sb = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        List<String> implementsList = new ArrayList<>();
        List<String> extendsList = new ArrayList<>();

        for (LineObject line : lines) {
            if (comparer.isPointOnBox(source, line.getSourceX(), line.getSourceY())) {
                BoxedObject target = findTarget(line);

                if (target != null) {
                    if (isInterface(target)) {
                        implementsList.add(target.name);
                    }

                    else {
                        String varName = target.name.substring(0, 1).toLowerCase() + target.name.substring(1);
                        fields.append("    private ").append(target.name).append(" ").append(varName).append(";\n");
                    }
                }
            }
        }

        sb.append("public class ").append(source.name);

        if (!extendsList.isEmpty()) {
            sb.append(" extends ").append(String.join(", ", extendsList));
        }

        if (!implementsList.isEmpty()) {
            sb.append(" implements ").append(String.join(", ", implementsList));
        }

        sb.append(" {\n\n");

        sb.append(fields);

        sb.append("}");
        return sb.toString();
    }

    private String buildInterfaceContent(BoxedObject source) {
        StringBuilder sb = new StringBuilder();
        List<String> extendsList = new ArrayList<>();
        StringBuilder methods = new StringBuilder();

        for (LineObject line : lines) {
            if (comparer.isPointOnBox(source, line.getSourceX(), line.getSourceY())) {
                BoxedObject target = findTarget(line);
                if (target != null) {
                    if (isInterface(target)) {
                        extendsList.add(target.name);
                    }
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