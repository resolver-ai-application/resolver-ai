package com.projects.resolver.llm.tools;

import com.projects.resolver.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;

    //todo: Modify description add-> restriction to LLM to not read same file again
    @Tool(name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.jsx'])")
            List<String> paths){
        List<String> result = new ArrayList<>();
        for(String path:paths){
            String cleanPath = path.startsWith("/") ? path.substring(1):path;
            log.info("Requested Path : {}",cleanPath);
            String content = projectFileService.getFileContent(projectId,cleanPath).content();
            result.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE ---",
                    cleanPath,content
            ));
        }
        return result;
    }
}

//
//import com.projects.resolver.service.ProjectFileService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.ai.tool.annotation.ToolParam;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@RequiredArgsConstructor
//public class CodeGenerationTools {
//
//    private final ProjectFileService projectFileService;
//    private final Long projectId;
//
//@Tool(
//        name = "read_files",
//        description =
//                """
//                          Read ONLY existing files from FILE_TREE.
//
//                          RULES:
//                          - Only request files already present in FILE_TREE.
//                          - NEVER request files that will be newly created.
//                          - If a file does not exist, DO NOT call this tool for it.
//                          - New files must be created directly using <file path="..."> tags.
//                          - Return file contents for existing files only.
//                          """)
//public List<String> readFiles(
//        @ToolParam(
//                description =
//                        """
//                                  List of existing relative file paths from FILE_TREE.
//                                  Example:
//                                  ['src/App.tsx', 'src/pages/Index.tsx']
//
//                                  NEVER include new files that do not already exist.
//                                  """)
//        List<String> paths) {
//
//    List<String> result = new ArrayList<>();
//
//    for (String path : paths) {
//        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
//        log.info("Requested file: {}", cleanPath);
//        try {
//            String content = projectFileService.getFileContent(projectId, cleanPath).content();
//            result.add(
//                    String.format(
//                            """
//                                            --- START OF FILE: %s ---
//                                            %s
//                                            --- END OF FILE ---
//                                            """,
//                            cleanPath, content));
//        } catch (Exception ex) {
//            log.warn("Skipped non-existing file read request: {}", cleanPath);
//            result.add(
//                    String.format(
//                            """
//                                            --- FILE NOT FOUND: %s ---
//                                            This file does not exist.
//                                            Create it directly using:
//                                            <file path="%s">...</file>
//                                            """,
//                            cleanPath, cleanPath));
//        }
//    }
//    return result;
//    }
//}
