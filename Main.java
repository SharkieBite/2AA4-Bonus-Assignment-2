import DiagramProcessing.EvaluateDiagram;
import DiagramProcessing.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Setup
        EvaluateDiagram parser = new EvaluateDiagram();

        Scanner inoutObject = new Scanner(System.in);

        System.out.print("Enter Absolute File Path: ");

        parser.parseXml(inoutObject.nextLine());

        // 3. Generate Code
        JavaCodeGenerator generator = new JavaCodeGenerator(
                parser.getClasses(),
                parser.getInterfaces(),
                parser.getLines()
        );

        // 4. Output to "src-gen" folder
        System.out.println("--- Starting Generation ---");
        generator.generate("src-gen");
        System.out.println("--- Done ---");
    }
}