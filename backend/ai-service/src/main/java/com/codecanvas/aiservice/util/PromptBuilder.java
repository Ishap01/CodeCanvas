package com.codecanvas.aiservice.util;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildExplainPrompt(String code) {

        return """
You are an expert software engineer.

Explain the following code for another developer.

Your explanation should include:

1. Overall purpose
2. Class/function responsibility
3. Logic flow
4. Important methods
5. Time complexity (if applicable)
6. Suggestions for improvement

Respond in Markdown.

Code:
%s
""".formatted(code);
    }

    public static String buildSummaryPrompt(String code) {

        return """
                Summarize this source code in under 100 words.
                
                     Mention:
                     - purpose
                     - functionality
                     - technologies used
                
                     Use Markdown.
                
                     Code:
                %s
                """.formatted(code);
    }

    public static String buildTagPrompt(String code) {

        return """
                Generate 5 concise programming tags for this code.
                
                      Only return comma-separated tags.
                
                      Example:
                      java,spring-boot,jpa,authentication,jwt
                
                      Code:
                %s
                """.formatted(code);
    }

}