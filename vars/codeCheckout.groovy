def call(String branch, String url, String credId) {
    // Validate branch name format
    if (!branch.matches('^[\\w.-]+$')) {
        error("Invalid branch name format")
    }
    
    // Validate URL format
    if (!url.matches('^https?://[\\w.-]+(/[\\w.-]+)*(\\.git)?$')) {
        error("Invalid git URL format")
    }
    
     try {
        timeout(time: 5, unit: 'MINUTES') {
            def gitConfig = [
                branch: branch,
                url: url,
                changelog: true,
                poll: false
            ]
            
            if (credentialsId) {
                gitConfig.credentialsId = credentialsId
            }
            
            git(gitConfig)
        }
     } catch (Exception e) {
        def errorMsg = "Git checkout failed:\n" +
                      "Branch: ${branch}\n" +
                      "URL: ${url}\n" +
                      "Error: ${e.message}"
        error(errorMsg)
     }

    
}