package org.uw.nwgc.multiomics.api;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Method;

public class GenerateTSFromEnum {
    public static void main(String[] args) throws Exception {
        // Fully-qualified enum class name passed via args[0]
        if (args.length < 4) {
            System.err.println("Usage: java GenerateTSFromEnum <fully-qualified-enum-class> <output-path> <ts-const-name> <ts-type-name>");
            System.exit(1);
        }

        String enumClassName = args[0];
        String outputPath = args[1];
        String constName = args[2];
        String typeName = args[3];

        Class<?> enumClass = Class.forName(enumClassName);
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("Provided class is not an enum: " + enumClassName);
        }

        Method getApiValueMethod = enumClass.getMethod("getApiValue");

        StringBuilder ts = new StringBuilder();
        ts
                .append("export const ")
                .append(constName)
                .append(" = {\n");

        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object constant : enumConstants) {
            String enumKey = constant.toString(); // Enum constant name
            String apiValue = (String) getApiValueMethod.invoke(constant);
            ts.append(String.format("    %s: '%s',\n", enumKey, apiValue));
        }

        ts
                .append("} as const;\n\n")
                .append("export type ")
                .append(typeName)
                .append(" = typeof ")
                .append(constName)
                .append("[keyof typeof ")
                .append(constName)
                .append("];");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(ts.toString());
        }

        System.out.println("Generated TypeScript " + constName + " at: " + outputPath);
    }
}
