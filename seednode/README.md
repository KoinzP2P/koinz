# KOINZ Seed Node

## Hardware

Highly recommended to use SSD! Minimum specs:

* CPU: 4 cores
* RAM: 8 GB
* SSD: 512 GB (HDD is too slow)

## Software

The following OS's are known to work well:

* Ubuntu 22.04
* Ubuntu 24.04

### Installation

Start with a clean Ubuntu server installation, and run the script
```bash
curl -s https://raw.githubusercontent.com/koinz-network/koinz/master/seednode/install_seednode_debian.sh | sudo bash
```

This will install and configure Tor, Bitcoin, and KOINZ-Seednode services to start on boot.

### Firewall

Next, configure your OS firewall to only allow SSH and Bitcoin P2P
```bash
ufw allow 22/tcp
ufw allow 8333/tcp
ufw enable
```

### Syncing

After installation, watch the Bitcoin blockchain sync progress
```bash
sudo tail -f /bitcoin/debug.log
```

After Bitcoin is fully synced, start the koinz service
```bash
sudo systemctl start koinz
sudo journalctl --unit koinz --follow
```

After KOINZ is fully synced, check your Bitcoin and KOINZ onion hostnames:
```bash
sudo -H -u bitcoin bitcoin-cli getnetworkinfo|grep address
sudo cat /koinz/koinz-seednode/btc_mainnet/tor/hiddenservice/hostname
```

### Testing

After your KOINZ seednode is ready, test it by connecting to your new btcnode and koinz!

macOS:
```bash
/Applications/KOINZ.app/Contents/MacOS/KOINZ --seedNodes=foo.onion:8000 --btcNodes=foo.onion:8333
```

### Monitoring

If you run a main seednode, you also are obliged to activate the monitoring feed by running

```bash
bash <(curl -s https://raw.githubusercontent.com/koinz-network/koinz-monitor/refs/heads/main/scripts/install_collectd_debian.sh)
```
Follow the instruction given by the script and report your public key to the [KOINZ monitoring maintainer](https://github.com/koinz-network/roles/issues/10)!

### Upgrading

To upgrade your seednode to a new tag, for example v1.2.5
```bash
sudo -u koinz -s
cd koinz
git fetch origin
git checkout v1.2.5 # new tag
./gradlew clean build -x test
exit
sudo service koinz restart
sudo journalctl --unit koinz --follow
```

### Uninstall

If you need to start over, you can run the uninstall script in this repo
```bash
sudo ./delete_seednode_debian.sh
```
WARNING: this script will delete all data!

