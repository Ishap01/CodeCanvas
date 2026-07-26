package com.codecanvas.aiservice.util;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildExplainPrompt(String code) {

        return """
                Explain the following source code in simple language.

                Code:
                %s
                """.formatted(code);
    }

    public static String buildSummaryPrompt(String code) {

        return """
                Summarize the following source code in 5-6 lines.

                Code:
                %s
                """.formatted(code);
    }

    public static String buildTagPrompt(String code) {

        return """
                Generate 5 relevant programming tags separated by commas.

                Code:
                %s
                """.formatted(code);
    }

}