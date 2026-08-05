package com.codecanvas.aiservice.util;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildExplainPrompt(String code) {

        return """
You are an expert software engineer and technical mentor.

You are given one or more source files that belong to the same software project.

IMPORTANT:
- Treat all files as one codebase.
- Understand the relationship between files before answering.
- Do not explain each file independently unless necessary.
- Explain how the components work together.

Your explanation should include:

## 1. Project Purpose
Explain what the overall project or feature is trying to achieve.

## 2. Architecture
Describe how the files interact with each other.

## 3. Responsibilities
Explain the responsibility of each important class or file.

## 4. Execution Flow
Describe the flow of execution from start to finish.

## 5. Important Logic
Highlight important methods, algorithms, validations or business logic.

## 6. Technologies Used
Mention frameworks, libraries or design patterns if applicable.

## 7. Complexity
Mention time/space complexity only where relevant.

## 8. Suggestions
Suggest possible improvements, optimizations or best practices.

Format your answer using Markdown with proper headings and bullet points.

Project Source Code:

%s
""".formatted(code);
    }

    public static String buildSummaryPrompt(String code) {

        return """
You are an experienced software engineer.

You are given one or more source files belonging to the same software project.

Treat all files as a single codebase.

Write a concise developer-friendly summary.

Rules:
- Keep the response between 100 and 150 words.
- Use Markdown.
- Use exactly these headings:

## Overview
## Key Features
## Technologies

Overview:
- Explain what the project does in 2–3 sentences.

Key Features:
- List 3–5 concise bullet points describing the main functionality.

Technologies:
- List the primary languages, frameworks and important technologies used.

Do not explain every file individually.
Do not include code.
Do not mention implementation details unless important.

Project Source Code:

%s
""".formatted(code);

    }

    public static String buildTagPrompt(String code) {

        return """
You are analyzing one or more source files from the same software project.

Treat all files as one project.

Generate  8 to 10 highly relevant programming tags.

Rules:
- Only programming-related tags.
- Return 8–10 unique tags whenever possible.
- Prefer technologies, frameworks, languages, architecture or concepts.
- Avoid generic words like "code", "project", "program".
- Return ONLY comma-separated tags.
- Do not number the tags.
- Do not add explanations.

Example:

java,spring-boot,jwt,authentication,microservices

Project Source Code:

%s
""".formatted(code);
    }

}