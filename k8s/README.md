# Kubernetes Manifests for Travel-Mate Backend

This directory contains Kubernetes manifests for deploying the Travel-Mate backend microservices, databases, and supporting infrastructure. Each service from the docker-compose.yml is represented as a Deployment, Service, and (where needed) ConfigMap, Secret, or PersistentVolumeClaim.

## Structure
- `base/` - Core manifests for all environments
- `overlays/` - Environment-specific overlays (dev, prod, etc.)

## How to Use
1. Apply manifests in the correct order (secrets/configs, storage, databases, core services, ingress).
2. Use `kubectl apply -k k8s/base` for a basic setup.
3. Update overlays for environment-specific configuration.

## Next Steps
- Integrate with CI/CD for automated deployments.
- Add ingress and TLS for production.
- Add resource requests/limits and autoscaling.

---

**Note:** This is a starting point. Adjust resource requests, storage classes, and secrets for your environment.

