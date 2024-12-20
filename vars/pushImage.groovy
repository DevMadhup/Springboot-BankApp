def call(String imageName){
    withCredentials([usernamePassword(credentialsId: "dockerhub", passwordVariable: 'DOCKER_PASSWD', usernameVariable: 'DOCKER_USER')]) {
                    sh "docker tag ${imageName} ${DOCKER_USER}/springboot-application"
                    sh "docker login -u${DOCKER_USER} -p${DOCKER_PASSWD}"
                    sh "docker push ${DOCKER_USER}/${imageName}"
                }
}
