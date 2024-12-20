## 1. Horizontal Pod Autoscaler

- Horizontal Pod Autoscaling helps manage traffic coming to your application, by launching new identical pods.
- The Kubernetes Metrics Server collects resource metrics from the kubelet in nodes and exposes those metrics through the Kubernetes API.
- Based on Resource utilization metrics and configuration defined on HPA spec it scale-out (increases the number of identical pods ) and scale-in (decreases the number of identical pods ) your application.

- To work with HPA you need Metrics Server deployed and configured into your cluster.

#### Install the Metrics server using following command

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```
#### Edit deployment metrics-server to work without TLS.

```bash
kubectl edit deployments.apps -n kube-system metrics-server 

# add these two enteries at: spec.template.spec.containers[0].args
# - --kubelet-insecure-tls
# - --kubelet-preferred-address-types=InternalIP,Hostname,ExternalIP

```
#### Verify Installation and apply HPA manifest
```bash
kubectl get pods -n kube-system
kubectl top nodes
```

```bash
kubectl apply -f hpa.yaml
```
#### Create HPA, Generate Load and check newly created Pods.

- Create a pod for generating load and testing
    ```bash
    kubectl run -i --tty load-generator --image=busybox /bin/sh

     while true; do wget -q -O- http://bankapp-service.bankapp-namespace.svc.cluster.local; done
    ```

#### When utilization of resources exceeds the defined value, hpa will create new pods.
```bash
watch -n2 kubectl get pods -n bankapp-namespace
```



## 2. Vertical Pod Autoscaler
- It scale-up and scale down the request of resources (CPU, memory) depending on the workload.
- It will set the resource request automatically based on usage for proper scheduling.
- In the case of limits defined in the template of containers, it also manages a ratio between request and limit. 
- VPA is installed in k8s using Custom Resource Definition object (CRD).

#### Install CRD and apply vpa manifest.

```bash 
kubectl apply -f https://raw.githubusercontent.com/kubernetes/autoscaler/vpa-release-1.0/vertical-pod-autoscaler/deploy/vpa-v1-crd-gen.yaml

kubectl apply -f https://raw.githubusercontent.com/kubernetes/autoscaler/vpa-release-1.0/vertical-pod-autoscaler/deploy/vpa-rbac.yaml
```

Apply VPA configution
```bash
kubectl apply -f vpa.yaml
```

Generate load to application
```bash
kubectl run -i --tty load-generator --image=busybox /bin/sh

while true; do wget -q -O- http://bankapp-service.bankapp-namespace.svc.cluster.local; done
```

Check resource utilization.
```bash
kubectl top nodes

kubectl describe deployment bankapp-deployment -n bankapp-namespace
```

**Note**: To configure Autoscaling resources request, limit must be set
