def call(String imageName){
    if (!imageName.matches('^[a-zA-Z0-9][a-zA-Z0-9_.-]*$')) {
       error('Invalid image name. Must contain only alphanumeric characters, dots, dashes, and underscores')
    }

    try {
        sh "docker info > /dev/null 2>&1"
        sh "docker build -t $imageName ."
    } catch (Exception e) {
        error "Docker daemon is not accessible. Please ensure Docker is running: ${e.message}"
    }
}
