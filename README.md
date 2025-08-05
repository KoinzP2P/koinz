# KOINZ - Decentralized P2P Exchange

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-green.svg)](https://gradle.org)

KOINZ is a hard fork of Bisq, providing a completely independent decentralized peer-to-peer exchange network for Bitcoin and other cryptocurrencies. It preserves Bisq's proven architecture while operating as a separate network with its own token economy (KNZ).

## 🎯 Key Features

- **Complete Network Independence**: KOINZ operates on Network ID 11, completely isolated from Bisq
- **Decentralized Trading**: No central authority, KYC, or AML requirements
- **Multi-Currency Support**: Trade Bitcoin against fiat currencies and altcoins
- **Built-in Security**: Multi-signature transactions and security deposits
- **Privacy-First**: All communications over Tor network
- **Open Source**: Licensed under AGPL v3

## 🏗️ Architecture Overview

KOINZ maintains Bisq's proven modular architecture:

- **Core Module**: Business logic, trading protocols, dispute resolution
- **P2P Module**: Tor-based networking, peer discovery, message handling
- **Desktop Module**: JavaFX GUI application for end users
- **Assets Module**: Cryptocurrency and fiat currency definitions
- **DAO Module**: Decentralized governance and KNZ token economics
- **API Module**: RESTful API for integrations and headless operation

## 🚀 Quick Start

### Prerequisites

- Java 17+ (OpenJDK recommended)
- Git
- 4GB RAM minimum

### Building from Source

```bash
git clone https://github.com/KoinzP2P/koinz.git
cd koinz
./gradlew build
```

### Running KOINZ Desktop

```bash
./koinz-desktop
```

## 📖 Documentation

- [Build Instructions](docs/build.md)
- [Development Setup](docs/dev-setup.md)
- [API Reference](docs/api-overview.md)
- [Testing Guide](docs/testing.md)
- [Release Process](docs/release-process.md)

## 🌐 Network Information

- **Network ID**: 11 (KOINZ_MAINNET)
- **Token**: KNZ (KOINZ Network Token)
- **P2P Protocol**: Tor-based with hidden services
- **Block Explorer**: Coming soon
- **Seed Nodes**: Managed by KOINZ network

## 🛠️ Development

### Project Structure

```
koinz/
├── core/           # Core business logic
├── p2p/            # P2P networking layer
├── desktop/        # JavaFX desktop application
├── cli/            # Command-line interface
├── daemon/         # Headless daemon
├── assets/         # Asset definitions
├── proto/          # Protocol buffer definitions
├── seednode/       # Network seed nodes
├── inventory/      # Network monitoring
├── restapi/        # REST API service
└── docs/           # Documentation
```

### Building Modules

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :desktop:build

# Run tests
./gradlew test

# Create distribution
./gradlew :desktop:installDist
```

## 🔒 Security

KOINZ inherits Bisq's battle-tested security model:

- Multi-signature Bitcoin transactions
- Security deposits to prevent fraud
- Decentralized arbitration system
- Tor network for privacy
- No central points of failure

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md).

### Development Workflow

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the GNU Affero General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

KOINZ is built upon the foundation of [Bisq](https://bisq.network), the pioneering decentralized Bitcoin exchange. We acknowledge and thank the Bisq contributors for their groundbreaking work in decentralized trading.

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/KoinzP2P/koinz/issues)
- **Discussions**: [GitHub Discussions](https://github.com/KoinzP2P/koinz/discussions)
- **Documentation**: [docs/](docs/)

---

**⚠️ Important**: KOINZ is in active development. This is beta software - use with caution and never risk more than you can afford to lose.
