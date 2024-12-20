## Configure nginx as proxy server, Domain Mapping and enable SSL encryption for HTTPS.
---


![Login diagram](images/login_https.png)

### Configure nginx on your EC2.
- Install nginx
   ```bash
   sudo apt update             
   ```
-  Start nginx 
   ```bash
   sudo systemctl start nginx
   ```

- enable nginx 
    ```bash
    sudo systemctl enable nginx
    ```

- Create a new Nginx server block configuration file for our bank-app application
    ```bash
    sudo touch /etc/nginx/sites-available/bank-app
    ```

- Configure server: add the following code
   ```bash
    server {
        listen 80;
        server_name bank.joakim.online;

        # Security headers
        add_header X-Content-Type-Options nosniff;
        add_header X-Frame-Options "SAMEORIGIN";
        add_header X-XSS-Protection "1; mode=block";
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
        # Buffer size optimizations
        client_body_buffer_size 10K;
        client_header_buffer_size 1k;
        client_max_body_size 8m;
    
        # Timeouts
        client_body_timeout 12;
        client_header_timeout 12;
    
        # Rate limiting
        limit_req_zone $binary_remote_addr zone=one:10m rate=1r/s;

        location / {
            proxy_pass http://localhost:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # Apply rate limiting
            limit_req zone=one burst=5 nodelay;
    
            # Error handling
            proxy_intercept_errors on;
            error_page 500 502 503 504 /50x.html;
        }

        location ~ /\. {
            deny all;
        }
    
        # Error page
        location = /50x.html {
            root /usr/share/nginx/html;
            internal;
        }
    }
    ```

- Create a symbolic link to the configuration file in the sites-enabled directory:
    ```bash
    sudo ln -s /etc/nginx/sites-available/bank-app /etc/nginx/sites-enabled/
    ```
- Test Nginx Configuration and restart nginx if test is successful
    ```bash
    sudo nginx -t 
    sudo systemctl restart nginx
    ```
- SSL certificate to the Domain.
    ```bash
    sudo apt install python3-certbot-nginx
    ```
    ```bash
    certbot --version
    ```
    ```bash
    certbot --nginx -d bank.joakim.online
    ```
- Verify SSL setup:
    1. Check SSL certificate: 
        ```bash
        curl -vI https://bank.joakim.online
        # Success: Look for "SSL certificate verify ok" and "HTTP/2 200"
        # If failed: Check certificate path and permissions
        ```
    2. Verify automatic renewal:
        ```bash
        sudo certbot renew --dry-run
        # Success: Look for "Congratulations, all renewals succeeded"
        # If failed: Check certbot logs at /var/log/letsencrypt/letsencrypt.log
        ```


    3. Test HTTPS redirect:
       ```bash
       curl -I http://bank.joakim.online
       # Success: Look for "301 Moved Permanently" and "Location: https://"
       # If failed: Check Nginx configuration for proper redirect rules
       ```
       
- Configure SSL parameters
    ```bash
    # Add to server block
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ```

- Backup SSL certificates
    ```bash
    sudo cp -r /etc/letsencrypt/live/bank.joakim.online /ssl/backup
    sudo cp -r /etc/letsencrypt/archive/bank.joakim.online /ssl/backup
    ```

- Set up automatic certificate renewal
    ```bash
    # Add to crontab
    echo "0 0 1 * * certbot renew --quiet" | sudo tee -a /etc/crontab
    ```
