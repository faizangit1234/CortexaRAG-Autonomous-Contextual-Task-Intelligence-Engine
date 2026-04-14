package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Client.LlmClient;
import com.faizan.taskmanager.Client.OllamaClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final LlmClient llmClient;

    public AiService(LlmClient llmClient){
        this.llmClient= llmClient;
    }

    public String cleanResponse(String response) {

        if (response == null) {
            return "";
        }

        String cleaned = response;

        // 1. Normalize whitespace
        cleaned = cleaned
                .replaceAll("\\r\\n|\\r|\\n", " ")
                .replaceAll("\\s+", " ");

        // 2. Remove common AI prefixes (STRICT, NON-GREEDY)
        cleaned = cleaned.replaceAll(
                "(?i)^(sure|okay|alright)[!.,\\s-]*(here( is|'s)?( the)?( task)?( description)?[:\\-\\s]*)?",
                ""
        );

        // 3. Remove "task:" or "description:" at start
        cleaned = cleaned.replaceAll(
                "(?i)^(task|description|task description)[:\\-\\s]*",
                ""
        );

        // 4. Remove quotes wrapping entire response
        cleaned = cleaned.replaceAll("^\"|\"$", "");

        // 5. Remove trailing explanations (VERY IMPORTANT)
        cleaned = cleaned.replaceAll(
                "(?i)(\\.|!)\\s*(let me know|hope this helps|feel free to).*",
                "$1"
        );

        cleaned = cleaned.replaceAll("(?i)sure.*?:", "")
                .replaceAll("(?i)a task description following.*?:", "");

        // 6. Hard trim again
        cleaned = cleaned.trim();

        // 7. Enforce max length (optional but production useful)
        if (cleaned.length() > 500) {
            cleaned = cleaned.substring(0, 500);
        }

        return cleaned;
    }

    public String generateTaskDescription(String title){
        String prompt = """
                         Generate a task description.

                         STRICT RULES:
                         - Only 1 sentence
                         - No introduction
                         - No explanations
                         - No extra words
                         - Output must start directly with the description

                         Task: %s
                         """.formatted(title);
        String raw = llmClient.generate(prompt);
        System.out.println("AI RAW RESPONSE : "+ raw);

        return cleanResponse(raw);
    }

    private String buildRagPrompt(String query, String context) {
        return """
    You MUST select the most relevant task from the context.

    RULES:
    - Output ONLY the exact task description.
    - Do NOT explain.
    - Do NOT add extra words.
    - Do NOT rephrase.

    If no match exists, output EXACTLY:
    INSUFFICIENT_CONTEXT

    ---------------------
    CONTEXT:
    %s
    ---------------------

    QUESTION:
    %s

    OUTPUT:
    """.formatted(context, query);
    }

    public String generateWithContext(String query, String context){
        String prompt = buildRagPrompt(query, context);
        // 🔥 DEBUG (VERY IMPORTANT)
        System.out.println("=== FINAL PROMPT SENT TO AI ===");
        System.out.println(prompt);
        String response = llmClient.generate(prompt);

        System.out.println("=== AI RESPONSE ===");
        System.out.println(response);

        return response;
    }
}
