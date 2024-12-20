# Nginx Ingress Controller 

1)  Apply the Required RBAC (Role-Based Access Control) and Service Account
  
  - Create a service account for the NGINX Ingress Controller and assign it the necessary permissions.
  ```bash
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
  ```
This will create all the necessary resources for the NGINX Ingress Controller, including:
- Service accounts
- ClusterRoles and ClusterRoleBindings
- Deployments
- Services
- ConfigMaps
- Ingress Class
---

2) Verify Ingress Controller Installation
  - After applying the YAML file, verify that the NGINX Ingress Controller is installed and running.
  ```bash
  kubectl get pods -n ingress-nginx
  ```
This should show the ingress-nginx-controller pods running.

---

3) Expose the NGINX Ingress Controller (Optional)
  - If you want to access the Ingress controller externally (e.g., via LoadBalancer), the ingress-nginx-controller service should be of type LoadBalancer.

To modify the service type, you can run:
```bash
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p '{"spec": {"type": "LoadBalancer"}}'
```
This will expose the NGINX Ingress Controller via a LoadBalancer, allowing you to access your applications from outside the cluster.
