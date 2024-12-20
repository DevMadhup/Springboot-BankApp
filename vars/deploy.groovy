def call() {
    try {
        // Validate environment
        sh 'docker compose config -q'
        
         // Graceful shutdown
         sh 'docker compose down --timeout 30'
        
        // Verify cleanup
        sh '''
            if docker compose ps -q | grep -q .; then
                echo "Failed to stop all containers"
                exit 1
            fi
        '''
         
         // Start services
         sh 'docker compose up -d'
        
// Verify deployment
         sh '''
             max_attempts=30
             attempt=1
             while [ $attempt -le $max_attempts ]; do
                unhealthy_services=$(docker compose ps --format '{{.Name}}: {{.Status}}' | grep -v "(healthy)")
                if [ -z "$unhealthy_services" ]; then
                    echo "All services are healthy"
                     exit 0
                else
                    echo "Unhealthy services detected:"
                    echo "$unhealthy_services"
                 fi
                 echo "Waiting for services to be healthy (attempt $attempt/$max_attempts)..."
                 sleep 10
                 attempt=$((attempt + 1))
             done
            echo "Health check timeout after $max_attempts attempts"
            docker compose ps
             exit 1
         '''
    } catch (Exception e) {
        echo "Deployment failed: ${e.message}"
        throw e
    }
}
