# SpringBoot-BankApp Deployment on Kubernetes

This guide explains how to deploy the SpringBoot-BankApp application on a Kubernetes cluster using the provided manifests and configure the NGINX Ingress Controller for routing.

### Prerequisites

- Kubernetes Cluster: Ensure you have a running Kubernetes cluster. KIND, Minikube, or any cloud provider will work.
- kubectl: Installed and configured to interact with your cluster.
- Docker Image: Ensure the SpringBoot-BankApp Docker image is available. 

Update the deployment.yml file to reference the correct image and tag.
Deployment Steps

## 1. Install and Configure the NGINX Ingress Controller

Deploy the NGINX Ingress Controller:

```bash

kubectl apply -f https://kind.sigs.k8s.io/examples/ingress/deploy-ingress-nginx.yaml
```
Verify that the Ingress Controller pods are running:

```bash

kubectl get pods -n ingress-nginx
```
Edit the ValidatingWebhookConfiguration to ignore validation errors temporarily:

```bash

kubectl edit ValidatingWebhookConfiguration ingress-nginx-admission
```
Update the failurePolicy to Ignore:

```yaml

failurePolicy: Ignore
```

## 2. Create a Namespace

Create a dedicated namespace for the application:

```bash

kubectl apply -f namespace.yml
```
## 3. Create ConfigMap

Store non-sensitive configuration like database names and other environment variables:

```bash

kubectl apply -f configMap.yml
```

## 4. Create Secrets
Store sensitive data like database credentials securely:

```bash

kubectl apply -f secrets.yml
```

## 5. Set Up Persistent Storage
Create a PersistentVolume (PV) for MySQL:

```bash

kubectl apply -f persistentVolume.yml
```
Create a PersistentVolumeClaim (PVC) to bind the PV:

```bash

kubectl apply -f persistentVolumeClaim.yml
```

## 6. Deploy MySQL

Deploy MySQL as a StatefulSet for persistent database management:

```bash

kubectl apply -f mysqlStatefulSet.yml
```
Expose MySQL via a Kubernetes Service:

```bash

kubectl apply -f mysqlService.yml
```

## 7. Deploy SpringBoot-BankApp
Deploy the SpringBoot-BankApp using a Deployment:

```bash

kubectl apply -f deployment.yml
```
Expose the application via a Service:

```bash

kubectl apply -f service.yml
```

## 8. Configure Ingress
Deploy the Ingress resource to route traffic to the SpringBoot-BankApp:

```bash

kubectl apply -f ingress.yml
```

Forward port 80 from the Ingress Controller to your localhost:

```bash

kubectl port-forward --namespace ingress-nginx service/ingress-nginx-controller 80:80
```

Update your /etc/hosts file to point bankapp.local to your localhost:

```lua

127.0.0.1 bankapp.local
```
Access the application in your browser at:

```arduino

http://bankapp.local
```
Verification
Check the status of all resources:

```bash

kubectl get all -n bankapp-namespace
```
Verify the logs of the MySQL and SpringBoot-BankApp pods:

```bash

kubectl logs -n bankapp-namespace <pod-name>
```
Cleaning Up
To remove all resources related to the SpringBoot-BankApp:

```bash

kubectl delete namespace bankapp-namespace
kubectl delete -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```
Notes

Ingress Tweaks: The failurePolicy: Ignore setting in the ValidatingWebhookConfiguration ensures smooth ingress creation in local environments like KIND.

Scaling: Scale the BankApp deployment if needed:

```bash

kubectl scale deployment bankapp-deployment --replicas=3 -n bankapp-namespace
```
Monitoring: Use tools like Prometheus or kubectl top to monitor resource usage.

### Configure application to AutoScale [vpa_and_hpa](hpa_vpa.md)
