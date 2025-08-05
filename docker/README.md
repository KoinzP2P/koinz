# KOINZ Docker Deployment

This directory contains Docker configurations for deploying the complete KOINZ stack, preserving Bisq's proven containerization architecture while implementing KOINZ-specific network isolation.

## 🏗️ Architecture Overview

The KOINZ Docker architecture consists of the following services:

- **koinz-bitcoind**: Bitcoin Core blockchain backend
- **koinz-tor**: Tor anonymity network for privacy
- **koinz-seednode**: P2P network seed node (Network ID 11)
- **koinz-desktop**: JavaFX GUI application (optional)
- **koinz-api**: REST API service for integrations
- **koinz-statsnode**: Network statistics collector
- **koinz-inventory**: Network monitoring service

## 🚀 Quick Start

### Prerequisites

- Docker 20.10+ with Compose V2
- 8GB RAM minimum (16GB recommended)
- 500GB disk space for Bitcoin blockchain
- Stable internet connection

### Build Images

```bash
# Build all KOINZ Docker images
./docker/build-all.sh

# Build specific version
./docker/build-all.sh --version 1.0.0

# Build and push to registry
./docker/build-all.sh --push --registry your-registry.com/koinz
```

### Deploy Stack

```bash
# Full stack deployment
docker-compose up -d

# Headless deployment (no desktop GUI)
docker-compose up -d koinz-bitcoind koinz-tor koinz-seednode koinz-api

# Desktop-enabled deployment
docker-compose --profile desktop up -d

# Monitoring stack
docker-compose up -d koinz-statsnode koinz-inventory
```

## 📦 Service Details

### Bitcoin Core (koinz-bitcoind)

- **Image**: `koinz/bitcoin:latest`
- **Ports**: 8332 (RPC), 8333 (P2P)
- **Volume**: `koinz_bitcoin_data`
- **Config**: `docker/bitcoin/bitcoin.conf`

**Features**:
- GPG-verified Bitcoin Core 25.1
- Optimized for KOINZ integration
- Block notification system
- Health checks with automatic restart

### Tor Network (koinz-tor)

- **Image**: `koinz/tor:latest`
- **Ports**: 9050 (SOCKS), 9051 (Control)
- **Volume**: `koinz_tor_data`
- **Config**: `docker/tor/torrc`

**Features**:
- Hidden services for KOINZ P2P
- Privacy-optimized configuration
- Client-only mode (no exit relay)
- Automatic hidden service generation

### KOINZ Seednode (koinz-seednode)

- **Image**: `koinz/seednode:latest`
- **Port**: 8000 (P2P)
- **Volume**: `koinz_seednode_data`
- **Config**: `docker/seednode/koinz.properties`

**Features**:
- Network ID 11 (KOINZ_MAINNET)
- Complete isolation from Bisq network
- Tor integration for privacy
- Health monitoring and auto-restart

### KOINZ Desktop (koinz-desktop)

- **Image**: `koinz/desktop:latest`
- **Port**: 9999 (API)
- **Volume**: `koinz_desktop_data`
- **Profile**: `desktop`

**Features**:
- JavaFX GUI application
- Full trading interface
- Wallet management
- X11 forwarding support for GUI

### KOINZ API (koinz-api)

- **Image**: `koinz/api:latest`
- **Port**: 8080 (HTTP)
- **Volume**: `koinz_api_data`

**Features**:
- RESTful API for integrations
- Swagger documentation
- CORS support
- Health checks

## 🔧 Configuration

### Environment Variables

```bash
# Bitcoin settings
BITCOIN_RPC_USER=koinz
BITCOIN_RPC_PASSWORD=koinz-secure-2024

# Network settings
KOINZ_NETWORK_ID=11
KOINZ_BASE_CURRENCY_NETWORK=BTC_MAINNET

# Memory settings
JDK_JAVA_OPTIONS=-Xms4096M -Xmx4096M -XX:+UseG1GC

# Tor settings
TOR_CONTROL_PASSWORD=koinz-secure-2024
```

### Volume Management

```bash
# List all KOINZ volumes
docker volume ls | grep koinz

# Backup volumes
docker run --rm -v koinz_bitcoin_data:/data -v $(pwd)/backup:/backup alpine tar czf /backup/bitcoin-data.tar.gz -C /data .

# Restore volumes
docker run --rm -v koinz_bitcoin_data:/data -v $(pwd)/backup:/backup alpine tar xzf /backup/bitcoin-data.tar.gz -C /data
```

## 🔍 Monitoring

### Health Checks

```bash
# Check service health
docker-compose ps

# View service logs
docker-compose logs -f koinz-seednode

# Monitor Bitcoin sync
docker-compose exec koinz-bitcoind bitcoin-cli getblockchaininfo
```

### Service URLs

- **Bitcoin RPC**: http://localhost:8332 (user: koinz, pass: koinz-secure-2024)
- **KOINZ API**: http://localhost:8080
- **Statistics**: http://localhost:8181
- **Inventory**: http://localhost:8282

## 🛠️ Development

### Development Mode

```bash
# Override for development
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Enable debug logging
docker-compose exec koinz-seednode sed -i 's/logLevel=INFO/logLevel=DEBUG/' /koinz/seednode/koinz.properties
docker-compose restart koinz-seednode
```

### Building Custom Images

```bash
# Build single service
docker build -f docker/Dockerfile.seednode -t koinz/seednode:dev .

# Test image locally
docker run --rm -it koinz/seednode:dev --help
```

## 🔒 Security

### Security Best Practices

1. **Change default passwords** in production
2. **Use Docker secrets** for sensitive data
3. **Enable firewall** rules for exposed ports
4. **Regular security updates** for base images
5. **Monitor container logs** for suspicious activity

### Production Hardening

```bash
# Create Docker secrets
echo "your-secure-password" | docker secret create bitcoin_rpc_password -
echo "your-tor-control-password" | docker secret create tor_control_password -

# Update docker-compose.yml to use secrets
# See docker-compose.prod.yml for production configuration
```

## 📊 Scaling

### Horizontal Scaling

```bash
# Scale seednode service
docker-compose up -d --scale koinz-seednode=3

# Load balancer configuration
# See docker/nginx/ for load balancer setup
```

### Resource Limits

```yaml
services:
  koinz-seednode:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 4G
        reservations:
          cpus: '1.0'
          memory: 2G
```

## 🆘 Troubleshooting

### Common Issues

1. **Bitcoin sync takes time**: Initial sync can take 24-48 hours
2. **Tor connection issues**: Check firewall and DNS settings
3. **Memory issues**: Increase JVM heap size via `JDK_JAVA_OPTIONS`
4. **Network connectivity**: Verify Docker network configuration

### Debug Commands

```bash
# Check container resources
docker stats

# Inspect network
docker network inspect koinz_koinz-network

# Debug service connectivity
docker-compose exec koinz-seednode nc -zv koinz-bitcoind 8332
docker-compose exec koinz-seednode nc -zv koinz-tor 9050
```

## 📝 License

This Docker configuration is part of the KOINZ project and is licensed under AGPL-3.0.

---

**⚠️ Important**: This is beta software. Use with caution in production environments.